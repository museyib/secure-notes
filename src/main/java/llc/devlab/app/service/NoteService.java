package llc.devlab.app.service;

import jakarta.transaction.Transactional;
import llc.devlab.app.audit.AuditAction;
import llc.devlab.app.audit.Auditable;
import llc.devlab.app.audit.RiskLevel;
import llc.devlab.app.exception.ConflictException;
import llc.devlab.app.model.dto.NoteDto;
import llc.devlab.app.model.dto.NoteRequest;
import llc.devlab.app.model.dto.NoteShareRequest;
import llc.devlab.app.model.entity.Note;
import llc.devlab.app.exception.ResourceNotFoundException;
import llc.devlab.app.mapper.NoteMapper;
import llc.devlab.app.model.entity.NoteShare;
import llc.devlab.app.repository.NoteRepository;
import llc.devlab.app.security.model.entity.AppUser;
import llc.devlab.app.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final UserRepository userRepository;

    @Transactional
    public NoteDto createNote(NoteRequest request) {
        Note note = noteMapper.toEntity(request);
        Note saved = noteRepository.save(note);
        return noteMapper.toDto(saved);
    }

    @Transactional
    public NoteDto updateNote(Long id, NoteRequest request) {
        Note note = getNoteByIdAndUsername(id);

        note.setTitle(request.title());
        note.setContent(request.content());
        Note saved = noteRepository.save(note);
        return noteMapper.toDto(saved);
    }

    public List<NoteDto> getNotesByUser(int page, int size, String contentSearch, String username) {
        List<Note> notes = noteRepository.findAllFiltered(PageRequest.of(page, size), username);
        Stream<NoteDto> stream = notes.stream().filter(note -> !note.isDeleted()).map(noteMapper::toDto);
        if (!isEmpty(contentSearch)) {
            stream = stream.filter(noteDto -> noteDto.content().toLowerCase().contains(contentSearch.toLowerCase()));
        }

        return stream.sorted(Comparator.comparing(NoteDto::id).reversed()).collect(Collectors.toList());
    }

    public NoteDto getNote(Long id) {
        Note note = getNoteByIdAndUsername(id);
        return noteMapper.toDto(note);
    }

    @Transactional
    @Auditable(targetId = "#id", targetType = "Note", riskLevel = RiskLevel.HIGH, action = AuditAction.DELETE)
    public void deleteNote(Long id) {
        Note note = getNoteByIdAndUsername(id);
        note.setDeleted(true);
    }

    @Transactional
    @Auditable(targetId = "#id", targetType = "Note", riskLevel = RiskLevel.MEDIUM, action = AuditAction.SHARE)
    public void shareNote(Long id, String ownerEmail, NoteShareRequest request) {
        if (ownerEmail.equals(request.sharedUserEmail()))
            throw new ConflictException("Owner cannot revoke access to themselves");

        Note note = getNoteByIdAndUsername(id);
        AppUser sharedUser = userRepository.findByEmailIgnoreCase(request.sharedUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this email: " + request.sharedUserEmail()));
        NoteShare noteShare = noteRepository.findBySharedUserEmail(id, sharedUser.getEmail()).orElse(new NoteShare());
        noteShare.setNote(note);
        noteShare.setSharedUser(sharedUser);
        noteShare.setShareMode(request.shareMode());

        note.getNoteShares().add(noteShare);
        noteRepository.save(note);
    }

    @Transactional
    @Auditable(targetId = "#id", targetType = "Note", riskLevel = RiskLevel.LOW, action = AuditAction.REVOKE)
    public void revokeNoteShareAccess(Long id, String ownerEmail, String sharedUserEmail) {
        if (ownerEmail.equals(sharedUserEmail))
            throw new ConflictException("Owner cannot revoke access to themselves");

        Note note = getNoteByIdAndUsername(id);

        Optional<NoteShare> noteShareOptional = note.getNoteShares().stream()
                .filter(ns -> ns.getSharedUser().getEmail().equals(sharedUserEmail))
                .findFirst();

        if (noteShareOptional.isPresent()) {
            NoteShare noteShare = noteShareOptional.get();
            note.getNoteShares().remove(noteShare);
            noteRepository.save(note);
        }
    }

    private Note getNoteByIdAndUsername(Long id) {
        return noteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
    }
}

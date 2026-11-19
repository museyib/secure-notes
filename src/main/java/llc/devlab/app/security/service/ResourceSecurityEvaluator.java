package llc.devlab.app.security.service;

import llc.devlab.app.model.entity.Note;
import llc.devlab.app.repository.NoteRepository;
import llc.devlab.app.security.model.enums.ShareMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("resourceSecurity")
@RequiredArgsConstructor
public class ResourceSecurityEvaluator {

    private final NoteRepository noteRepository;

    public boolean hasAccess(Long noteId, String email, ShareMode shareMode) {
        Note note = noteRepository.findById(noteId).orElse(null);
        List<String> sharedUserEmails = noteRepository.findAllSharedUsersByMode(noteId, shareMode);
        return note != null && (note.getCreatedBy().equals(email) || sharedUserEmails.contains(email));
    }

    public boolean isOwner(Long noteId, String email) {
        return noteRepository.findById(noteId)
                .map(note -> note.getCreatedBy().equals(email)).orElse(false);
    }
}

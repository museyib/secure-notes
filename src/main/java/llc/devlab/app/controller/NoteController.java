package llc.devlab.app.controller;

import jakarta.validation.Valid;
import llc.devlab.app.model.dto.NoteDto;
import llc.devlab.app.model.dto.NoteRequest;
import llc.devlab.app.model.dto.NoteShareRequest;
import llc.devlab.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDto>> getNotes(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false, name = "q") String contentSearch,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.getNotesByUser(page, size, contentSearch, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.hasAccess(#id, authentication.principal.username, T(llc.devlab.app.security.model.enums.ShareMode).READ)")
    public ResponseEntity<NoteDto> getNote(@PathVariable long id) {
        return ResponseEntity.ok(noteService.getNote(id));
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(@RequestBody NoteRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(noteService.createNote(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@resourceSecurity.hasAccess(#id, authentication.principal.username, T(llc.devlab.app.security.model.enums.ShareMode).EDIT)")
    public ResponseEntity<NoteDto> updateNote(@PathVariable long id, @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.updateNote(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@resourceSecurity.hasAccess(#id, authentication.principal.username, T(llc.devlab.app.security.model.enums.ShareMode).EDIT)")
    public ResponseEntity<Void> deleteNote(@PathVariable long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/share")
    @PreAuthorize("@resourceSecurity.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<Void> shareNote(@PathVariable long id,
                                          @Valid @RequestBody NoteShareRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        noteService.shareNote(id, userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/revoke")
    @PreAuthorize("@resourceSecurity.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<Void> revokeNoteShareAccess(@PathVariable long id,
                                                       @RequestBody NoteShareRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        noteService.revokeNoteShareAccess(id, userDetails.getUsername(), request.sharedUserEmail());
        return ResponseEntity.noContent().build();
    }

}

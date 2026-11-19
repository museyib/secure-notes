package llc.devlab.app.mapper;

import llc.devlab.app.model.dto.NoteDto;
import llc.devlab.app.model.dto.NoteRequest;
import llc.devlab.app.model.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public Note toEntity(NoteRequest request) {
        Note note = new Note();
        note.setTitle(request.title());
        note.setContent(request.content());
        return note;
    }

    public NoteDto toDto(Note note) {
        return new NoteDto(note.getId(), note.getTitle(), note.getContent());
    }
}

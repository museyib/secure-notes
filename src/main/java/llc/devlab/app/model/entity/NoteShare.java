package llc.devlab.app.model.entity;

import jakarta.persistence.*;
import llc.devlab.app.security.model.entity.AppUser;
import llc.devlab.app.security.model.enums.ShareMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "note_share", uniqueConstraints = @UniqueConstraint(columnNames = {"note_id", "user_id"}))
public class NoteShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser sharedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareMode shareMode;
}
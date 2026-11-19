package llc.devlab.app.model.entity;

import jakarta.persistence.*;
import llc.devlab.app.cryptography.NoteConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
public class Note extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Convert(converter = NoteConverter.class)
    private String title;

    @Convert(converter = NoteConverter.class)
    private String content;

    private boolean deleted;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NoteShare> noteShares;
}

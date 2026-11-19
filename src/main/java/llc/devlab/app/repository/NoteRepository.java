package llc.devlab.app.repository;

import llc.devlab.app.model.entity.Note;
import llc.devlab.app.model.entity.NoteShare;
import llc.devlab.app.security.model.enums.ShareMode;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"noteShares"})
    Optional<Note> findById(@NonNull Long id);

    @Query("SELECT DISTINCT ns.sharedUser.email FROM Note n JOIN n.noteShares ns WHERE n.id = :id AND ns.shareMode = :shareMode")
    List<String> findAllSharedUsersByMode(@Param("id") Long id, @Param("shareMode") ShareMode shareMode);

    @Query("SELECT DISTINCT ns FROM Note n JOIN n.noteShares ns WHERE n.id = :id AND ns.sharedUser.email = :sharedUserEmail")
    Optional<NoteShare> findBySharedUserEmail(@Param("id") Long id, @Param("sharedUserEmail") String sharedUserEmail);

    @Query("SELECT n FROM Note n WHERE n.createdBy = :username")
    List<Note> findAllFiltered(Pageable pageable,
                               @Param("username") String username);

    Optional<Note> findByIdAndDeletedFalse(Long id);
}

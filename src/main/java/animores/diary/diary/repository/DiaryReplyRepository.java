package animores.diary.diary.repository;

import animores.diary.diary.entity.DiaryReply;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryReplyRepository extends JpaRepository<DiaryReply, Long> {

    Optional<DiaryReply> findByIdAndDeletedDtIsNull(Long id);

    Integer countByDiaryCommentIdAndDeletedDtIsNull(Long commentId);
}

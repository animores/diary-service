package animores.diary.diary.repository.impl;

import static animores.diary.diary.entity.QDiaryComment.diaryComment;
import static animores.diary.diary.entity.QDiaryReply.diaryReply;
import static animores.diary.profile.entity.QProfile.profile;

import animores.diary.diary.dao.GetAllDiaryCommentDao;
import animores.diary.diary.dao.GetAllDiaryReplyDao;
import animores.diary.diary.repository.DiaryCommentCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class DiaryCommentCustomRepositoryImpl implements DiaryCommentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GetAllDiaryCommentDao> getAllDiaryComment(Long diaryId,
        int page, int size) {
        return jpaQueryFactory.select(
                Projections.fields(GetAllDiaryCommentDao.class,
                    diaryComment.id.as("commentId"),
                    diaryComment.content,
                    diaryComment.createdAt,
                    profile.id.as("profileId"),
                    profile.name,
                    profile.imageUrl,
                    diaryReply.count().as("replyCount")
                )
            )
            .from(diaryComment)
            .leftJoin(profile)
            .on(profile.id.eq(diaryComment.profile.id))
            .leftJoin(diaryReply)
            .on(diaryComment.id.eq(diaryReply.diaryComment.id).and(diaryReply.deletedDt.isNull()))
            .where(diaryComment.diary.id.eq(diaryId))
            .groupBy(diaryComment.id)
            .orderBy(diaryComment.id.desc())
            .offset((long) (page - 1) * size)
            .limit(size)
            .fetch();
    }

    @Override
    public List<GetAllDiaryReplyDao> getAllDiaryReply(Long commentId, int page, int size) {
        return jpaQueryFactory.select(
                Projections.fields(GetAllDiaryReplyDao.class,
                    diaryComment.id.as("replyId"),
                    diaryComment.content,
                    diaryComment.createdAt,
                    profile.id.as("profileId"),
                    profile.name,
                    profile.imageUrl
                )
            )
            .from(diaryReply)
            .leftJoin(profile)
            .on(profile.id.eq(diaryReply.profile.id))
            .where(diaryReply.diaryComment.id.eq(commentId))
            .orderBy(diaryReply.id.desc())
            .offset((long) (page - 1) * size)
            .limit(size)
            .fetch();
    }

    @Override
    public Long getAllDiaryCommentCount(Long diaryId) {
        return jpaQueryFactory
            .select(diaryComment.count())
            .from(diaryComment)
            .where(diaryComment.diary.id.eq(diaryId))
            .fetchOne();
    }

}

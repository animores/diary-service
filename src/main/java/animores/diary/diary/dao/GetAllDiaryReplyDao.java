package animores.diary.diary.dao;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class GetAllDiaryReplyDao {

    private Long replyId;
    private String content;
    private LocalDateTime createdAt;

    private Long profileId;
    private String name;
    private String imageUrl;
}

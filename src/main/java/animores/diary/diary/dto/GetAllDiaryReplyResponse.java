package animores.diary.diary.dto;

import animores.diary.diary.dao.GetAllDiaryReplyDao;
import java.util.List;
import lombok.Getter;

@Getter
public class GetAllDiaryReplyResponse {

    private long totalCount;

    private List<GetAllDiaryReplyDao> replies;

    public GetAllDiaryReplyResponse(long totalCount, List<GetAllDiaryReplyDao> replies) {
        this.totalCount = totalCount;
        this.replies = replies;
    }
}

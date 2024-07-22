package animores.diary.diary.service;

import animores.diary.account.entity.Account;
import animores.diary.diary.dto.AddDiaryCommentRequest;
import animores.diary.diary.dto.EditDiaryCommentRequest;
import animores.diary.diary.dto.GetAllDiaryReplyResponse;
import animores.diary.diary.dto.RemoveDiaryCommentRequest;

public interface DiaryCommentService {

    public void addDiaryComment(Account account, AddDiaryCommentRequest request);

    public void editDiaryComment(Account account, Long commentId, EditDiaryCommentRequest request);

    public void removeDiaryComment(Account account, Long commentId,
        RemoveDiaryCommentRequest request);

    public GetAllDiaryReplyResponse getAllDiaryReply(Account account, Long commentId,
        Long profileId, int page, int size);

}

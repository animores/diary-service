package animores.diary.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 삭제 요청")
public record RemoveDiaryCommentRequest(
    @Schema(description = "프로필 아이디", example = "1")
    Long profileId
) {

}

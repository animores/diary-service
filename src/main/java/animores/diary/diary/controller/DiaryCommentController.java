package animores.diary.diary.controller;

import animores.diary.account.entity.Account;
import animores.diary.account.service.AccountService;
import animores.diary.common.Response;
import animores.diary.common.aop.UserInfo;
import animores.diary.diary.dto.AddDiaryCommentRequest;
import animores.diary.diary.dto.EditDiaryCommentRequest;
import animores.diary.diary.dto.GetAllDiaryReplyResponse;
import animores.diary.diary.dto.RemoveDiaryCommentRequest;
import animores.diary.diary.service.DiaryCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//@PreAuthorize("hasAuthority('USER')")
//@SecurityRequirement(name = "Authorization")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diary-comments")
public class DiaryCommentController {

    private final AccountService accountService;
    private final DiaryCommentService diaryCommentService;

    @UserInfo
    @PostMapping("")
    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    public Response<Void> addDiaryComment(
        @RequestBody @Parameter(description = "댓글 작성에 대한 요청 데이터", required = true) AddDiaryCommentRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryCommentService.addDiaryComment(account, request);
        return Response.success(null);
    }

    @UserInfo
    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    public Response<Void> editDiaryComment(
        @PathVariable @Parameter(description = "댓글 아이디", required = true) Long commentId,
        @RequestBody @Parameter(description = "댓글 수정에 대한 요청 데이터", required = true) EditDiaryCommentRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryCommentService.editDiaryComment(account, commentId, request);
        return Response.success(null);
    }

    @UserInfo
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public Response<Void> removeDiaryComment(
        @PathVariable @Parameter(description = "댓글 아이디", required = true) Long commentId,
        @RequestBody @Parameter(description = "댓글 삭제에 대한 요청 데이터", required = true) RemoveDiaryCommentRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryCommentService.removeDiaryComment(account, commentId, request);
        return Response.success(null);
    }

    @UserInfo
    @GetMapping("{commentId}/replies")
    @Operation(summary = "대댓글 목록 조회", description = "일지에 대한 대댓글 목록을 조회합니다.")
    public GetAllDiaryReplyResponse getAllDiaryReply(
        @PathVariable @Parameter(description = "대댓글 아이디", required = true) Long commentId,
        @RequestParam("profileId") @Parameter(description = "프로필 아이디", required = true, example = "1") Long profileId,
        @RequestParam("page") @Parameter(description = "페이지 번호 (1부터 시작)", required = true, example = "1") int page,
        @RequestParam("size") @Parameter(description = "페이지별 개수", required = true, example = "15") int size
    ) {
        Account account = accountService.getAccountFromContext();
        return diaryCommentService.getAllDiaryReply(account, commentId, profileId, page, size);
    }

}

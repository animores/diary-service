package animores.diary.diary.controller;

import animores.diary.account.entity.Account;
import animores.diary.account.service.AccountService;
import animores.diary.common.Response;
import animores.diary.common.aop.UserInfo;
import animores.diary.diary.dto.*;
import animores.diary.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

//@PreAuthorize("hasAuthority('USER')")
//@SecurityRequirement(name = "Authorization")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diaries")
public class DiaryController {

    private final AccountService accountService;
    private final DiaryService diaryService;

    @UserInfo
    @GetMapping("")
    @Operation(summary = "일지 목록 조회", description = "일지 목록을 조회합니다.")
    public Response<GetAllDiaryResponse> getAllDiary(
        @RequestParam("profileId") @Parameter(description = "프로필 아이디", required = true, example = "1") Long profileId,
        @RequestParam("page") @Parameter(description = "페이지 번호 (1부터 시작)", required = true, example = "1") int page,
        @RequestParam("size") @Parameter(description = "페이지별 개수", required = true, example = "15") int size) {
        Account account = accountService.getAccountFromContext();
        return Response.success(diaryService.getAllDiary(account, profileId, page, size));
    }

    @UserInfo
    @GetMapping("/calendar")
    @Operation(summary = "일지 캘린더 목록 조회 (개발중)", description = "캘린더의 일지 목록을 조회합니다.")
    public Response<GetCalendarDiaryResponse> getCalendarDiary(
        @RequestParam("profileId") @Parameter(description = "프로필 아이디", required = true, example = "1") Long profileId,
        @RequestParam("date") LocalDate date) {
        Account account = accountService.getAccountFromContext();
        return Response.success(diaryService.getCalendarDiary(account, profileId, date));
    }

    @UserInfo
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "일지 생성", description = "일지를 생성합니다.")
    public Response<Void> addDiary(
        @RequestPart(name = "request") @Parameter(description = "일지 생성에 대한 요청 데이터", required = true) AddDiaryRequest request,
        @RequestPart(name = "files", required = false) @Parameter(description = "업로드할 파일들", required = false) List<MultipartFile> files)
        throws IOException {
        Account account = accountService.getAccountFromContext();
        diaryService.addDiary(account, request, files);
        return Response.success(null);
    }

    @UserInfo
    @PatchMapping("/{diaryId}")
    @Operation(summary = "일지 내용 수정", description = "일지 내용을 수정합니다.")
    public Response<Void> editDiaryContent(
        @PathVariable @Parameter(description = "일지 아이디", required = true) Long diaryId,
        @RequestBody @Parameter(description = "일지 내용 수정에 대한 요청 데이터", required = true) EditDiaryContentRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryService.editDiaryContent(account, diaryId, request);
        return Response.success(null);
    }

    @UserInfo
    @PostMapping(value = "/{diaryId}/diary-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "일지 미디어 추가", description = "일지의 사진 및 영상파일을 추가합니다.")
    public Response<Void> addDiaryMedia(
        @PathVariable @Parameter(description = "일지 아이디", required = true) Long diaryId,
        @RequestPart @Parameter(description = "일지 미디어 추가에 대한 요청 데이터", required = true) AddDiaryMediaRequest request,
        @RequestPart(name = "files") @Parameter(description = "업로드할 파일들", required = true) List<MultipartFile> files)
        throws IOException {
        Account account = accountService.getAccountFromContext();
        diaryService.addDiaryMedia(account, diaryId, request, files);
        return Response.success(null);
    }

    @UserInfo
    @PutMapping(value = "/{diaryId}/diary-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "일지 미디어 수정", description = "일지의 사진 및 영상파일을 추가 및 삭제합니다.")
    public Response<Void> editDiaryMedia(
        @PathVariable @Parameter(description = "일지 아이디", required = true) Long diaryId,
        @RequestPart @Parameter(description = "일지 미디어 수정에 대한 요청 데이터", required = true) EditDiaryMediaRequest request,
        @RequestPart(name = "files") @Parameter(description = "업로드할 파일들", required = true) List<MultipartFile> files)
        throws IOException {
        Account account = accountService.getAccountFromContext();
        diaryService.editDiaryMedia(account, diaryId, request, files);
        return Response.success(null);
    }

    @UserInfo
    @DeleteMapping("/{diaryId}/diary-media")
    @Operation(summary = "일지 미디어 삭제", description = "일지의 사진 및 영상파일을 삭제합니다.")
    public Response<Void> removeDiaryMedia(
        @PathVariable @Parameter(description = "일지 아이디", required = true) Long diaryId,
        @RequestBody @Parameter(description = "일지 미디어 삭제에 대한 요청 데이터", required = true) EditDiaryMediaRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryService.removeDiaryMedia(account, diaryId, request);
        return Response.success(null);
    }

    @UserInfo
    @DeleteMapping("/{diaryId}")
    @Operation(summary = "일지 삭제", description = "일지를 삭제합니다.")
    public Response<Void> removeDiary(@PathVariable Long diaryId,
        @RequestBody RemoveDiaryRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryService.removeDiary(account, diaryId, request);
        return Response.success(null);
    }

    @UserInfo
    @PostMapping("/{diaryId}/likes")
    @Operation(summary = "일지 좋아요", description = "일지 좋아요를 등록합니다.")
    public Response<Void> addDiaryLike(
        @PathVariable @Parameter(description = "일지 아이디", required = true) Long diaryId,
        @RequestBody @Parameter(description = "일지 좋아요에 대한 요청 데이터", required = true) AddDiaryLikeRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryService.addDiaryLike(account, diaryId, request);
        return Response.success(null);
    }

    @UserInfo
    @DeleteMapping("/{diaryId}/likes")
    @Operation(summary = "일지 좋아요 취소", description = "일지 좋아요를 취소합니다.")
    public Response<Void> cancelDiaryLike(
        @PathVariable @Parameter(description = "일지 아이디", required = true) Long diaryId,
        @RequestBody @Parameter(description = "일지 좋아요 취소에 대한 요청 데이터", required = true) CancelDiaryLikeRequest request) {
        Account account = accountService.getAccountFromContext();
        diaryService.cancelDiaryLike(account, diaryId, request);
        return Response.success(null);
    }

    @UserInfo
    @GetMapping("{diaryId}/comments")
    @Operation(summary = "댓글 목록 조회", description = "일지에 대한 댓글 목록을 조회합니다.")
    public GetAllDiaryCommentResponse getAllDiaryComment(
        @PathVariable @Parameter(description = "일지 아이디", required = true) Long diaryId,
        @RequestParam("profileId") @Parameter(description = "프로필 아이디", required = true, example = "1") Long profileId,
        @RequestParam("page") @Parameter(description = "페이지 번호 (1부터 시작)", required = true, example = "1") int page,
        @RequestParam("size") @Parameter(description = "페이지별 개수", required = true, example = "15") int size
    ) {
        Account account = accountService.getAccountFromContext();
        return diaryService.getAllDiaryComment(account, diaryId, profileId, page, size);
    }
}

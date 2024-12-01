package animores.diary.diary.service.impl;

import animores.diary.account.entity.Account;
import animores.diary.common.exception.CustomException;
import animores.diary.common.exception.ExceptionCode;
import animores.diary.common.service.AuthorizationService;
import animores.diary.common.service.S3Service;
import animores.diary.diary.dao.GetAllDiaryCommentDao;
import animores.diary.diary.dao.GetAllDiaryDao;
import animores.diary.diary.dao.GetCalendarDiaryDao;
import animores.diary.diary.dto.*;
import animores.diary.diary.entity.Diary;
import animores.diary.diary.entity.DiaryLike;
import animores.diary.diary.entity.DiaryMedia;
import animores.diary.diary.entity.DiaryMediaType;
import animores.diary.diary.repository.*;
import animores.diary.diary.service.DiaryService;
import animores.diary.profile.entity.Profile;
import animores.diary.profile.repository.ProfileRepository;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static animores.diary.common.S3Path.DIARY_PATH;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final S3Service s3Service;
    private final ProfileRepository profileRepository;
    private final AuthorizationService authorizationService;
    private final DiaryRepository diaryRepository;
    private final DiaryCustomRepository diaryCustomRepository;
    private final DiaryMediaRepository diaryMediaRepository;
    private final DiaryMediaCustomRepository diaryMediaCustomRepository;
    private final DiaryLikeRepository diaryLikeRepository;
    private final DiaryCommentCustomRepository diaryCommentCustomRepository;

    @Override
    @Transactional(readOnly = true)
    public GetAllDiaryResponse getAllDiary(Account account, Long profileId, int page, int size) {
        Profile profile = findProfileById(profileId);
        authorizationService.validateProfileAccess(account, profile);

        List<GetAllDiaryDao> diaries = diaryCustomRepository.getAllDiary(account.getId(), profileId,
            page, size);
        Long totalCount = diaryCustomRepository.getAllDiaryCount(account.getId());

        return new GetAllDiaryResponse(totalCount, diaries);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCalendarDiaryResponse getCalendarDiary(Account account, Long profileId,
                                                     LocalDate date) {
        Profile profile = findProfileById(profileId);
        authorizationService.validateProfileAccess(account, profile);

        QueryResults<GetCalendarDiaryDao> diaries = diaryCustomRepository.getCalendarDiary(
            account.getId(), date);

        return new GetCalendarDiaryResponse(diaries.getTotal(), diaries.getResults());
    }

    @Override
    @Transactional
    public void addDiary(Account account, AddDiaryRequest request, List<MultipartFile> files)
        throws IOException {
        Profile profile = findProfileById(request.profileId());
        authorizationService.validateProfileAccess(account, profile);

        // files에서 "files"만 넘어오고 파일은 안담겨서 넘어왔을 경우 에러 처리 필요

        Diary diary = diaryRepository.save(Diary.create(account, profile, request.content()));

        if (files != null) {
            List<String> fileNames = files.stream()
                .map(file -> UUID.randomUUID().toString())
                .toList();

            s3Service.uploadFilesToS3(files, DIARY_PATH, fileNames);
            List<DiaryMedia> diaryMedias = createDiaryMedias(diary, fileNames, files);
            diaryMediaRepository.saveAll(diaryMedias);
        }

    }

    @Override
    @Transactional
    public void editDiaryContent(Account account, Long diaryId, EditDiaryContentRequest request) {
        Profile profile = findProfileById(request.profileId());
        Diary diary = findDiaryById(diaryId);

        authorizationService.validateProfileAccess(account, profile);
        authorizationService.validateDiaryAccess(diary, profile);

        diary.updateContent(request.content());
    }

    @Transactional
    @Override
    public void addDiaryMedia(Account account, Long diaryId, AddDiaryMediaRequest request,
        List<MultipartFile> files) throws IOException {
        Profile profile = findProfileById(request.profileId());
        Diary diary = findDiaryById(diaryId);

        authorizationService.validateProfileAccess(account, profile);
        authorizationService.validateDiaryAccess(diary, profile);

        List<String> fileNames = files.stream()
            .map(file -> UUID.randomUUID().toString())
            .toList();
        s3Service.uploadFilesToS3(files, DIARY_PATH, fileNames);
        diaryMediaRepository.saveAll(createDiaryMedias(diary, fileNames, files));
        reorderDiaryMedia(diary.getId(), DiaryMediaType.I);
    }

    @Override
    @Transactional
    public void editDiaryMedia(Account account, Long diaryId, EditDiaryMediaRequest request,
        List<MultipartFile> files) throws IOException {
        Profile profile = findProfileById(request.profileId());
        Diary diary = findDiaryById(diaryId);

        authorizationService.validateProfileAccess(account, profile);
        authorizationService.validateDiaryAccess(diary, profile);

        List<DiaryMedia> mediaListToDelete = diaryMediaRepository.findByIdIn(request.mediaIds());
        if (mediaListToDelete.isEmpty()) {
            throw new CustomException(ExceptionCode.NOT_FOUND_DIARY_MEDIA);
        }
        s3Service.removeFilesFromS3(
            mediaListToDelete.stream()
                .map(DiaryMedia::getUrl)
                .toList()
        );
        diaryMediaRepository.deleteAll(mediaListToDelete);

        List<String> fileNames = files.stream()
            .map(file -> UUID.randomUUID().toString())
            .toList();

        s3Service.uploadFilesToS3(files, DIARY_PATH, fileNames);
        diaryMediaRepository.saveAll(createDiaryMedias(diary, fileNames, files));
        reorderDiaryMedia(diary.getId(), DiaryMediaType.I);
    }

    @Override
    @Transactional
    public void removeDiaryMedia(Account account, Long diaryId, EditDiaryMediaRequest request) {
        Profile profile = findProfileById(request.profileId());
        Diary diary = findDiaryById(diaryId);

        authorizationService.validateProfileAccess(account, profile);
        authorizationService.validateDiaryAccess(diary, profile);

        List<DiaryMedia> mediaListToDelete = diaryMediaRepository.findByIdIn(request.mediaIds());
        if (mediaListToDelete.isEmpty()) {
            throw new CustomException(ExceptionCode.NOT_FOUND_DIARY_MEDIA);
        }
        s3Service.removeFilesFromS3(
            mediaListToDelete.stream()
                .map(DiaryMedia::getUrl)
                .toList()
        );
        diaryMediaRepository.deleteAll(mediaListToDelete);

        reorderDiaryMedia(diary.getId(), DiaryMediaType.I);
    }

    @Override
    @Transactional
    public void removeDiary(Account account, Long diaryId, RemoveDiaryRequest request) {
        Profile profile = findProfileById(request.profileId());
        Diary diary = findDiaryById(diaryId);

        authorizationService.validateProfileAccess(account, profile);
        authorizationService.validateDiaryAccess(diary, profile);

        diary.delete();
    }

    @Override
    @Transactional
    public void addDiaryLike(Account account, Long diaryId, AddDiaryLikeRequest request) {
        Profile profile = findProfileById(request.profileId());
        Diary diary = findDiaryById(diaryId);

        authorizationService.validateProfileAccess(account, profile);

        diaryLikeRepository.save(DiaryLike.create(profile, diary));
    }

    @Override
    @Transactional
    public void cancelDiaryLike(Account account, Long diaryId, CancelDiaryLikeRequest request) {
        Profile profile = findProfileById(request.profileId());
        Diary diary = findDiaryById(diaryId);

        authorizationService.validateProfileAccess(account, profile);

        DiaryLike diaryLikeToDelete = diaryLikeRepository.findByDiaryIdAndProfileId(diary.getId(),
                profile.getId())
            .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_DIARY_LIKE));

        diaryLikeRepository.delete(diaryLikeToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public GetAllDiaryCommentResponse getAllDiaryComment(Account account, Long diaryId,
        Long profileId, int page, int size) {
        Profile profile = findProfileById(profileId);
        authorizationService.validateProfileAccess(account, profile);

        List<GetAllDiaryCommentDao> comments = diaryCommentCustomRepository.getAllDiaryComment(
            diaryId, page, size);
        Long totalCount = diaryCommentCustomRepository.getAllDiaryCommentCount(diaryId);

        return new GetAllDiaryCommentResponse(totalCount, comments);
    }

    private Profile findProfileById(Long id) {
        return profileRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Profile not found with id: " + id));
    }

    private Diary findDiaryById(Long id) {
        return diaryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Diary not found with id: " + id));
    }

    private List<DiaryMedia> createDiaryMedias(Diary diary,
        List<String> fileNames, List<MultipartFile> fileList) {
        return IntStream.range(0, fileNames.size())
            .mapToObj(i -> DiaryMedia.create(diary, DIARY_PATH + fileNames.get(i), i,
                    DiaryMediaType.checkType(fileList.get(i).getContentType()))
            ).toList();
    }

    public void reorderDiaryMedia(Long diaryId, DiaryMediaType type) {
        List<DiaryMedia> mediaList = diaryMediaCustomRepository.getAllDiaryMediaToReorder(diaryId,
            type);

        for (int i = 0; i < mediaList.size(); i++) {
            mediaList.get(i).updateMediaOrder(i);
        }
    }

}

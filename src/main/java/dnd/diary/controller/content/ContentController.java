package dnd.diary.controller.content;

import dnd.diary.request.content.ContentDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.ContentResponse;
import dnd.diary.service.content.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    // 피드 작성
    @PostMapping("content")
    public CustomResponseEntity<ContentResponse.Create> contentCreate(
            @AuthenticationPrincipal final Long userId,
            @RequestPart(required = false) final List<MultipartFile> multipartFile,
            @RequestParam final Long groupId,
            @RequestParam final String content,
            @RequestParam(required = false) final Double latitude,
            @RequestParam(required = false) final Double longitude,
            @RequestParam(required = false) final String location
    ) {
        return CustomResponseEntity.success(contentService.createContent(
                userId, multipartFile, groupId,
                content, latitude, longitude, location)
        );
    }

    // 피드 조회
    @GetMapping("content")
    public CustomResponseEntity<ContentResponse.Detail> contentDetail(
            @AuthenticationPrincipal final Long userId,
            @RequestParam final Long contentId
    ) {
        return CustomResponseEntity.success(contentService.detailContent(userId, contentId));
    }

    // 그룹 피드 리스트 조회
    @GetMapping("content/group")
    public CustomResponseEntity<Page<ContentResponse.GroupPage>> contentGroupList(
            @AuthenticationPrincipal final Long userId,
            @RequestParam final Long groupId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(contentService.groupListContent(userId, groupId, page));
    }

    // 그룹 전체 피드 리스트 조회
    @GetMapping("content/group/all")
    public CustomResponseEntity<Page<ContentResponse.GroupPage>> contentGroupAllList(
            @AuthenticationPrincipal final Long userId,
            @RequestParam final List<Long> groupId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(contentService.groupAllListContent(userId, groupId, page));
    }

    // 피드 검색 조회
    @GetMapping("content/group/search")
    public CustomResponseEntity<Page<ContentResponse.Create>> searchContent(
            @RequestParam final List<Long> groupId,
            @RequestParam final String word,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(contentService.contentSearch(groupId, word, page));
    }


    // 피드 수정
    @PutMapping("content")
    public CustomResponseEntity<ContentResponse.Update> contentUpdate(
            @AuthenticationPrincipal final Long userId,
            @RequestPart(required = false) final List<MultipartFile> multipartFile,
            @RequestParam final Long contentId,
            @RequestParam final String content,
            @RequestParam(required = false) final Double latitude,
            @RequestParam(required = false) final Double longitude,
            @RequestParam(required = false) final String location
    ) {
        return CustomResponseEntity.success(contentService.updateContent(
                userId, multipartFile, contentId,
                content, latitude, longitude, location)
        );
    }

    // 피드 삭제
    @DeleteMapping("content")
    public CustomResponseEntity<Boolean> contentDelete(
            @AuthenticationPrincipal final Long userId,
            @RequestParam final Long contentId
    ) {
        return CustomResponseEntity.success(contentService.deleteContent(userId, contentId));
    }

    // 지도 포함 검색
    @GetMapping("content/map")
    public CustomResponseEntity<List<ContentDto.mapListContent>> myMapList(
            @AuthenticationPrincipal final Long userId,
            @RequestParam final Double startLatitude,
            @RequestParam final Double startLongitude,
            @RequestParam final Double endLatitude,
            @RequestParam final Double endLongitude
    ) {
        return CustomResponseEntity.success(contentService.listMyMap(userId, startLatitude, startLongitude, endLatitude, endLongitude));
    }

    // (중복되는 장소의) 지도 피드 상세보기
    @GetMapping("content/map/detail")
    public CustomResponseEntity<List<ContentDto.mapListContentDetail>> myMapListDetail(
            @RequestParam final String location, @AuthenticationPrincipal final Long userId
    ) {
        return CustomResponseEntity.success(contentService.listDetailMyMap(location, userId));
    }
}

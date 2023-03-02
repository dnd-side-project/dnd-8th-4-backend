package dnd.diary.controller.content;

import dnd.diary.dto.content.ContentDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.ContentService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
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

    // 그룹 피드 리스트 조회
    @GetMapping("content/group")
    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> contentGroupList(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final Long groupId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(contentService.groupListContent(userDetails, groupId, page));
    }

    // 그룹 전체 피드 리스트 조회
    @GetMapping("content/group/all")
    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> contentGroupAllList(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final List<Long> groupId,
            @RequestParam final Integer page
    ) {
        return CustomResponseEntity.success(contentService.groupAllListContent(userDetails, groupId, page));
    }

    // 피드 검색 조회
    @GetMapping("content/group/search")
    public CustomResponseEntity<Page<ContentDto.ContentSearchDto>> searchContent(
            @RequestParam final List<Long> groupId,
            @RequestParam final String word,
            @RequestParam final Integer page
    ) {
        return contentService.contentSearch(groupId, word, page);
    }

    // 피드 작성
    @PostMapping("content")
    public CustomResponseEntity<ContentDto.CreateDto> contentCreate(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestPart(required = false) final List<MultipartFile> multipartFile,
            @RequestParam final Long groupId,
            @RequestParam final String content,
            @RequestParam(required = false) final Double latitude,
            @RequestParam(required = false) final Double longitude,
            @RequestParam(required = false) final String location
    ) throws ParseException {
        return CustomResponseEntity.success(contentService.createContent(
                userDetails, multipartFile, groupId,
                content, latitude, longitude, location)
        );
    }

    // 피드 조회
    @GetMapping("content")
    public CustomResponseEntity<ContentDto.detailDto> contentDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Long contentId
    ) {
        return CustomResponseEntity.success(contentService.detailContent(userDetails, contentId));
    }

    // 피드 수정
    @PutMapping("content")
    public CustomResponseEntity<ContentDto.UpdateDto> contentUpdate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart(required = false) final List<MultipartFile> multipartFile,
            @RequestParam final Long contentId,
            @RequestParam final String content,
            @RequestParam(required = false) final Double latitude,
            @RequestParam(required = false) final Double longitude,
            @RequestParam(required = false) final String location
    ) {
        return CustomResponseEntity.success(contentService.updateContent(
                userDetails, multipartFile, contentId,
                content, latitude, longitude, location)
        );
    }

    // 피드 삭제
    @DeleteMapping("content")
    public CustomResponseEntity<ContentDto.deleteContent> contentDelete(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final Long contentId
    ) {
        return contentService.deleteContent(userDetails, contentId);
    }

    // 지도 포함 검색
    @GetMapping("content/map")
    public CustomResponseEntity<List<ContentDto.mapListContent>> myMapList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Double x,
            @RequestParam final Double y
    ) {
        return CustomResponseEntity.success(contentService.listMyMap(userDetails, x, y));
    }

    // 지도 피드 상세보기
    @GetMapping("content/map/detail")
    public CustomResponseEntity<List<ContentDto.mapListContentDetail>> myMapListDetail(
            @RequestParam final String location, @AuthenticationPrincipal UserDetails userDetails
    ) {
        return CustomResponseEntity.success(contentService.listDetailMyMap(location, userDetails));
    }
}

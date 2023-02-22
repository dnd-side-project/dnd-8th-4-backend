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

import javax.validation.Valid;
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
        return contentService.groupListContent(userDetails, groupId, page);
    }

    // 그룹 전체 피드 리스트 조회
    @GetMapping("content/group/all")
    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> contentGroupAllList(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final List<Long> groupId,
            @RequestParam final Integer page
    ) {
        return contentService.groupAllListContent(userDetails, groupId, page);
    }

    // 피드 작성
    @PostMapping("content")
    public CustomResponseEntity<ContentDto.CreateDto> contentCreate(
            @AuthenticationPrincipal final UserDetails user,
            @RequestParam final Long groupId,
            @RequestPart(required = false) final List<MultipartFile> multipartFile,
            @Valid @RequestPart final ContentDto.CreateDto request
    ) throws ParseException {
        return contentService.createContent(user, groupId, multipartFile, request);
    }

    // 피드 조회
    @GetMapping("content")
    public CustomResponseEntity<ContentDto.detailDto> contentDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Long contentId
    ) {
        return contentService.detailContent(userDetails, contentId);
    }

    // 피드 수정
    @PatchMapping("content")
    public CustomResponseEntity<ContentDto.UpdateDto> contentUpdate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Long contentId,
            @RequestPart(required = false) final List<MultipartFile> multipartFile,
            @Valid @RequestPart final ContentDto.UpdateDto request
    ) {
        return contentService.updateContent(userDetails, contentId, multipartFile, request);
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
        return contentService.listMyMap(userDetails, x, y);
    }

    // 지도 피드 상세보기
    @GetMapping("content/map/detail")
    public CustomResponseEntity<List<ContentDto.mapListContentDetail>> myMapListDetail(
            @RequestParam final List<Long> contentId
    ){
        return contentService.listDetailMyMap(contentId);
    }
}

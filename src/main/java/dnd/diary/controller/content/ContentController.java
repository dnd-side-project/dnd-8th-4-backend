package dnd.diary.controller.content;

import dnd.diary.dto.content.ContentDto;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.content.ContentService;
import lombok.RequiredArgsConstructor;
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
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long groupId,
            @RequestParam Integer page
    ) {
        return contentService.groupListContent(userDetails, groupId, page);
    }

    // 그룹 전체 피드 리스트 조회
    @GetMapping("content/group/all")
    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> contentGroupAllList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam List<Long> groupId,
            @RequestParam Integer page
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
    ) {
        return contentService.createContent(user, groupId, multipartFile, request);
    }

    // 피드 조회
    @GetMapping("content")
    public CustomResponseEntity<ContentDto.detailDto> contentDetail(
            @RequestParam final Long contentId
    ) {
        return contentService.detailContent(contentId);
    }

    // 피드 수정
    @PatchMapping("content")
    public CustomResponseEntity<ContentDto.UpdateDto> contentUpdate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam final Long contentId,
            @RequestPart(required = false) final List<MultipartFile> multipartFile,
            @Valid @RequestPart final ContentDto.UpdateDto request
    ) {
        return contentService.updateContent(userDetails,contentId, multipartFile, request);
    }

    // 피드 삭제
    @DeleteMapping("content")
    public CustomResponseEntity<ContentDto.deleteContent> contentDelete(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam final Long contentId
    ) {
        return contentService.deleteContent(userDetails, contentId);
    }
}

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

    @GetMapping("content/group")
    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> contentGroupList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long groupId,
            @RequestParam Integer page
    ){
        return contentService.groupListContent(userDetails,groupId,page);
    }

    @GetMapping("content/group/all")
    public CustomResponseEntity<Page<ContentDto.groupListPagePostsDto>> contentGroupAllList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam List<Long> groupId,
            @RequestParam Integer page
    ){
        return contentService.groupAllListContent(userDetails,groupId, page);
    }

    @PostMapping("content")
    public CustomResponseEntity<ContentDto.CreateDto> contentCreate(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam Long groupId,
            @RequestPart (required = false) List<MultipartFile> multipartFile,
            @Valid @RequestPart ContentDto.CreateDto request
    ){
        return contentService.createContent(user,groupId,multipartFile,request);
    }

    @GetMapping("content")
    public CustomResponseEntity<ContentDto.detailDto> contentDetail(
            @RequestParam Long contentId
    ){
        return contentService.detailContent(contentId);
    }

    @PatchMapping("content")
    public CustomResponseEntity<ContentDto.UpdateDto> contentUpdate(
        @RequestParam Long contentId,
        @RequestPart (required = false) List<MultipartFile> multipartFile,
        @Valid @RequestPart ContentDto.UpdateDto request
    ){
        return contentService.updateContent(contentId,multipartFile,request);
    }

    @DeleteMapping("content")
    public CustomResponseEntity<ContentDto.deleteContent> contentDelete(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long contentId
    ){
        return contentService.deleteContent(userDetails,contentId);
    }
}

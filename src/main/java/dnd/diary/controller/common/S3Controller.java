package dnd.diary.controller.common;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dnd.diary.response.CustomResponseEntity;
import dnd.diary.service.s3.S3Service;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	@PostMapping("/files")
	public CustomResponseEntity<List<String>> uploadImageList(@RequestParam("images") List<MultipartFile> multipartFile) throws Exception {
		List<String> fileImageList = s3Service.uploadImageList(multipartFile);
		return CustomResponseEntity.success(fileImageList);
	}

	@PostMapping("/file")
	public CustomResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile multipartFile) throws Exception {
		String fileImageList = s3Service.uploadImage(multipartFile);
		return CustomResponseEntity.success(fileImageList);
	}

	@DeleteMapping("/file")
	public CustomResponseEntity<Object> remove(String fileUrl) throws Exception {
		s3Service.remove(fileUrl);
		return CustomResponseEntity.success();
	}
}

package dnd.diary.service.s3;

import static dnd.diary.enumeration.Result.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import dnd.diary.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public String saveProfileImage(MultipartFile file) {
		String fileName = createFileName(file.getOriginalFilename());
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setContentType(file.getContentType());

		try (InputStream inputStream = file.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead));

		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
		}
		return amazonS3.getUrl(bucket, fileName).toString();
	}

	public List<String> uploadImageList(List<MultipartFile> multipartFile) {

		List<String> fileUrlList = new ArrayList<>();

		multipartFile.forEach(file -> {
			String fileName = createFileName(file.getOriginalFilename());
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.getSize());
			objectMetadata.setContentType(file.getContentType());

			try (InputStream inputStream = file.getInputStream()) {
				amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead));
				String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
				fileUrlList.add(fileUrl);
			} catch (IOException e) {
				throw new CustomException(FAIL_IMAGE_UPLOAD);
			}

		});

		return fileUrlList;
	}

	public String createFileName(String fileName) {
		// 랜덤으로 파일 이름 생성
		return UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	public String uploadImage(MultipartFile multipartFile) {

		String fileName = createFileName(multipartFile.getOriginalFilename());
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(multipartFile.getSize());
		objectMetadata.setContentType(multipartFile.getContentType());

		String fileUrl = "";
		try (InputStream inputStream = multipartFile.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead));
			fileUrl = amazonS3.getUrl(bucket, fileName).toString();
		} catch (IOException e) {
			throw new CustomException(FAIL_IMAGE_UPLOAD);
		}

		return fileUrl;
	}

	// 파일 확장자 전달
	public String getFileExtension(String fileName) {
		try {
			return fileName.substring(fileName.lastIndexOf("."));
		} catch (StringIndexOutOfBoundsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 (" + fileName + ") 입니다.");
		}
	}

	public void remove(String fileUrl) throws Exception {
		String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}

}

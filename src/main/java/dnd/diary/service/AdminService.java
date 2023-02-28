package dnd.diary.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dnd.diary.domain.group.GroupImage;
import dnd.diary.domain.user.UserImage;
import dnd.diary.repository.group.GroupImageRepository;
import dnd.diary.repository.user.UserImageRepository;
import dnd.diary.service.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserImageRepository userImageRepository;
	private final GroupImageRepository groupImageRepository;

	private final S3Service s3Service;

	// 사용자 기본 프로필 등록
	public void createUserProfileImage(List<MultipartFile> multipartFiles) {
		List<String> userProfileImageList = s3Service.uploadImageList(multipartFiles);
		for (String userProfileImage : userProfileImageList) {
			UserImage userImage = UserImage.toEntity(userProfileImage);
			userImageRepository.save(userImage);
		}
	}

	// 그룹 기본 이미지 등록
	public void createGroupProfileImage(List<MultipartFile> multipartFiles) {
		List<String> groupProfileImageList = s3Service.uploadImageList(multipartFiles);
		for (String groupProfileImage : groupProfileImageList) {
			GroupImage groupImage = GroupImage.toEntity(groupProfileImage);
			groupImageRepository.save(groupImage);
		}
	}
}
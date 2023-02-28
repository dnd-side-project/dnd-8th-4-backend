package dnd.diary.service;

import org.springframework.stereotype.Service;

import dnd.diary.repository.group.GroupImageRepository;
import dnd.diary.repository.user.UserImageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserImageRepository userImageRepository;
	private final GroupImageRepository groupImageRepository;

	// 사용자 기본 프로필 등록


	// 그룹 기본 이미지 등록
}

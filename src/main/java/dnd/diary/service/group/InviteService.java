package dnd.diary.service.group;

import static dnd.diary.enumeration.Result.*;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.diary.domain.group.Group;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.dto.userDto.UserDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.GroupRepository;
import dnd.diary.repository.group.InviteRepository;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InviteService {

	private final GroupRepository groupRepository;
	private final InviteRepository inviteRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	private final UserService userService;

	// 초대 수락
	public void acceptInvite(Long groupId, Long notificationId, boolean acceptYn) {
		User user = findUser();
		Group invitedGroup = findGroup(groupId);

		checkAlreadyExist(user, invitedGroup);


	}


	// 초대 거절
	public void rejectInvite(Long groupId, Long notificationId, boolean acceptYn) {
		User user = findUser();
		Group invitedGroup = findGroup(groupId);

		checkAlreadyExist(user, invitedGroup);
	}

	// 이미 가입한 그룹인지 체크
	private void checkAlreadyExist(User user, Group group) {
		List<UserJoinGroup> userJoinGroupList = group.getUserJoinGroups();
		for (UserJoinGroup userJoinGroup : userJoinGroupList) {
			if (user.getId().equals(userJoinGroup.getUser().getId())) {
				throw new CustomException(ALREADY_EXIST_IN_GROUP);
			}
		}
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_GROUP));
	}

	private User findUser() {
		UserDto.InfoDto userInfo = userService.findMyListUser();
		return userRepository.findById(userInfo.getId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
	}
}

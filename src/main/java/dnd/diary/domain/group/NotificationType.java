package dnd.diary.domain.group;

import lombok.Getter;

@Getter
public enum NotificationType {

	INVITE(0, "그룹 초대 알림"),
	CONTENT_COMMENT(1, "내 게시물 댓글 알림"),
	CONTENT_EMOTION(2, "내 게시물 공감 알림"),
	COMMENT_LIKE(3, "내 댓글 공감 알림"),
	NEW_GROUP_MEMBER(4, "그룹 새 멤버 알림");

	private final int code;
	private final String desc;
	NotificationType(final int code, final String desc) {
		this.code = code;
		this.desc = desc;
	}
}

package dnd.diary.enumeration;

import lombok.Getter;

@Getter
public enum NotificationType implements EnumType {

    INVITE(0, "초대 알림"),
    CONTENT(1, "그룹 새 게시물 알림"),
    COMMENT(2, "게시물 새 댓글 알림"),
    EMOTION(3, "게시물 공감 알림"),
    LIKE(4, "댓글 좋아요 알림");

    private final int code;
    private final String desc;

    NotificationType(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }
}

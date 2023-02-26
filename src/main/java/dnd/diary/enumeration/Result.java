package dnd.diary.enumeration;

import lombok.Getter;

@Getter
public enum Result {

	OK(0, "성공"),
	FAIL(-1, "실패"),
	// 유저 관련
	NOT_FOUND_USER(2200, "존재하지 않는 사용자"),
	POSSIBLE_USE_EMAIL(0, "사용 가능한 이메일"),
	DUPLICATION_USER(2201, "이미 존재하는 사용자"),
	DUPLICATION_NICKNAME(2202, "이미 존재하는 닉네임"),
	NOT_MATCHED_ID_OR_PASSWORD(2203,"아이디 또는 비밀번호를 잘못 입력하였습니다."),

	// 그룹 관련
	NOT_FOUND_GROUP(2101, "존재하지 않는 그룹"),
	LOW_MIN_GROUP_NAME_LENGTH(2102, "그룹 이름 최소 글자(1자) 미만"),
	HIGH_MAX_GROUP_NAME_LENGTH(2103, "그룹 이름 최대 글자(12자) 초과"),
	HIGH_MAX_GROUP_NOTE_LENGTH(2104, "그룹 소개 최대 글자(30자) 초과"),
	NO_USER_GROUP_LIST(2105, "가입한 그룹이 없는 경우"),
	FAIL_UPDATE_GROUP(2106, "방장만 그룹 수정 가능"),
	FAIL_DELETE_GROUP(2107, "방장만 그룹 삭제 가능"),
	NO_AUTHORITY_INVITE(2108, "방장만 그룹 초대 가능"),
	HIGH_MAX_INVITE_MEMBER_COUNT(2109, "최대 초대 인원 초과"),
	HIGH_MAX_GROUP_MEMBER_COUNT(2110, "그룹 최대 인원 초과"),
	ALREADY_EXIST_IN_GROUP(2111, "이미 그룹에 가입된 사용자"),
	ALREADY_EXIST_GROUP_NAME(2112, "이미 존재하는 그룹 이름"),

	// 알림 관련
	NOT_FOUND_NOTIFICATION(2400, "존재하지 않는 알림"),

	// 미션 관련
	HIGH_MAX_MISSION_NAME_LENGTH(2500, "미션 이름 최대 글자(20자) 초과"),
	NOT_FOUND_MISSION(2501, "존재하지 않는 미션"),
	FAIL_DELETE_MISSION(2502, "미션 생성자만 삭제 가능"),
	INVALID_MISSION_PERIOD(2503, "미션 진행 기간이 아님"),
	INVALID_USER_MISSION(2504, "유저가 가진 미션이 아닌 경우"),
	INVALID_GROUP_MISSION(2505, "그룹이 가진 미션이 아닌 경우"),
	NOT_CHECK_MISSION_LOCATION(2506, "위치 인증이 완료되지 않은 미션"),
	ALREADY_COMPLETE_MISSION(2507, "이미 완료된 미션"),

	// 피드 관련
	CONTENT_DELETE_OK(0, "피드 삭제 성공"),
	EMOTION_DELETE_OK(0, "공감 삭제 성공"),
	COMMENT_LIKE_DELETE_OK(0, "댓글 좋아요 삭제 성공"),
	NOT_FOUND_CONTENT(2300, "해당 게시글은 존재하지 않습니다."),
	NOT_FOUND_COMMENT(2301, "해당 댓글은 존재하지 않습니다."),

	NOT_MATCHED_USER_CONTENT(2302, "유저가 작성한 글이 아닙니다."),
	NOT_SUPPORTED_EMOTION_STATUS(2303, "지원하지 않는 감정 상태입니다."),
	ALREADY_ADD_BOOKMARK(2304, "이미 북마크한 게시물 입니다."),
	BOOKMARK_DELETE_OK(0, "북마크 삭제 성공"),

	// 파일 관련
	FAIL_IMAGE_UPLOAD(2000, "파일 업로드 실패");

	private final int code;
	private final String message;

	Result(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public Result resolve(int code) {
		for (Result result : values()) {
			if (result.getCode() == code) {
				return result;
			}
		}
		return null;
	}
}

package dnd.diary.enumeration;

import lombok.Getter;

@Getter
public enum Result {

	OK(0, "성공"),
	EMOTION_DELETE_OK(0, "공감 삭제 성공"),
	FAIL(-1, "실패"),
	FAIL_IMAGE_UPLOAD(2000, "파일 업로드 실패"),
	NOT_FOUND_USER(2100, "존재하지 않는 사용자"),
	NOT_FOUND_GROUP(2101, "존재하지 않는 그룹"),
	LOW_MIN_GROUP_NAME_LENGTH(2102, "그룹 이름 최소 글자(1자) 미만"),
	HIGH_MAX_GROUP_NAME_LENGTH(2103, "그룹 이름 최대 글자(12자) 초과"),
	HIGH_MAX_GROUP_NOTE_LENGTH(2104, "그룹 소개 최대 글자(30자) 초과"),
	NO_USER_GROUP_LIST(2105, "가입한 그룹이 없는 경우"),
	FAIL_UPDATE_GROUP(2106, "방장만 그룹 수정 가능"),
	FAIL_DELETE_GROUP(2107, "방장만 그룹 삭제 가능");

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

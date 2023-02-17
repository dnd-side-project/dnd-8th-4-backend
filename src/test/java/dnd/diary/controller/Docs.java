package dnd.diary.controller;

import java.util.Map;

import dnd.diary.domain.content.EmotionStatus;
import dnd.diary.enumeration.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 조회할 Enum 목록
@Getter
@NoArgsConstructor
public class Docs {

	Map<Integer, String> apiResponseCodes;
	Map<Integer, String> emotionStatus;

	@Builder(builderClassName = "TestBuilder", builderMethodName = "testBuilder")
	private Docs(Map<Integer, String> apiResponseCodes, Map<Integer, String> emotionStatus) {
		this.apiResponseCodes = apiResponseCodes;
		this.emotionStatus = emotionStatus;
	}
}

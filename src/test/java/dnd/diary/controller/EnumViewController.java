package dnd.diary.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dnd.diary.domain.content.EmotionStatus;
import dnd.diary.enumeration.EnumType;
import dnd.diary.enumeration.Result;
import dnd.diary.response.CustomResponseEntity;

@RestController
public class EnumViewController {

	@GetMapping("/docs")
	public CustomResponseEntity<Docs> findAll() {

		Map<Integer, String> apiResponseCodes = getDocs(Result.values());
		Map<Integer, String> emotionStatus = getDocs(EmotionStatus.values());

		return CustomResponseEntity.success((
			Docs.testBuilder()
				.apiResponseCodes(apiResponseCodes)
				.emotionStatus(emotionStatus)
				.build()
		));
	}

	private Map<Integer, String> getDocs(Result[] result) {
		return Arrays.stream(result)
			.collect(
				Collectors.toMap(Result::getCode, Result::getMessage, (x, y) -> y, LinkedHashMap::new)
			);
	}

	private Map<Integer, String> getDocs(EnumType[] enumTypes) {
		return Arrays.stream(enumTypes)
			.collect(Collectors.toMap(EnumType::getCode, EnumType::getDesc));
	}
}

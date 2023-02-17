package dnd.diary.common;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;

import dnd.diary.controller.Docs;
import dnd.diary.enumeration.Result;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.utils.CustomResponseFieldsSnippet;

public class CommonDocumentationTests extends ApiDocumentationTest {

	@Test
	public void commons() throws Exception {
		//when
		ResultActions result = this.mockMvc.perform(
			get("/docs")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		MvcResult mvcResult = result.andReturn();
		Docs docs = getData(mvcResult);
		result.andExpect(status().isOk())
			.andDo(
				document(
					"common",
					customResponseFields("custom-response", null,
						attributes(key("title").value("공통응답")),
						subsectionWithPath("data").description("데이터"),
						fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
						fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지")
					)
					, customResponseFields("custom-response",
						beneathPath("data.apiResponseCodes").withSubsectionId("apiResponseCodes")
						, attributes(key("title").value("응답코드"))
						, enumConvertFieldDescriptor(Result.values())
					)
					, customResponseFields("custom-response", beneathPath("data.emotionStatus")
							.withSubsectionId("emotionStatus"),
						attributes(key("title").value("emotionStatus 타입")),
						enumConvertStringFieldDescriptor(docs.getEmotionStatus())
					)
				)
			);
	}

	// 커스텀 템플릿 사용을 위한 함수
	public static CustomResponseFieldsSnippet customResponseFields
	(String type,
		PayloadSubsectionExtractor<?> subsectionExtractor,
		Map<String, Object> attributes, FieldDescriptor... descriptors) {
		return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes
			, true);
	}

	// Map 으로 넘어온 enumValue 를 fieldWithPath 로 변경하여 리턴
	private FieldDescriptor[] enumConvertFieldDescriptor(Result[] results) {
		return Arrays.stream(results)
			.map(result -> fieldWithPath(String.valueOf(result.getCode())).description(result.getMessage()))
			.toArray(FieldDescriptor[]::new);
	}

	private static FieldDescriptor[] enumConvertStringFieldDescriptor(Map<Integer, String> enumValues) {

		return enumValues.entrySet().stream()
			.map(x -> fieldWithPath(String.valueOf(x.getKey())).description(x.getValue()))
			.toArray(FieldDescriptor[]::new);
	}

	Docs getData(MvcResult result) throws IOException {
		CustomResponseEntity<Docs> response = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
			new TypeReference<>() {
			});

		return response.getData();
	}
}

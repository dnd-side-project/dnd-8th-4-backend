package dnd.diary.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface DocumentLinkGenerator {

	static String generateLinkCode(DocUrl docUrl) {
		return String.format("link:../common/%s.html[%s %s,role=\"popup\"]", docUrl.pageId, docUrl.text, "코드");
	}

	static String generateText(DocUrl docUrl) {
		return String.format("%s %s", docUrl.text, "코드명");
	}

	@RequiredArgsConstructor
	enum DocUrl {

		CREATOR_STATUS("creatorStatus", "크리에이터 상태");

		private final String pageId;
		@Getter
		private final String text;

	}
}
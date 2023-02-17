package dnd.diary.utils;

import static org.springframework.restdocs.snippet.Attributes.*;

import org.springframework.restdocs.snippet.Attributes;

public interface DocumentFormatGenerator {

	static Attributes.Attribute getDateFormat() {
		return key("format").value("yyyy-MM-dd");
	}
}

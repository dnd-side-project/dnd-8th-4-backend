package dnd.diary.controller.group;

import dnd.diary.exception.CustomException;
import org.springframework.stereotype.Component;

import static dnd.diary.enumeration.Result.*;

@Component
public class GroupValidator {

	private final int MIN_GROUP_NAME = 1;
	private final int MAX_GROUP_NAME = 12;
	private final int MAX_GROUP_NOTE = 30;

	public void checkGroupCreateAndUpdate(String groupName, String groupNote) {
		checkGroupName(groupName);
		checkGroupNote(groupNote);
	}

	private void checkGroupName(String groupName) {
		if (groupName.length() < MIN_GROUP_NAME) {
			throw new CustomException(LOW_MIN_GROUP_NAME_LENGTH);
		}
		if (groupName.length() > MAX_GROUP_NAME) {
			throw new CustomException(HIGH_MAX_GROUP_NAME_LENGTH);
		}
	}

	private void checkGroupNote(String groupNote) {
		if (groupNote.length() > MAX_GROUP_NOTE) {
			throw new CustomException(HIGH_MAX_GROUP_NOTE_LENGTH);
		}
	}
}

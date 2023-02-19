package dnd.diary.controller.group;

import dnd.diary.exception.CustomException;
import org.springframework.stereotype.Component;

import static dnd.diary.enumeration.Result.HIGH_MAX_GROUP_NAME_LENGTH;
import static dnd.diary.enumeration.Result.LOW_MIN_GROUP_NAME_LENGTH;

@Component
public class GroupValidator {

	private final int MIN_GROUP_NAME = 1;
	private final int MAX_GROUP_NAME = 12;

	public void checkGroupName(String groupName) {
		if (groupName.length() < MIN_GROUP_NAME) {
			throw new CustomException(LOW_MIN_GROUP_NAME_LENGTH);
		}
		if (groupName.length() > MAX_GROUP_NAME) {
			throw new CustomException(HIGH_MAX_GROUP_NAME_LENGTH);
		}
	}
}

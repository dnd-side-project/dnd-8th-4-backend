package dnd.diary.controller.mission;

import static dnd.diary.enumeration.Result.*;

import org.springframework.stereotype.Component;

import dnd.diary.dto.group.MissionCreateRequest;
import dnd.diary.exception.CustomException;

@Component
public class MissionValidator {

	private final int MAX_MISSION_NAME_LENGTH = 20;
	public void checkCreateMission(MissionCreateRequest request) {
		if (request.getMissionName().length() > MAX_MISSION_NAME_LENGTH) {
			throw new CustomException(HIGH_MAX_MISSION_NAME_LENGTH);
		}
	}
}

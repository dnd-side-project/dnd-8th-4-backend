package dnd.diary.domain.mission;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtil {

	public static LocalDateTime convertLocalDateTimeZone(final LocalDateTime localDateTime, final ZoneId fromZone, final ZoneId toZone) {
		final ZonedDateTime zonedDateTime = localDateTime.atZone(fromZone);
		final ZonedDateTime converted = zonedDateTime.withZoneSameInstant(toZone);
		return converted.toLocalDateTime();
	}
}
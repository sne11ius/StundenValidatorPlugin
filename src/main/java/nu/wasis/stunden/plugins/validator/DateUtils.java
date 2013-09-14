package nu.wasis.stunden.plugins.validator;

import org.joda.time.DateTime;

public class DateUtils {

	private DateUtils() {
		// static only
	}
	
	public static boolean isSameDay(final DateTime day, final DateTime otherDay) {
		return day.getYear() == otherDay.getYear() &&
			   day.getMonthOfYear() == otherDay.getMonthOfYear() &&
			   day.getDayOfMonth() == otherDay.getDayOfMonth();
	}
	
}

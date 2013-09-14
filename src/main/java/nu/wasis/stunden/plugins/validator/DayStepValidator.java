package nu.wasis.stunden.plugins.validator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class DayStepValidator {

	private DateTime currentDay;

	public DayStepValidator(final DateTime begin) {
		this.currentDay = begin;
	}

	public DayStepResult step(final DateTime nextDay) {
		if (DateUtils.isSameDay(currentDay, nextDay)) {
			// This is ok in our small little world...
			return new DayStepResult(true, "");
		}
		switch (currentDay.getDayOfWeek()) {
			// Fall throughs are intentional ;)
			case DateTimeConstants.MONDAY    :
			case DateTimeConstants.TUESDAY   :
			case DateTimeConstants.WEDNESDAY :
			case DateTimeConstants.THURSDAY  : {
				return checkSingleStep(nextDay, 1);
			}
			case DateTimeConstants.FRIDAY : {
				DayStepResult singleStep = null;
				for (int i = 1; i <= 3; ++i) {
					singleStep = checkSingleStep(nextDay, i);
					if (singleStep.isSuccess()) {
						return singleStep;
					}
				}
				return singleStep;
			}
			default: {
				// This should not happen
				throw new IllegalArgumentException("Uncovered weekday. Maybe they added an 8th one D:");
			}
		}
	}

	private DayStepResult checkSingleStep(final DateTime nextDay, final int stepSize) {
		final DateTime realNextDay = currentDay.plusDays(stepSize);
		final boolean isSameDay = DateUtils.isSameDay(nextDay, realNextDay);
		if (!isSameDay) {
			return new DayStepResult(false, nextDay + " (" + nextDay.dayOfWeek().getAsText() + ") is not a valid successor of " + currentDay + " (" + currentDay.dayOfWeek().getAsText() + ").");
		} else {
			this.currentDay = nextDay;
			return new DayStepResult(true, "");
		}
	}

	public static final class DayStepResult {
		private final boolean success;
		private final String message;
		
		public DayStepResult(final boolean success, final String message) {
			this.success = success;
			this.message = message;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getMessage() {
			return message;
		}
	}
}

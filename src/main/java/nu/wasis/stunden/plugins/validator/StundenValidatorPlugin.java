package nu.wasis.stunden.plugins.validator;

import java.util.Collections;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nu.wasis.stunden.commons.CommonDateUtils;
import nu.wasis.stunden.commons.DayStepValidator;
import nu.wasis.stunden.commons.DayStepValidator.DayStepResult;
import nu.wasis.stunden.model.Day;
import nu.wasis.stunden.model.Entry;
import nu.wasis.stunden.model.WorkPeriod;
import nu.wasis.stunden.plugin.ProcessPlugin;
import nu.wasis.stunden.plugins.validator.exception.InvalidWorkPeriodException;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeConstants;

@PluginImplementation
public class StundenValidatorPlugin implements ProcessPlugin {
	
	private static final Logger LOG = Logger.getLogger(StundenValidatorPlugin.class);

	@Override
	public WorkPeriod process(final WorkPeriod workPeriod, final Object configuration) {
		LOG.info("Validating the stuff...");
		if (workPeriod.getDays().isEmpty()) {
			throw new InvalidWorkPeriodException("Work period contains no days.");
		}
		final boolean startsOnMonday = DateTimeConstants.MONDAY == workPeriod.getBegin().getDayOfWeek();
		final boolean startsOnFirstOfMonth = 1 == workPeriod.getBegin().getDayOfMonth();
		if (!(startsOnMonday || startsOnFirstOfMonth)) {
//			LOG.error("Day of week: " + );
			throw new InvalidWorkPeriodException("Work period must start with a monday or on the first day of month.");
		}
		final boolean endsOnWeekend = endsOnWeekend(workPeriod);
		final boolean endsOnLastOfMonth = CommonDateUtils.isSameDay(workPeriod.getEnd(), workPeriod.getEnd().dayOfMonth().withMaximumValue());
		if (!(endsOnWeekend || endsOnLastOfMonth)) {
			throw new InvalidWorkPeriodException("Work period must end on (friday, saturday sunday) or on last day of month.");
		}
		if (5 > workPeriod.getDays().size()) {
			throw new InvalidWorkPeriodException("Work period must be composed of whole week blocks (weekends are optional).");
		}
		final DayStepValidator dayStepValidator = new DayStepValidator(workPeriod.getBegin());
		for (final Day day : workPeriod.getDays()) {
			validateDayIsSuccessor(dayStepValidator, day);
			validateDayHasEntries(day);
			validateEntries(day);
		}
		LOG.info("...done");
		return workPeriod;
	}
	
	private boolean endsOnWeekend(final WorkPeriod workPeriod) {
		final int lastWeekday = workPeriod.getEnd().getDayOfWeek();
		return DateTimeConstants.FRIDAY   == lastWeekday ||
			   DateTimeConstants.SATURDAY == lastWeekday ||
			   DateTimeConstants.SUNDAY   == lastWeekday;
	}

	private void validateDayIsSuccessor(final DayStepValidator dayStepValidator, final Day day) {
		final DayStepResult dayStepResult = dayStepValidator.step(day.getDate());
		if (!dayStepResult.isSuccess()) {
			throw new InvalidWorkPeriodException(dayStepResult.getMessage());
		}
	}
	
	private void validateDayHasEntries(final Day day) {
		if (day.getEntries().isEmpty()) {
			throw new InvalidWorkPeriodException("Day " + day + " has no entries.");
		}
	}

	private void validateEntries(final Day day) {
		Collections.sort(day.getEntries());
		Entry currentEntry = day.getEntries().get(0);
		for (final Entry nextEntry : day.getEntries()) {
			if (currentEntry == nextEntry) {
				continue;
			}
			if (!(currentEntry.getEnd().getMillis() == nextEntry.getBegin().getMillis())) {
				LOG.error("Hole or overlapping entries:");
				LOG.error(currentEntry);
				LOG.error(nextEntry);
				throw new InvalidWorkPeriodException("There is a hole or overlapping entries in day " + day);
			}
			if (currentEntry.getProject().getName().isEmpty()) {
				throw new InvalidWorkPeriodException("Project name is empty for this entry:" + currentEntry + " (on " + day.getDate() + ").");
			}
			currentEntry = nextEntry;
		}
	}

	@Override
	public Class<?> getConfigurationClass() {
		return null;
	}

}

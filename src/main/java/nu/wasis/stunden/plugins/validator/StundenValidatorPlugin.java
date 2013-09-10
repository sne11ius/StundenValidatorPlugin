package nu.wasis.stunden.plugins.validator;

import java.util.Collections;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nu.wasis.stunden.model.Day;
import nu.wasis.stunden.model.Entry;
import nu.wasis.stunden.model.WorkPeriod;
import nu.wasis.stunden.plugin.ProcessPlugin;
import nu.wasis.stunden.plugins.validator.exception.InvalidWorkPeriodException;

import org.apache.log4j.Logger;

@PluginImplementation
public class StundenValidatorPlugin implements ProcessPlugin {
	
	private static final Logger LOG = Logger.getLogger(StundenValidatorPlugin.class);

	@Override
	public WorkPeriod process(WorkPeriod workPeriod, Object configuration) {
		LOG.info("Validating the stuff...");
		Collections.sort(workPeriod.getDays());
		for (Day day : workPeriod.getDays()) {
			if (day.getDate().isBefore(workPeriod.getBegin()) || day.getDate().isAfter(workPeriod.getEnd())) {
				throw new InvalidWorkPeriodException("Day " + day + " is out of bounds of the work period.");
			}
			if (day.getEntries().isEmpty()) {
				throw new InvalidWorkPeriodException("Day " + day + " has no entries.");
			}
			Collections.sort(day.getEntries());
			Entry currentEntry = day.getEntries().get(0);
			for (Entry nextEntry : day.getEntries()) {
				if (currentEntry == nextEntry) {
					continue;
				}
				if (!(currentEntry.getEnd().getMillis() == nextEntry.getBegin().getMillis())) {
					throw new InvalidWorkPeriodException("There is a hole or overlapping entries in day " + day);
				}
				if (currentEntry.getProject().getName().isEmpty()) {
					throw new InvalidWorkPeriodException("Project name is empty for this entry:" + currentEntry);
				}
				currentEntry = nextEntry;
			}
		}
		// check that all days between start and end are covered - weekends optional
		// ...
		// check no entries overlap
		// ...
		// check for holes in days - they are not ok
		// ...
		//
		LOG.debug("...done");
		return workPeriod;
	}
	
	@Override
	public Class<?> getConfigurationClass() {
		return null;
	}

}

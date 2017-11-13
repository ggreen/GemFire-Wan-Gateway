package io.pivotal.event.listener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;

import io.pivotal.domain.Session;

/**
 * Cache Listener that writes timings to the local file system
 * @author wwilliams
 *
 */
public class SessionListener extends CacheListenerAdapter<String, Session> implements Declarable {

	private static LogWriter logger;
	private static PrintWriter writer;
	private static boolean isLogLevelFine = false;
	private static String sourceDataCenterId;

	static {
		logger = CacheFactory.getAnyInstance().getDistributedSystem().getLogWriter();
	}

	/**
	 * updates the new Session object. GemFire has a lock on this object while in
	 * this method for consistency
	 */
	@Override
	public void afterUpdate(EntryEvent<String, Session> entryEvent) {
		if (isLogLevelFine) {
			logger.fine("I am in afterUpdate");
		}
		logEntry(entryEvent);
	}

	public void afterCreate(EntryEvent<String, Session> entryEvent) {
		if (isLogLevelFine) {
			logger.fine("I am in afterCreate");
		}
		logEntry(entryEvent);
	}
	private void logEntry(EntryEvent<String, Session> entryEvent) {

		Session newSession = entryEvent.getNewValue();
		if (newSession == null) {
			return;
		}

		if (newSession.getSourceDataCenterId() == null
				|| newSession.getSourceDataCenterId().equals(sourceDataCenterId)) {
			return;
		}

		long toTime = System.currentTimeMillis();
		newSession.setToTime(toTime);
		newSession.setDurationMillis((int) (toTime - newSession.getFromTime()));

		writer.println(newSession.getId() + "," + newSession.getSourceDataCenterId() + ","
				+ newSession.getAppId() + "," + newSession.getFromTime() + "," + newSession.getDurationMillis());
	}

	public void init(Properties props) {
		logger.info("I am in " + this.getClass().getSimpleName() + " init");
		String fileName = (String) props.get("filename");
		String logLevel = (String) props.get("loglevel");
		isLogLevelFine = (logLevel.equalsIgnoreCase("fine") ? true : false);
		sourceDataCenterId = (String) props.get("dataCenterId");
		logger.info("Data Center Id=" + sourceDataCenterId);

		try {
			writer = new PrintWriter(new FileWriter(fileName), true);
			writer.println("ID,Data Center,App,Entry Time,WAN Duration");
			logger.info("ID,Data Center,App,Entry Time,WAN Duration");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		logger.info("logLevel=" + logLevel);
	}
}

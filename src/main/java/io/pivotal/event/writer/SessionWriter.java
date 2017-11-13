package io.pivotal.event.writer;

import java.util.Properties;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheWriterAdapter;
import com.gemstone.gemfire.internal.cache.EntryEventImpl;

import io.pivotal.domain.BaseObject;
import io.pivotal.domain.Session;

/**
 * Sample program to inject architecture-level attributes into an object
 * @author wwilliams
 *
 */
public class SessionWriter extends CacheWriterAdapter<String, Session> implements Declarable {

	private static LogWriter logger;
	private static String sourceDataCenterId;

	static {
		logger = CacheFactory.getAnyInstance().getDistributedSystem().getLogWriter();
	}

	/**
	 * updates the new Session object. GemFire has a lock on this object while in
	 * this method for consistency
	 */
	@Override
	public void beforeUpdate(EntryEvent<String, Session> entryEvent) {
		doUpdate(entryEvent);
	}

	public void beforeCreate(EntryEvent<String, Session> entryEvent) {
		doUpdate(entryEvent);
	}

	private void doUpdate(EntryEvent<String, Session> entryEvent) {

		BaseObject newSession = (BaseObject) entryEvent.getNewValue();
		if (newSession == null) {
			return;
		}
		
		// if it came from this data center then timestamp it, otherwise log it
		if (newSession.getSourceDataCenterId() == null 
				|| newSession.getSourceDataCenterId().equals(sourceDataCenterId)) {
			timeStampEntry(newSession, entryEvent);
		}
	}

	private void timeStampEntry(BaseObject newSession, EntryEvent<String, Session> entryEvent) {
		// if the client provided an appId, pass it onto the 
		String appId = (String) entryEvent.getCallbackArgument();
		 if (appId != null) {
			 newSession.setAppId(appId);
		 }
		newSession.setFromTime(System.currentTimeMillis());
		newSession.setSourceDataCenterId(sourceDataCenterId);

		// update the new entry
		EntryEventImpl eei = (EntryEventImpl) entryEvent;
		eei.setNewValue(newSession);
		eei.makeSerializedNewValue();
	}

	public void init(Properties props) {
		logger.info("I am in " + this.getClass().getSimpleName() + " class init");
		sourceDataCenterId = (String) props.get("dataCenterId");
		logger.info("Data Center Id=" + sourceDataCenterId);
	}
}

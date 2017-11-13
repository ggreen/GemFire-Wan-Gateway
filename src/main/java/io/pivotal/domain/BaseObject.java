package io.pivotal.domain;

public class BaseObject {

	private String sourceDataCenterId;
	private long fromTime;
	private long toTime;
	private int durationMillis;
	private String appId;

	// for Serialization only. Do not use
	public BaseObject() {
	}
	

	public long getFromTime() {
		return fromTime;
	}

	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	public long getToTime() {
		return toTime;
	}

	public void setToTime(long toTime) {
		this.toTime = toTime;
	}

	public int getDurationMillis() {
		return durationMillis;
	}

	public void setDurationMillis(int durationMillis) {
		this.durationMillis = durationMillis;
	}


	public String getSourceDataCenterId() {
		return sourceDataCenterId;
	}


	public void setSourceDataCenterId(String sourceDataCenterId) {
		this.sourceDataCenterId = sourceDataCenterId;
	}


	public String getAppId() {
		return appId;
	}


	public void setAppId(String appId) {
		this.appId = appId;
	}


	@Override
	public String toString() {
		return "BaseObject [fromTime=" + fromTime + ", toTime=" + toTime + ", durationMillis=" + durationMillis + ", sourceDataCenterId=" + sourceDataCenterId + ", appId=" + appId + "]";
	}
}

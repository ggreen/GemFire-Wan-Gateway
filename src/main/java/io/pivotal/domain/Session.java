package io.pivotal.domain;

public class Session extends BaseObject {

	private String id;
	private byte[] payload;
	
	// stupid constructor for serializer - DO NOT USE
	public Session() {
		super();
	}

	public Session(String id, byte[] payload) {
		super();
		this.id = id;
		this.payload = payload;
	}

	public byte[] getPayload() {
		return payload;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Session [id=" + id + super.toString() + "]";
	}

}

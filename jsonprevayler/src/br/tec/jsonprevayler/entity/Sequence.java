package br.tec.jsonprevayler.entity;

import java.io.Serializable;

public class Sequence implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private long lastValue;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getLastValue() {
		return lastValue;
	}
	public void setLastValue(long lastValue) {
		this.lastValue = lastValue;
	}
	
}
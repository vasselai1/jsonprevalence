package br.tec.jsonprevayler.entity;

import java.util.Date;

public class History implements Comparable<History> {
	
	private Date dateVersion;
	private String json;
	
	public History(Date dateVersion, String json) {
		this.dateVersion = dateVersion;
		this.json = json;
	}
	public Date getDateVersion() {
		return dateVersion;
	}
	public void setDateVersion(Date dateVersion) {
		this.dateVersion = dateVersion;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	@Override
	public int compareTo(History otherHistory) {
		return dateVersion.compareTo(otherHistory.getDateVersion());
	}
	
	@Override
	public String toString() {
		return "History [dateVersion=" + dateVersion + ", json=" + json + "]";
	}
	
}
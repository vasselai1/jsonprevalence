package br.tec.jsonprevayler.entity;

import java.io.Serializable;

public class GeoPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double latitude;
	private Double longitude;
	
	public GeoPoint(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
}
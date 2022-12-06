package br.tec.jsonprevayler.entity;

import java.util.Date;

public class WarningPrevalenceOperation implements PrevalenceEntity {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Date moment;
	private String description;
	private String reason;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getMoment() {
		return moment;
	}
	public void setMoment(Date moment) {
		this.moment = moment;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
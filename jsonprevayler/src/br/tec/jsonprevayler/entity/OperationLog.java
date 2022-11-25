package br.tec.jsonprevayler.entity;

import java.util.Date;

public final class OperationLog implements PrevalenceEntity {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String className;
	private String name;
	private String json;
	private Date initMoment;
	
	public OperationLog(String className, String name, String json) {
		this.className = className;
		this.name = name;
		this.json = json;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public Date getInitMoment() {
		return initMoment;
	}
	public void setInitMoment(Date initMoment) {
		this.initMoment = initMoment;
	}
	
}
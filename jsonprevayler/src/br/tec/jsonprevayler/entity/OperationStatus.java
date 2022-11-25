package br.tec.jsonprevayler.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.pojojsonrepository.core.operations.OperationState;

public class OperationStatus implements PrevalenceEntity {	
	
	private static final long serialVersionUID = 1L;	
	
	private Long id;	
	private Map<OperationState, Date> status = new HashMap<OperationState, Date>();
	private List<String> mensages = new ArrayList<String>();
	
	public OperationStatus(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Map<OperationState, Date> getStatus() {
		return status;
	}
	public void setStatus(Map<OperationState, Date> status) {
		this.status = status;
	}
	public List<String> getMensages() {
		return mensages;
	}
	public void setMensages(List<String> mensages) {
		this.mensages = mensages;
	}
	public void addMessage(String mensage) {
		mensages.add(mensage);
	}
	
}
package br.org.pr.jsonprevayler.pojojsonrepository.core;

import java.util.Date;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

public class EntityTokenKey {
	
	private final Class<? extends PrevalenceEntity> classe;
	private final Long id;
	private String currentUseDescription;
	private Date currentMomentUse;
	
	public EntityTokenKey(Class<? extends PrevalenceEntity> classe, Long id) {
		this.classe = classe;
		this.id = id;
	}

	public void setUse(String description) {
		currentUseDescription = description;
		currentMomentUse = new Date();
	}
	public void setEnd() {
		currentUseDescription = null;
		currentMomentUse = null;
	}
	
	public Class<? extends PrevalenceEntity> getClasse() {
		return classe;
	}
	public Long getId() {
		return id;
	}
	public String getCurrentUseDescription() {
		return currentUseDescription;
	}
	public Date getCurrentMomentUse() {
		return currentMomentUse;
	}
	
}
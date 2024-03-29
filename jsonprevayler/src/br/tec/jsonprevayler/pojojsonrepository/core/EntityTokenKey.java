package br.tec.jsonprevayler.pojojsonrepository.core;

import java.util.Date;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.pojojsonrepository.core.util.DateProvider;

public class EntityTokenKey {
	
	private final Class<? extends PrevalenceEntity> classe;
	private final Long id;
	private String currentUseDescription;
	private Date currentMomentUse;
	private DateProvider dateProvider;
	
	public EntityTokenKey(Class<? extends PrevalenceEntity> classe, Long id, DateProvider dateProvider) {
		this.classe = classe;
		this.id = id;
		this.dateProvider = dateProvider;
	}

	public void setUse(String description) {
		currentUseDescription = description;
		currentMomentUse = dateProvider.get();
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

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("EntityTokenKey [classe=").append(classe);
		result.append(", id=").append(id);
		result.append(", currentUseDescription=").append(currentUseDescription);
		result.append(", currentMomentUse=").append(currentMomentUse);
		return result.toString();
	}

}
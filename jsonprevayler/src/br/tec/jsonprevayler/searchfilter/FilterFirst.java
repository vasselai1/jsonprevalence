package br.tec.jsonprevayler.searchfilter;

import br.tec.jsonprevayler.entity.PrevalenceEntity;

public interface FilterFirst <T extends PrevalenceEntity> {

	boolean isAcepted(T entity);
	
}

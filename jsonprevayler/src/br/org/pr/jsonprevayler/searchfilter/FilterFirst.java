package br.org.pr.jsonprevayler.searchfilter;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

public interface FilterFirst <T extends PrevalenceEntity> {

	boolean isAcepted(T entity);
	
}

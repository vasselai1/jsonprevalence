package br.org.pr.jsonprevayler.searchfilter;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

public interface ProgressSearchObserver <T extends PrevalenceEntity> {

	void setTotal(int total);
	void addOneProgress();
	void addFounded(T entity);
	
}
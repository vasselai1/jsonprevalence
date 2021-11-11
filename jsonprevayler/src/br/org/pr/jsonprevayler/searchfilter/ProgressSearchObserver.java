package br.org.pr.jsonprevayler.searchfilter;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

public interface ProgressSearchObserver <T extends PrevalenceEntity> {

	void setTotalEntitiesRepository(Integer total);
	Integer getTotalEntitiesRepository();
	void addOneProgress();
	void addFounded(T entity);
	void sumCountFounded(int unitsFoundeds);
	void endEvent();
	
}
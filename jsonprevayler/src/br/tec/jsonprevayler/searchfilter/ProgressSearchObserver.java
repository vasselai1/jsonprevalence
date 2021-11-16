package br.tec.jsonprevayler.searchfilter;

import br.tec.jsonprevayler.entity.PrevalenceEntity;

public interface ProgressSearchObserver <T extends PrevalenceEntity> {

	void setTotalEntitiesRepository(Integer total);
	Integer getTotalEntitiesRepository();
	void addOneProgress();
	void addFounded(T entity);
	void sumCountFounded(int unitsFoundeds);
	void endEvent();
	
}
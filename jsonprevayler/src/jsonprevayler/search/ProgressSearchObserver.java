package jsonprevayler.search;

import jsonprevayler.entity.PrevalenceEntity;

public interface ProgressSearchObserver <T extends PrevalenceEntity> {

	void setTotal(int total);
	void addOneProgress();
	void addFounded(T entity);
	
}
package br.org.pr.jsonprevayler.searchfilter;

import java.util.Comparator;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;

public interface PrevalenceFilter <T extends PrevalenceEntity> {

	boolean isAcepted(T entity);
	Class<? extends PrevalenceEntity> getClasse();
	Comparator<T> getComparator();
	ProgressSearchObserver<T> getProgressSearchObserver();
	int getFirstResult();
	int getPageSize();
	void setTotal(int total);
	void setMemorySearchEngine(MemorySearchEngineInterface searchEngine);
	
}
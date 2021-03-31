package jsonprevayler.search;

import java.util.Comparator;

import jsonprevayler.entity.PersistenceEntity;

public interface PersistenceFilter <T extends PersistenceEntity> {

	boolean isAcepted(T entity);
	Class<? extends PersistenceEntity> getClasse();
	Comparator<T> getComparator();
	int getFirstResult();
	int getPageSize();
	void setTotal(int total);	
	
}
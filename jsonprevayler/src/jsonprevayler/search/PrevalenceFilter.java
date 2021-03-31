package jsonprevayler.search;

import java.util.Comparator;

import jsonprevayler.PrevalenceRepository;
import jsonprevayler.entity.PrevalenceEntity;

public interface PrevalenceFilter <T extends PrevalenceEntity> {

	boolean isAcepted(T entity);
	Class<? extends PrevalenceEntity> getClasse();
	Comparator<T> getComparator();
	int getFirstResult();
	int getPageSize();
	void setTotal(int total);
	void setPrevalenceInstance(PrevalenceRepository pojoJsonRepository);
	
}
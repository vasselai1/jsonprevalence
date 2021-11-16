package br.tec.jsonprevayler.searchfilter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;

public abstract class PrevalenceSearchFilter <T extends PrevalenceEntity> implements PrevalenceFilter<T> {
	
	protected int firstResult;
	protected int pageSize;
	protected int total;
	protected Comparator<T> comparator;
	protected Map<Long,Integer> matches = new HashMap<Long, Integer>();
	protected MemorySearchEngineInterface searchEngine;
	
	public PrevalenceSearchFilter(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	public PrevalenceSearchFilter(int firstResult, int pageSize, Comparator<T> comparator) {
		this.firstResult = firstResult;
		this.pageSize = pageSize;
		this.comparator = comparator;
	}
	
	public int getFirstResult() {
		return firstResult;
	}
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public Comparator<T> getComparator() {
		return comparator;
	}
	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	protected void addMatche(Long id) {
		if (matches.containsKey(id)) {
			matches.put(id, (matches.get(id) + 1));
		} else {
			matches.put(id, (1));
		}
	}
	public void setMemorySearchEngine(MemorySearchEngineInterface searchEngine) {
		this.searchEngine = searchEngine;
	}
	
}
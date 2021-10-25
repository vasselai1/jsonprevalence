package br.org.pr.jsonprevayler.searchfilter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import br.org.pr.jsonprevayler.PrevalentJsonRepository;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

public abstract class PrevalenceSearchFilter <T extends PrevalenceEntity> implements PrevalenceFilter<T> {
	
	protected int firstResult;
	protected int pageSize;
	protected int total;
	protected Comparator<T> comparator;
	protected Map<Long,Integer> matches = new HashMap<Long, Integer>();
	protected PrevalentJsonRepository prevalence;
	
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
	public void setPrevalenceInstance(PrevalentJsonRepository pojoJsonRepository) {
		this.prevalence = pojoJsonRepository;
	}
}
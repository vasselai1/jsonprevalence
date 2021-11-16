package br.tec.jsonprevayler.searchfilter;

import java.util.Comparator;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;
import br.tec.jsonprevayler.searchfilter.processing.BasicProgressSearchObserver;

public abstract class AbstractPrevalenceFilter <T extends PrevalenceEntity> implements PrevalenceFilter<T> {

	protected MemorySearchEngineInterface searchEngine;
	protected ProgressSearchObserver<T> progressSearchObserver = new BasicProgressSearchObserver<T>();
	
	protected Comparator<T> comparator = new Comparator<T>() {
		@Override
		public int compare(T o1, T o2) {
			return o1.getId().compareTo(o2.getId());
		}
	};

	private final T example;
	private int total = 0;

	public AbstractPrevalenceFilter(ProgressSearchObserver<T> progressSearchObserver, Comparator<T> comparator, T example) {
		this.progressSearchObserver = progressSearchObserver;
		this.comparator = comparator;
		this.example = example;
	}
	public AbstractPrevalenceFilter(Comparator<T> comparator,T example) {
		this.comparator = comparator;
		this.example = example;
	}
	public AbstractPrevalenceFilter(T example) {
		this.example = example;
	}
	public AbstractPrevalenceFilter() {
		example = null;
	}
	
	public abstract boolean isAcepted(T entity);

	public Class<? extends PrevalenceEntity> getClasse() {
		return example.getClass();
	}	
	public Comparator<T> getComparator() {
		return comparator;
	}	
	public int getFirstResult() {
		return 0;
	}	
	public int getPageSize() {
		return 99999999;
	}	
	public void setTotal(int total) {
		this.total = total;
	}	
	public int getTotal() {
		return total;
	}
	@Override
	public void setMemorySearchEngine(MemorySearchEngineInterface searchEngine) {
		this.searchEngine = searchEngine;
	}
	@Override
	public ProgressSearchObserver<T> getProgressSearchObserver() {
		return progressSearchObserver;
	}
	
}
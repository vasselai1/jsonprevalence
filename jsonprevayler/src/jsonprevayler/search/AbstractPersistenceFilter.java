package jsonprevayler.search;

import java.util.Comparator;

import jsonprevayler.PrevalenceRepository;
import jsonprevayler.entity.PersistenceEntity;

public abstract class AbstractPersistenceFilter <T extends PersistenceEntity> implements PersistenceFilter<T> {

	protected PrevalenceRepository prevalence;
	private Comparator<T> comparator = new Comparator<T>() {
		@Override
		public int compare(T o1, T o2) {
			return o1.getId().compareTo(o2.getId());
		}
	};

	private final T example;
	private int total = 0;

	public AbstractPersistenceFilter(T example) {
		this.example = example;
	}
	public abstract boolean isAcepted(T entity);

	public Class<? extends PersistenceEntity> getClasse() {
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
	public void setPrevalenceInstance(PrevalenceRepository pojoJsonRepository) {
		this.prevalence = pojoJsonRepository;
	}
}
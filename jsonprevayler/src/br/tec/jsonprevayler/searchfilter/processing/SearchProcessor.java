package br.tec.jsonprevayler.searchfilter.processing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.ProgressSearchObserver;

public abstract class SearchProcessor {

	protected MemorySearchEngineInterface prevalence;
	@SuppressWarnings("rawtypes")
	private ProgressSearchObserver progress = new BasicProgressSearchObserver();
	protected boolean onlyCount = false;
	protected int totalFounded = 0;
		
	public void setMemorySearchEngine(MemorySearchEngineInterface prevalence) {
		this.prevalence = prevalence;
	}
	@SuppressWarnings("rawtypes")
	protected void setProgressObserver(ProgressSearchObserver progress) {
		this.progress = progress;
	}

	protected void setTotalEntitiesRepository(Integer total) {
		progress.setTotalEntitiesRepository(total);
	}
	@SuppressWarnings("unchecked")
	protected <T extends PrevalenceEntity> void addFounded(T entity) {
		progress.addFounded(entity);
	}
	protected void addOneProgress() {
		progress.addOneProgress();
	}
	public void setOnlyCount(boolean onlyCount) {
		this.onlyCount = onlyCount;
	}
	
	protected void addCountFounded() {
		progress.sumCountFounded(1);
	}
	
	protected void sumCountFounded(int partialCount) {
		progress.sumCountFounded(partialCount);
	}	
	
	public int getTotalFounded() {
		return totalFounded;
	}
	
	public abstract <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException;
	
}
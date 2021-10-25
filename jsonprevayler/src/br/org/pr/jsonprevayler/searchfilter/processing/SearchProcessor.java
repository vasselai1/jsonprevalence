package br.org.pr.jsonprevayler.searchfilter.processing;

import java.io.IOException;
import java.util.List;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemorySearchEngineInterface;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;
import br.org.pr.jsonprevayler.searchfilter.ProgressSearchObserver;

public abstract class SearchProcessor {

	protected MemorySearchEngineInterface prevalence;
	@SuppressWarnings("rawtypes")
	private ProgressSearchObserver progress;
	protected boolean onlyCount = false;
	protected int totalFounded = 0;
		
	public void setMemorySearchEngine(MemorySearchEngineInterface prevalence) {
		this.prevalence = prevalence;
	}
	@SuppressWarnings("rawtypes")
	protected void setProgressObserver(ProgressSearchObserver progress) {
		this.progress = progress;
	}

	protected void setTotalCount(Integer total) {
		if (progress == null) {
			return;
		}
		progress.setTotal(total);
	}
	@SuppressWarnings("unchecked")
	protected <T extends PrevalenceEntity> void addFounded(T entity) {
		if (progress == null) {
			return;
		}
		progress.addFounded(entity);
	}
	protected void addOneProgress() {
		if (progress == null) {
			return;
		}
		progress.addOneProgress();
	}
	
	public void setOnlyCount(boolean onlyCount) {
		this.onlyCount = onlyCount;
	}
	
	protected void addCountFounded() {
		totalFounded++;
	}
	
	protected void sumCountFounded(int partialCount) {
		totalFounded = totalFounded + partialCount;
	}	
	
	public int getTotalFounded() {
		return totalFounded;
	}
	
	public abstract <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws ClassNotFoundException, IOException, ValidationPrevalenceException;
	
}
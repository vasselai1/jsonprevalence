package br.org.pr.jsonprevayler.searchfilter.processing;

import java.util.ArrayList;
import java.util.List;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.searchfilter.ProgressSearchObserver;

public class BasicProgressSearchObserver<T extends PrevalenceEntity> implements ProgressSearchObserver<T> {

	private Integer totalEntitiesRepository = 0;
	private Integer totalProcessed = 0;
	private Integer totalFounded = 0;
	private boolean endOfSearch = false;
	private List<T> foundeds = new ArrayList<T>();
	
	@Override
	public void setTotalEntitiesRepository(Integer total) {
		totalEntitiesRepository = total;		
	}

	@Override
	public Integer getTotalEntitiesRepository() {
		return totalEntitiesRepository;
	}

	@Override
	public void addOneProgress() {
		totalProcessed++;
	}

	@Override
	public void addFounded(T entity) {
		totalFounded++;
		foundeds.add(entity);
	}

	@Override
	public void sumCountFounded(int unitsFoundeds) {
		totalFounded = totalFounded + unitsFoundeds;//Para processamento distribuido
	}

	@Override
	public void endEvent() {
		endOfSearch = true;		
	}

	public boolean isEndOfSearch() {
		return endOfSearch;
	}

	public Integer getTotalProcessed() {
		return totalProcessed;
	}

	public Integer getTotalFounded() {
		return totalFounded;
	}

	public List<T> getFoundeds() {
		return foundeds;
	}
	
}
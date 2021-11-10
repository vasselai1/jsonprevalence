package br.org.pr.jsonprevayler.searchfilter.processing;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.searchfilter.ProgressSearchObserver;

@SuppressWarnings("rawtypes")
public class BasicProgressSearchObserver implements ProgressSearchObserver {

	private Integer totalEntitiesRepository;
	
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
		totalEntitiesRepository++;
	}

	@Override
	public void addFounded(PrevalenceEntity entity) {
		totalEntitiesRepository++;
	}

	@Override
	public void sumCountFounded(int unitsFoundeds) {
		totalEntitiesRepository = totalEntitiesRepository + unitsFoundeds;
	}

}
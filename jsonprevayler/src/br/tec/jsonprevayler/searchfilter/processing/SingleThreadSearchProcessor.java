package br.tec.jsonprevayler.searchfilter.processing;

import java.util.Collection;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;

public class SingleThreadSearchProcessor extends SearchProcessor {
	
	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws InternalPrevalenceException, ValidationPrevalenceException {
		setProgressObserver(filter.getProgressSearchObserver());
		setTotalEntitiesRepository(prevalence.count(classe));
		filter.setMemorySearchEngine(prevalence);
		Collection<Long> ids = prevalence.getKeys(classe);
		for (Long id : ids) {
			T entity = (T) prevalence.getPojo(classe, id);
			if (filter.isAcepted(entity)) {
				if (onlyCount) {
					addCountFounded();
					continue;
				}
				retorno.add(entity);
				addFounded(entity);
			}
			addOneProgress();
		}
	}
	
}
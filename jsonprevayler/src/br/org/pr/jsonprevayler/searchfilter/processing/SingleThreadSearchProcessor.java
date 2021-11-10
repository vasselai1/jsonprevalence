package br.org.pr.jsonprevayler.searchfilter.processing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;

public class SingleThreadSearchProcessor extends SearchProcessor {
	
	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
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
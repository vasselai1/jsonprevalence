package jsonprevayler.search.processing;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jsonprevayler.PrevalenceRepository;
import jsonprevayler.entity.PrevalenceEntity;
import jsonprevayler.search.PrevalenceFilter;
import jsonprevayler.search.ProgressSearchObserver;

public class SingleThreadSearchProcessor implements SearchProcessor {

	private PrevalenceRepository prevalence;
	
	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno, Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository) {
		if (filter.getProgressSearchObserver() != null) {
			filter.getProgressSearchObserver().setTotal(pojoRepository.size());
		}
		filter.setPrevalenceInstance(prevalence);
		Collection<Long> ids = pojoRepository.get(classe).keySet();
		ProgressSearchObserver<T> progress = filter.getProgressSearchObserver();
		for (Long id : ids) {
			T entity = (T) pojoRepository.get(classe).get(id);
			if (filter.isAcepted(entity)) {
				retorno.add(entity);
				if (progress != null) {
					progress.addFounded(entity);
				}
			}
			if (progress != null) {
				progress.addOneProgress();
			}
		}
	}

	@Override
	public void setPrevalence(PrevalenceRepository prevalenceJsonRepository) {
		this.prevalence = prevalenceJsonRepository;
	}

}

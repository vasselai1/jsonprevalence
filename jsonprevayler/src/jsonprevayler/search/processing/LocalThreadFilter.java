package jsonprevayler.search.processing;

import java.util.List;
import java.util.Map;

import jsonprevayler.entity.PrevalenceEntity;
import jsonprevayler.search.PrevalenceFilter;
import jsonprevayler.search.ProgressSearchObserver;

public class LocalThreadFilter <T extends PrevalenceEntity> extends Thread {

	private final Class<T> classe;
	private final PrevalenceFilter<T> filter;
	private final Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository;
	private final List<Long> keysSector;
	private final List<T> retorno;
	
	
	
	public LocalThreadFilter(Class<T> classe, 
							 PrevalenceFilter<T> filter,
							 Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository, 
							 List<Long> keysSector,
							 List<T> retorno) {

		this.classe = classe;
		this.filter = filter;
		this.pojoRepository = pojoRepository;
		this.keysSector = keysSector;
		this.retorno = retorno;
	}

	@Override
	public void run() {
		ProgressSearchObserver<T> progress = filter.getProgressSearchObserver();
		for (Long keyLoop : keysSector) {
			T entity = (T) pojoRepository.get(classe).get(keyLoop);
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

}

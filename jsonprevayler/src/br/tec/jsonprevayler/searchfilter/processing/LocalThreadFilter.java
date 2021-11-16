package br.tec.jsonprevayler.searchfilter.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;

public class LocalThreadFilter <T extends PrevalenceEntity> extends SearchProcessor implements Runnable {

	private final Class<T> classe;
	private final PrevalenceFilter<T> filter;
	private final List<Long> keysSector;
	private final List<T> retorno;
	private List<Throwable> errors = new ArrayList<Throwable>();
	
	
	public LocalThreadFilter(Class<T> classe, PrevalenceFilter<T> filter, List<Long> keysSector, List<T> retorno) {
		this.classe = classe;
		this.filter = filter;
		this.keysSector = keysSector;
		this.retorno = retorno;
	}

	@Override
	public void run() {
		setProgressObserver(filter.getProgressSearchObserver());
		for (Long keyLoop : keysSector) {
			try {
				T entity = (T) prevalence.getPojo(classe, keyLoop);
				if (filter.isAcepted(entity)) {
					if (onlyCount) {
						addCountFounded();
						continue;
					}
					retorno.add(entity);
					addFounded(entity);
				}
				addOneProgress();
			} catch (Exception e) {
				errors.add(e);
			}
		}
	}
	
	public List<Throwable> getErrors() {
		return errors;
	}

	@SuppressWarnings("hiding")
	@Override
	public <T extends PrevalenceEntity> void process(Class<T> classe, PrevalenceFilter<T> filter, List<T> retorno) throws ClassNotFoundException, IOException, ValidationPrevalenceException {
		throw new ValidationPrevalenceException("This method do not is used!");
	}

}
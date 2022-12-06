package br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo;

import java.util.ArrayList;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;
import flexjson.JSONSerializer;

public class FilterJsonOperation <T extends PrevalenceEntity> extends FilterOperation<T> {

	public FilterJsonOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore, searchProcessor);
	}
	
	public FilterJsonOperation<T> set(Class<T> classe, PrevalenceFilter<T> filter) {
		super.classe = classe;
		super.filter = filter;
		return this;
	}

	public String execute() throws InternalPrevalenceException, ValidationPrevalenceException {
		FilterPojoOperation<T> filterPojoOperation = new FilterPojoOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessor);
		filterPojoOperation.set(classe, filter);
		List<String> retorno = new ArrayList<String>();
		for (T entityLoop : filterPojoOperation.execute()) {
			String json = memoryCore.getJson(classe, entityLoop.getId());
			retorno.add(json);
		}
		totalResults = retorno.size();
		return new JSONSerializer().serialize(retorno);	
	}

	@Override
	public String getOperationName() {
		return "FilterJsonOperation_" + classe.getSimpleName() + "_" + filter.getClass().getSimpleName();
	}
	
}
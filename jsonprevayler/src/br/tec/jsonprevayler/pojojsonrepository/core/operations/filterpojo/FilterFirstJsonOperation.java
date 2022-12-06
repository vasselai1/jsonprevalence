package br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public class FilterFirstJsonOperation <T extends PrevalenceEntity> extends FilterOperation<T> {

	public FilterFirstJsonOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore, searchProcessor);
	}
	
	public FilterFirstJsonOperation<T> set(Class<T> classe, FilterFirst<T> filter) {
		super.classe = classe;
		super.filterFirst = filter;
		return this;
	}

	public String execute() throws InternalPrevalenceException, ValidationPrevalenceException {
		FilterFirstPojoOperation<T> filterFirstOperation = new FilterFirstPojoOperation<T>(prevalenceConfigurator, sequenceProvider, memoryCore, fileCore, searchProcessor);
		T entity = filterFirstOperation.set(classe, filterFirst).execute();
		if (entity == null) {
			return null;
		}
		totalResults = 1;
		return memoryCore.getJson(classe, entity.getId());
	}

	@Override
	public String getOperationName() {
		return "FilterFirstPojoOperation" + classe.getSimpleName() + "_" + filter.getClass().getSimpleName();
	}
	
}
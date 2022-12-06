package br.tec.jsonprevayler.pojojsonrepository.core.operations.filterpojo;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.OperationState;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public class FilterFirstPojoOperation <T extends PrevalenceEntity> extends FilterOperation<T> {

	public FilterFirstPojoOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore, searchProcessor);
	}
	
	public FilterFirstPojoOperation<T> set(Class<T> classe, FilterFirst<T> filter) {
		super.classe = classe;
		super.filterFirst = filter;
		return this;
	}

	public T execute() throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, filterFirst, dateProvider.get());
		for (T entityLoop : memoryCore.getValues(classe)) {
			if (filterFirst.isAcepted(entityLoop)) {
				updateStateAssinc(OperationState.FINALIZED, dateProvider.get());
				totalResults = 1;
				return entityLoop;
			}
		}
		updateStateAssinc(OperationState.FINALIZED, dateProvider.get());
		return null;	
	}

	@Override
	public String getOperationName() {
		return "FilterFirstPojoOperation" + classe.getSimpleName() + "_" + filter.getClass().getSimpleName();
	}
	
}
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
import br.tec.jsonprevayler.pojojsonrepository.core.operations.OperationState;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;

public class FilterPojoOperation <T extends PrevalenceEntity> extends FilterOperation<T> {

	public FilterPojoOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore, searchProcessor);
	}
	
	public FilterPojoOperation<T> set(Class<T> classe, PrevalenceFilter<T> filter) {
		super.classe = classe;
		super.filter = filter;
		return this;
	}

	public List<T> execute() throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, filter, dateProvider.get());
		List<T> retorno = new ArrayList<T>();		
		filter.setMemorySearchEngine(memoryCore);
		searchProcessor.setMemorySearchEngine(memoryCore);
		updateStateAssinc(OperationState.INIT_ITERATION, dateProvider.get());
		searchProcessor.process(classe, filter, retorno);		
		updateStateAssinc(OperationState.END_ITERATION, dateProvider.get());
		updateStateAssinc(OperationState.INIT_SORT, dateProvider.get());
		filter.setTotal(retorno.size());
		retorno.sort(filter.getComparator());
		updateStateAssinc(OperationState.END_SORT, dateProvider.get());
		int finalRegister = filter.getFirstResult() + filter.getPageSize();
		if (finalRegister > (retorno.size())) {
			finalRegister = retorno.size();
		}
		if (filter.getPageSize() <= 0) {
			updateStateAssinc(OperationState.FINALIZED, dateProvider.get());
			return retorno;
		}
		updateStateAssinc(OperationState.INIT_PAGINATION, dateProvider.get());
		retorno = retorno.subList(filter.getFirstResult(), finalRegister);
		updateStateAssinc(OperationState.END_PAGINATION, dateProvider.get());
		totalResults = retorno.size();
		updateStateAssinc(OperationState.FINALIZED, dateProvider.get());
		return retorno;		
	}

	@Override
	public String getOperationName() {
		return "FilterPojoOperation_" + classe.getSimpleName() + "_" + filter.getClass().getSimpleName();
	}
	
}
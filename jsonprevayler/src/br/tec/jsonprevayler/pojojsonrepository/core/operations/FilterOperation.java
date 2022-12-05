package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;
import flexjson.JSONSerializer;

public class FilterOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	private final MemoryCore memoryCore;
	private final SearchProcessor searchProcessor;//Precisa ser um por pesquisa!!!
	
	public FilterOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore, SearchProcessor searchProcessor) {
		this.memoryCore = memoryCore;
		this.searchProcessor = searchProcessor;
		this.prevalenceConfigurator = prevalenceConfigurator;
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}

	public Integer count(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return memoryCore.count(classe);
	}
	
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		
		List<T> retorno = new ArrayList<T>();
		searchProcessor.setOnlyCount(true);
		searchProcessor.setMemorySearchEngine(memoryCore);
		searchProcessor.process(classe, filter, retorno);	
		return searchProcessor.getTotalFounded();
	}
	
	public String listJson(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new JSONSerializer().serialize(memoryCore.listJson(classe));
	}	
	
	public List<T> listPojo(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new ArrayList<T>(memoryCore.getValues(classe));
	}	
	
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return list(classe, filter);
	}
	
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		List<T> filtrados = list(classe, filter);
		List<String> retorno = new ArrayList<String>();
		for (T entityLoop : filtrados) {
			String json = memoryCore.getJson(classe, entityLoop.getId());
			retorno.add(json);
		}
		return new JSONSerializer().serialize(retorno);
	}	
	
	private List<T> list(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, filter, new Date());
		List<T> retorno = new ArrayList<T>();		
		filter.setMemorySearchEngine(memoryCore);
		searchProcessor.setMemorySearchEngine(memoryCore);
		updateStateAssinc(OperationState.INIT_ITERATION, new Date());
		searchProcessor.process(classe, filter, retorno);		
		updateStateAssinc(OperationState.END_ITERATION, new Date());
		updateStateAssinc(OperationState.INIT_SORT, new Date());
		filter.setTotal(retorno.size());
		retorno.sort(filter.getComparator());
		updateStateAssinc(OperationState.END_SORT, new Date());
		int finalRegister = filter.getFirstResult() + filter.getPageSize();
		if (finalRegister > (retorno.size())) {
			finalRegister = retorno.size();
		}
		if (filter.getPageSize() <= 0) {
			updateStateAssinc(OperationState.FINALIZED, new Date());
			return retorno;
		}
		updateStateAssinc(OperationState.INIT_PAGINATION, new Date());
		retorno = retorno.subList(filter.getFirstResult(), finalRegister);
		updateStateAssinc(OperationState.END_PAGINATION, new Date());
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return retorno;	
	}	

	public T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		return getFirst(classe, filterFirst, true);
	}
	
	public String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		T entity = getFirst(classe, filterFirst, false);
		if (entity == null) {
			return null;
		}
		return memoryCore.getJson(classe, entity.getId());
	}
	
	private T getFirst(Class<T> classe, FilterFirst<T> filterFirst, boolean secureCopy) throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, filterFirst, new Date());
		for (T entityLoop : memoryCore.getValues(classe)) {
			if (filterFirst.isAcepted(entityLoop)) {
				updateStateAssinc(OperationState.FINALIZED, new Date());
				return entityLoop;
			}
		}
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return null;
	} 
	
}
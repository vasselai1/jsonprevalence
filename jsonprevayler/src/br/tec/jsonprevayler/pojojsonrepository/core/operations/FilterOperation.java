package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.SearchProcessor;
import flexjson.JSONSerializer;

public class FilterOperation <T extends PrevalenceEntity> {

	private final MemoryCore memoryCore;
	private final SearchProcessor searchProcessor;//Precisa ser um por pesquisa!!!
	
	public FilterOperation(MemoryCore memoryCore, SearchProcessor searchProcessor) {
		this.memoryCore = memoryCore;
		this.searchProcessor = searchProcessor;
	}

	public Integer count(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return memoryCore.count(classe);
	}
	
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		List<T> retorno = new ArrayList<T>();
		searchProcessor.setOnlyCount(true);
		searchProcessor.setMemorySearchEngine(memoryCore);
		searchProcessor.process(classe, filter, retorno);	
		return searchProcessor.getTotalFounded();
	}
	
	public String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		return new JSONSerializer().serialize(memoryCore.listJson(classe));
	}	
	
	public List<T> listPojo(Class<T> classe) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return new ArrayList<T>(memoryCore.getValues(classe));
	}	
	
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return list(classe, filter);
	}
	
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		List<T> filtrados = list(classe, filter);
		List<String> retorno = new ArrayList<String>();
		for (T entityLoop : filtrados) {
			String json = memoryCore.getJson(classe, entityLoop.getId());
			retorno.add(json);
		}
		return new JSONSerializer().serialize(retorno);
	}	
	
	private List<T> list(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		List<T> retorno = new ArrayList<T>();		
		filter.setMemorySearchEngine(memoryCore);
		searchProcessor.setMemorySearchEngine(memoryCore);
		searchProcessor.process(classe, filter, retorno);		
		filter.setTotal(retorno.size());
		retorno.sort(filter.getComparator());
		int finalRegister = filter.getFirstResult() + filter.getPageSize();
		if (finalRegister > (retorno.size())) {
			finalRegister = retorno.size();
		}
		if (filter.getPageSize() <= 0) {
			return retorno;
		}
		return retorno.subList(filter.getFirstResult(), finalRegister);	
	}	

	public T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		return getFirst(classe, filterFirst, true);
	}
	
	public String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		T entity = getFirst(classe, filterFirst, false);
		if (entity == null) {
			return null;
		}
		return memoryCore.getJson(classe, entity.getId());
	}
	
	private T getFirst(Class<T> classe, FilterFirst<T> filterFirst, boolean secureCopy) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		for (T entityLoop : memoryCore.getValues(classe)) {
			if (filterFirst.isAcepted(entityLoop)) {
				return entityLoop;
			}
		}
		return null;
	} 
	
}
package br.org.pr.jsonprevayler.pojojsonrepository.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.org.pr.jsonprevayler.searchfilter.FilterFirst;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;
import br.org.pr.jsonprevayler.searchfilter.processing.SearchProcessor;
import br.org.pr.jsonprevayler.util.ObjectCopyUtil;
import flexjson.JSONSerializer;

public class FilterOperation <T extends PrevalenceEntity> {

	private final MemoryCore memoryCore;
	private final SearchProcessor searchProcessor;//Precisa ser um por pesquisa!!!
	
	public FilterOperation(MemoryCore memoryCore, SearchProcessor searchProcessor) {
		this.memoryCore = memoryCore;
		this.searchProcessor = searchProcessor;
	}

	public Integer count(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = MemoryCore.getClassRepository(classe);
		return memoryCore.count(classe);
	}
	
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = MemoryCore.getClassRepository(classe);
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
		return ObjectCopyUtil.copyList(classe, memoryCore.getValues(classe));
	}	
	
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return list(classe, filter, true);
	}
	
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		classe = MemoryCore.getClassRepository(classe);
		List<T> filtrados = list(classe, filter, false);
		List<String> retorno = new ArrayList<String>();
		for (T entityLoop : filtrados) {
			String json = memoryCore.getJson(classe, entityLoop.getId());
			retorno.add(json);
		}
		return new JSONSerializer().serialize(retorno);
	}	
	
	private List<T> list(Class<T> classe, PrevalenceFilter<T> filter, boolean secureCopy) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		classe = MemoryCore.getClassRepository(classe);		
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
		if (!secureCopy) {
			retorno.subList(filter.getFirstResult(), finalRegister);
		}
		return ObjectCopyUtil.copyList(classe, retorno.subList(filter.getFirstResult(), finalRegister));
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
				if (secureCopy) {
					return ObjectCopyUtil.copyEntity(entityLoop);
				}
				return entityLoop;
			}
		}
		return null;
	} 
	
}
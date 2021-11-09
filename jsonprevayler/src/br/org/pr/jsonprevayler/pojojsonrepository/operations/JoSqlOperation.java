package br.org.pr.jsonprevayler.pojojsonrepository.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.org.pr.jsonprevayler.util.ObjectCopyUtil;

public class JoSqlOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	private final MemoryCore memoryCore;
	
	public JoSqlOperation(MemoryCore memoryCore) {
		this.memoryCore = memoryCore;
	}

	private Query initQueryJoSql(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws IOException, ValidationPrevalenceException, QueryParseException {		
		Query query = new Query();
		query.parse(joSqlQuery);
		if (parametersBind != null) {
			for (String key : parametersBind.keySet()) {
				query.setVariable(key, parametersBind.get(key));
			}
		}
		return query;
	}
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = MemoryCore.getClassRepository(classe);
		Query query = initQueryJoSql(classe, joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		List<?> result = queryResults.getResults();
		return returnSercure(classe, result);
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = MemoryCore.getClassRepository(classe);
		Query query = initQueryJoSql(classe, joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		List<?> result = queryResults.getHavingResults();
		return returnSercure(classe, result);
	}

	@SuppressWarnings("unchecked")
	private List<?> returnSercure(Class<T> classe, List<?> result) throws ClassNotFoundException, IOException {
		if (!isPrevalentInstances(result)) {
			return result;
		}
		return ObjectCopyUtil.copyList(classe, (Collection<T>) result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		classe = MemoryCore.getClassRepository(classe);
		Query query = initQueryJoSql(classe, joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		Map result = queryResults.getGroupByResults();
		boolean isValuesPrevalentInstances = false;
		boolean isKeysPrevalentInstances = false;
		if (result == null) {
			return result;
		}
		for (Object objectLoop : result.values()) {
			if (objectLoop instanceof PrevalenceEntity) {
				isValuesPrevalentInstances = true;
				break;
			}
		}
		for (Object objectLoop : result.keySet()) {
			if (objectLoop instanceof PrevalenceEntity) {
				isKeysPrevalentInstances = true;
				break;
			}
		}		
		if (isValuesPrevalentInstances || isKeysPrevalentInstances) {
			 Map copiedMap = new HashMap();
			 for (Object keyLoop : result.keySet()) {
				 copiedMap.put(ObjectCopyUtil.copyEntity(keyLoop), ObjectCopyUtil.copyEntity(result.get(keyLoop)));
			 }
			 return copiedMap;
		}
		return result;
	}	
	
}
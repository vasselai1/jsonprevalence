package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.util.ObjectCopyUtil;

public class JoSqlOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	private final MemoryCore memoryCore;
	
	public JoSqlOperation(MemoryCore memoryCore) {
		this.memoryCore = memoryCore;
	}

	private Query initQueryJoSql(String joSqlQuery, Map<String, Object> parametersBind) throws IOException, ValidationPrevalenceException, QueryParseException {		
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
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		List<?> result = queryResults.getResults();
		return returnSercure(classe, result);
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
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

	@SuppressWarnings("rawtypes")
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException {
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		return queryResults.getGroupByResults();
	}	
	
}
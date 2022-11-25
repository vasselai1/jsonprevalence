package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;

public class JoSqlOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	private final MemoryCore memoryCore;
		
	public JoSqlOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		this.memoryCore = memoryCore;
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
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
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException, NoSuchAlgorithmException {
		initStateAssinc(classe, classe, new Date());
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return queryResults.getResults();		
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException, NoSuchAlgorithmException {
		initStateAssinc(classe, classe, new Date());
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return queryResults.getHavingResults();
	}

	@SuppressWarnings("rawtypes")
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, InterruptedException, NoSuchAlgorithmException {
		initStateAssinc(classe, classe, new Date());
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = query.execute(memoryCore.getValues(classe));
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return queryResults.getGroupByResults();
	}
	
}
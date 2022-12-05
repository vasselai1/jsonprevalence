package br.tec.jsonprevayler.pojojsonrepository.core.operations;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.josql.Query;
import org.josql.QueryResults;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.util.LoggerUtil;

public class JoSqlOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {

	private final MemoryCore memoryCore;
	private final Logger logger = Logger.getLogger(getClass().getName());
		
	public JoSqlOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		this.memoryCore = memoryCore;
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}

	private Query initQueryJoSql(String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {	
		Query query = new Query();
		try {
			query.parse(joSqlQuery);
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Error in parse joSqlQuery %1$s", joSqlQuery);
		}
		if (parametersBind != null) {
			for (String key : parametersBind.keySet()) {
				query.setVariable(key, parametersBind.get(key));
			}
		}
		return query;
	}
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, classe, new Date());
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = null;
		try {
			queryResults = query.execute(memoryCore.getValues(classe));
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Error in execute list joSqlQuery %1$s with parameters ", joSqlQuery, parametersBind);
		}
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return queryResults.getResults();		
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, classe, new Date());
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = null;
		try {
			queryResults = query.execute(memoryCore.getValues(classe));
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Error in execute having joSqlQuery %1$s with parameters ", joSqlQuery, parametersBind);
		}
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return queryResults.getHavingResults();
	}

	@SuppressWarnings("rawtypes")
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, classe, new Date());
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = null;
		try {
			queryResults = query.execute(memoryCore.getValues(classe));
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Error in execute group joSqlQuery %1$s with parameters ", joSqlQuery, parametersBind);
		}
		updateStateAssinc(OperationState.FINALIZED, new Date());
		return queryResults.getGroupByResults();
	}
	
}
package br.tec.jsonprevayler.pojojsonrepository.core.operations.josql;

import java.util.Map;

import org.josql.Query;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.CommonsOperations;
import br.tec.jsonprevayler.util.LoggerUtil;

public abstract class JoSqlOperation <T extends PrevalenceEntity> extends CommonsOperations<T> {
	
	protected Class<T> classe;
	protected String joSqlQuery;
	protected Map<String, Object> parametersBind;
		
	protected JoSqlOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil, MemoryCore memoryCore, FileCore fileCore) {
		setCore(prevalenceConfigurator, memoryCore, fileCore, sequenceUtil);
	}

	protected JoSqlOperation<T> set(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) {
		this.classe = classe;
		this.joSqlQuery = joSqlQuery;
		this.parametersBind = parametersBind;
		return this;
	}
	
	protected Query initQueryJoSql(String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {	
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
	
}
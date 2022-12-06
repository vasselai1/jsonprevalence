package br.tec.jsonprevayler.pojojsonrepository.core.operations.josql;

import java.util.Map;

import org.josql.Query;
import org.josql.QueryResults;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.FileCore;
import br.tec.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.OperationState;
import br.tec.jsonprevayler.util.LoggerUtil;

public class JoSqlGroupInMapOperation <T extends PrevalenceEntity> extends JoSqlOperation<T> {

	public JoSqlGroupInMapOperation(PrevalenceConfigurator prevalenceConfigurator, SequenceProvider sequenceUtil,MemoryCore memoryCore, FileCore fileCore) {
		super(prevalenceConfigurator, sequenceUtil, memoryCore, fileCore);
	}
	
	public JoSqlGroupInMapOperation<T> set(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) {
		return set(classe, joSqlQuery, parametersBind);		
	}
	
	@SuppressWarnings("rawtypes")
	public Map execute() throws InternalPrevalenceException, ValidationPrevalenceException {
		initStateAssinc(classe, classe, dateProvider.get());
		Query query = initQueryJoSql(joSqlQuery, parametersBind);
		QueryResults queryResults = null;
		try {
			queryResults = query.execute(memoryCore.getValues(classe));
		} catch (Exception ex) {
			throw LoggerUtil.error(logger, ex, "Error in execute group joSqlQuery %1$s with parameters ", joSqlQuery, parametersBind);
		}
		updateStateAssinc(OperationState.FINALIZED, dateProvider.get());
		return queryResults.getGroupByResults();		
	}

	@Override
	public String getOperationName() {		
		return "JoSqlGroupInMapOperation";
	}	

}
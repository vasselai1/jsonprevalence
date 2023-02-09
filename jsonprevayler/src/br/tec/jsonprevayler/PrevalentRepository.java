package br.tec.jsonprevayler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.OperationsControler;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.CustomOperation;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

/**
 * Prevalence (file json and memory pojo) CRUD for java POJO with version control, geopoints distance calc, phonetic math, history, journal, observer and backup. 
 * File system uses Json in balanced directories.
 * @author vasselai1
 */
public class PrevalentRepository {

	private final PrevalenceConfigurator prevalenceConfigurator;

	public PrevalentRepository(PrevalenceConfigurator prevalenceConfigurator) {
		this.prevalenceConfigurator = prevalenceConfigurator;
	}

	public <T extends PrevalenceEntity> void save(T entity) throws InternalPrevalenceException, ValidationPrevalenceException {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).save(entity);
	}
	
	public <T extends PrevalenceEntity> void update(T entity) throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).update(entity);
	}
	
	public <T extends PrevalenceEntity> void delete(T entity) throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).delete(entity);
	}

	public <Z extends CustomOperation> Z newAtomicOperation(Class<Z> classeCustomOperation)  throws InternalPrevalenceException, ValidationPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		return new OperationsControler<PrevalenceEntity>(prevalenceConfigurator, getSearchProcessorFactory()).newCustomOperation(classeCustomOperation);
	}
	
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).count(classe);
	}
	public <T extends PrevalenceEntity> Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).count(classe, filter);
	}
	
	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getPojo(classe, id);
	} 
	
	public <T extends PrevalenceEntity> String getJson(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getJson(classe, id);
	}
	
	public <T extends PrevalenceEntity> T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getFirstPojo(classe, filterFirst);
	}
	
	public <T extends PrevalenceEntity> String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getFirstJson(classe, filterFirst);
	}
	
	public <T extends PrevalenceEntity> List<T> listPojo(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listPojo(classe);
	}
	public <T extends PrevalenceEntity> List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listPojo(classe, filter);
	}

	public <T extends PrevalenceEntity> String listJson(Class<T> classe) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listJson(classe);
	}
	public <T extends PrevalenceEntity> String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listJson(classe, filter);
	}	
	
	public <T extends PrevalenceEntity> List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).joSqlQueryList(classe, joSqlQuery, parametersBind);
	}

	public <T extends PrevalenceEntity> List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).joSqlQueryHavingList(classe, joSqlQuery, parametersBind);
	}

	@SuppressWarnings({ "rawtypes" })
	public <T extends PrevalenceEntity> Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).joSqlQueryGroupMap(classe, joSqlQuery, parametersBind);
	}
	
	public <T extends PrevalenceEntity> Map<Date, String> listVersions(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listVersions(classe, id);
	}
	
	public <T extends PrevalenceEntity> void overwrite(Class<T> classe, Long id, Date versionDate) throws InternalPrevalenceException, ValidationPrevalenceException {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).overwrite(classe, id, versionDate);
	}

	public <T extends PrevalenceEntity> void overwriteLast(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).overwrite(classe, id, null);
	}	
	
	public <T extends PrevalenceEntity> void deleteHistoryAndDetails(Class<T> classe, Long id) throws InternalPrevalenceException, ValidationPrevalenceException {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).deleteHistoryAndDetails(classe, id);
	}
	
	public void registerObserver(PrevalenceChangeObserver observer) {
		OperationsControler.registerObserver(observer);
	}

	public void deRegisterObserver(PrevalenceChangeObserver observer) {
		OperationsControler.deRegisterObserver(observer);
	}	
	
	private SearchProcessorFactory getSearchProcessorFactory() throws InternalPrevalenceException {
		return prevalenceConfigurator.getSearchProcessorFactory();
	}
	
}
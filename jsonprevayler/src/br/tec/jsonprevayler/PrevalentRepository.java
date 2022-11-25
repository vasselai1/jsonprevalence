package br.tec.jsonprevayler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.tec.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.tec.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.CustomOperation;
import br.tec.jsonprevayler.pojojsonrepository.core.operations.OperationsControler;
import br.tec.jsonprevayler.searchfilter.FilterFirst;
import br.tec.jsonprevayler.searchfilter.PrevalenceFilter;
import br.tec.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;

/**
 * Prevalence (file and memory) CRUD with version control, history, journal and observer. 
 * @author vasselai1
 */
public class PrevalentRepository {

	private final PrevalenceConfigurator prevalenceConfigurator;

	public PrevalentRepository(PrevalenceConfigurator prevalenceConfigurator) {
		this.prevalenceConfigurator = prevalenceConfigurator;
	}

	public <T extends PrevalenceEntity> void save(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, DeprecatedPrevalenceEntityVersionException, InstantiationException, NoSuchMethodException, Exception {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).save(entity);
	}
	
	public <T extends PrevalenceEntity> void update(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, InstantiationException, NoSuchMethodException, Exception {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).update(entity);
	}
	
	public <T extends PrevalenceEntity> void delete(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InternalPrevalenceException, InstantiationException, NoSuchMethodException, Exception {
		new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).delete(entity);
	}

	public <Z extends CustomOperation> Z newAtomicOperation(Class<Z> classeCustomOperation)  throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, InstantiationException, NoSuchMethodException, Exception {
		return new OperationsControler<PrevalenceEntity>(prevalenceConfigurator, getSearchProcessorFactory()).newAtomicOperation(classeCustomOperation);
	}
	
	public <T extends PrevalenceEntity> Integer count(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).count(classe);
	}
	public <T extends PrevalenceEntity> Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, ValidationPrevalenceException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).count(classe, filter);
	}
	
	public <T extends PrevalenceEntity> T getPojo(Class<T> classe, Long id) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, ValidationPrevalenceException, InterruptedException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getPojo(classe, id);
	} 
	
	public <T extends PrevalenceEntity> String getJson(Class<T> classe, Long id) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ValidationPrevalenceException, IOException, InterruptedException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getJson(classe, id);
	}
	
	public <T extends PrevalenceEntity> T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException, NoSuchAlgorithmException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getFirstPojo(classe, filterFirst);
	}
	
	public <T extends PrevalenceEntity> String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException, NoSuchAlgorithmException {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).getFirstJson(classe, filterFirst);
	}
	
	public <T extends PrevalenceEntity> List<T> listPojo(Class<T> classe) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listPojo(classe);
	}
	public <T extends PrevalenceEntity> List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listPojo(classe, filter);
	}

	public <T extends PrevalenceEntity> String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listJson(classe);
	}
	public <T extends PrevalenceEntity> String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).listJson(classe, filter);
	}	
	
	public <T extends PrevalenceEntity> List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).joSqlQueryList(classe, joSqlQuery, parametersBind);
	}

	public <T extends PrevalenceEntity> List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).joSqlQueryHavingList(classe, joSqlQuery, parametersBind);
	}

	@SuppressWarnings({ "rawtypes" })
	public <T extends PrevalenceEntity> Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return new OperationsControler<T>(prevalenceConfigurator, getSearchProcessorFactory()).joSqlQueryGroupMap(classe, joSqlQuery, parametersBind);
	}
	
	public void registerObserver(PrevalenceChangeObserver observer) {
		OperationsControler.registerObserver(observer);
	}

	public void deRegisterObserver(PrevalenceChangeObserver observer) {
		OperationsControler.deRegisterObserver(observer);
	}	
	
	private SearchProcessorFactory getSearchProcessorFactory() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		return prevalenceConfigurator.getSearchProcessorFactory();
	}
	
}
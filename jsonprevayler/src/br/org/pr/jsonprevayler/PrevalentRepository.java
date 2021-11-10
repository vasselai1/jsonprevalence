package br.org.pr.jsonprevayler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.josql.QueryExecutionException;
import org.josql.QueryParseException;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.DeprecatedPrevalenceEntityVersionException;
import br.org.pr.jsonprevayler.exceptions.InternalPrevalenceException;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.PrevalenceChangeObserver;
import br.org.pr.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.org.pr.jsonprevayler.pojojsonrepository.operations.CustomOperation;
import br.org.pr.jsonprevayler.pojojsonrepository.operations.OperationsControler;
import br.org.pr.jsonprevayler.searchfilter.FilterFirst;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;

/**
 * Prevalence (file and memory) CRUD with version control, history, journal and observer. 
 * @author vasselai1
 */
public class PrevalentRepository <T extends PrevalenceEntity> {

	private final PrevalenceConfigurator prevalenceConfigurator;

	public PrevalentRepository(PrevalenceConfigurator prevalenceConfigurator) {
		this.prevalenceConfigurator = prevalenceConfigurator;
	}

	public void save(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, DeprecatedPrevalenceEntityVersionException, InstantiationException, NoSuchMethodException, Exception {
		getNewConfiguredControler().save(entity);
	}
	public void saveDeep(T entity) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAlgorithmException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException, InstantiationException, NoSuchMethodException, Exception {
		getNewConfiguredControler().saveDeep(entity);
	}
	
	public void update(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, InstantiationException, NoSuchMethodException, Exception {
		getNewConfiguredControler().update(entity);
	}
	public  void updateDeep(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, InstantiationException, NoSuchMethodException, Exception {
		getNewConfiguredControler().updateDeep(entity);
	}	
	
	public void delete(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InternalPrevalenceException, InstantiationException, NoSuchMethodException, Exception {
		getNewConfiguredControler().delete(entity);
	}

	public <Z extends CustomOperation> Z newAtomicOperation(Class<Z> classeCustomOperation)  throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException, InstantiationException, NoSuchMethodException, Exception {
		return getNewConfiguredControler().newAtomicOperation(classeCustomOperation);
	}
	
	public Integer count(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().count(classe);
	}
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, ValidationPrevalenceException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().count(classe, filter);
	}
	
	public T getPojo(Class<T> classe, Long id) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, ValidationPrevalenceException, InterruptedException {
		return getNewConfiguredControler().getPojo(classe, id);
	} 
	
	public String getJson(Class<T> classe, Long id) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ValidationPrevalenceException, IOException, InterruptedException {
		return getNewConfiguredControler().getJson(classe, id);
	}
	
	public T getFirstPojo(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		return getNewConfiguredControler().getFirstPojo(classe, filterFirst);
	}
	
	public String getFirstJson(Class<T> classe, FilterFirst<T> filterFirst) throws ValidationPrevalenceException, ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, InterruptedException {
		return getNewConfiguredControler().getFirstJson(classe, filterFirst);
	}
	
	public List<T> listPojo(Class<T> classe) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().listPojo(classe);
	}
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().listPojo(classe, filter);
	}

	public String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, Exception {
		return getNewConfiguredControler().listJson(classe);
	}
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().listJson(classe, filter);
	}	
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().joSqlQueryList(classe, joSqlQuery, parametersBind);
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().joSqlQueryHavingList(classe, joSqlQuery, parametersBind);
	}

	@SuppressWarnings({ "rawtypes" })
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Exception {
		return getNewConfiguredControler().joSqlQueryGroupMap(classe, joSqlQuery, parametersBind);
	}
	
	public void registerObserver(PrevalenceChangeObserver observer) {
		OperationsControler.registerObserver(observer);
	}

	public void deRegisterObserver(PrevalenceChangeObserver observer) {
		OperationsControler.deRegisterObserver(observer);
	}	
	
	private OperationsControler<T> getNewConfiguredControler() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		return new OperationsControler<T>(prevalenceConfigurator.getPrevalencePath(), prevalenceConfigurator.getPrevalencePath(), prevalenceConfigurator.getSearchProcessorFactory().createNewSearchProcessor(), prevalenceConfigurator.getInitializationMemoryCoreType());
	}
	
}
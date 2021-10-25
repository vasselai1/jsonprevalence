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
import br.org.pr.jsonprevayler.pojojsonrepository.operations.OperationsControler;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SearchProcessorFactory;
import br.org.pr.jsonprevayler.searchfilter.processing.searchprocessorfactory.SingleThreadSearchProcessorFactory;

/**
 * Prevalence (file and memory) CRUD with version control, history, journal and observer. 
 * @author vasselai1
 */
public class PrevalentJsonRepository <T extends PrevalenceEntity> {

	private final String prevalencePath;
	private final String systemName;
	private final SearchProcessorFactory searchProcessorFactory;

	public PrevalentJsonRepository(String prevalencePath, String systemName) {
		this.prevalencePath = prevalencePath;
		this.systemName = systemName;
		searchProcessorFactory = new SingleThreadSearchProcessorFactory();
	}

	public PrevalentJsonRepository(String prevalencePath, String systemName, SearchProcessorFactory searchProcessorFactory) {
		this.prevalencePath = prevalencePath;
		this.systemName = systemName;
		this.searchProcessorFactory = searchProcessorFactory;
	}	

	public void save(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, DeprecatedPrevalenceEntityVersionException {
		getNewConfiguredControler().save(entity);
	}
	public void save(T entity, String author) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException {
		getNewConfiguredControler().save(entity, author);
	}
	public void saveDeep(T entity) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAlgorithmException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		getNewConfiguredControler().saveDeep(entity);
	}
	public void saveDeep(T entity, String author) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAlgorithmException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		getNewConfiguredControler().saveDeep(entity, author);
	}
	
	public void update(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		getNewConfiguredControler().update(entity);
	}
	public  void update(T entity, String author) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		getNewConfiguredControler().update(entity, author);
	}
	public  void updateDeep(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		getNewConfiguredControler().updateDeep(entity);
	}	
	public  void updateDeep(T entity, String author) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		getNewConfiguredControler().updateDeep(entity, author);
	}
	
	public void delete(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InternalPrevalenceException {
		getNewConfiguredControler().delete(entity);
	}

	public Integer count(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException {
		return getNewConfiguredControler().count(classe);
	}
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, ValidationPrevalenceException, ClassNotFoundException {
		return getNewConfiguredControler().count(classe, filter);
	}
	
	public List<T> listPojo(Class<T> classe) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException {
		return getNewConfiguredControler().listPojo(classe);
	}
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException {
		return getNewConfiguredControler().listPojo(classe, filter);
	}

	public String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException {
		return getNewConfiguredControler().listJson(classe);
	}
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException {
		return getNewConfiguredControler().listJson(classe, filter);
	}	
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException {
		return getNewConfiguredControler().joSqlQueryList(classe, joSqlQuery, parametersBind);
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException {
		return getNewConfiguredControler().joSqlQueryHavingList(classe, joSqlQuery, parametersBind);
	}

	@SuppressWarnings({ "rawtypes" })
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException {
		return getNewConfiguredControler().joSqlQueryGroupMap(classe, joSqlQuery, parametersBind);
	}
	
	public void registerObserver(PrevalenceChangeObserver observer) {
		OperationsControler.registerObserver(observer);
	}
	
	private OperationsControler<T> getNewConfiguredControler() {
		return new OperationsControler<T>(prevalencePath, systemName, searchProcessorFactory.createNewSearchProcessor());
	}
}
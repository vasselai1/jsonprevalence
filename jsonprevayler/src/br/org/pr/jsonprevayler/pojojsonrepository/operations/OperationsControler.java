package br.org.pr.jsonprevayler.pojojsonrepository.operations;

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
import br.org.pr.jsonprevayler.infrastrutuctre.SequenceProvider;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;
import br.org.pr.jsonprevayler.pojojsonrepository.core.MemoryCore;
import br.org.pr.jsonprevayler.searchfilter.PrevalenceFilter;
import br.org.pr.jsonprevayler.searchfilter.processing.SearchProcessor;

public class OperationsControler <T extends PrevalenceEntity> {
 	
	private final FileCore fileCore;
	private final MemoryCore memoryCore;	
	private final SequenceProvider sequenceProvider;	
	private final SaveOperation<T> saveOperation;
	private final UpdateOperation<T> updateOperation;
	private final DeleteOperation<T> deleteOperation;
	private final FilterOperation<T> filterOperation;
	private final JoSqlOperation<T> joSqlOperation;

	public OperationsControler(String prevalencePath, String systemName, SearchProcessor searchProcessor) {
		fileCore = new FileCore(prevalencePath, systemName);
		memoryCore = new MemoryCore(fileCore);
		sequenceProvider = new SequenceProvider(fileCore.getSystemPath());		
		saveOperation = new SaveOperation<T>(sequenceProvider, memoryCore, fileCore);
		updateOperation = new UpdateOperation<T>(sequenceProvider, memoryCore, fileCore);
		deleteOperation = new DeleteOperation<T>(memoryCore, fileCore, sequenceProvider);
		filterOperation = new FilterOperation<T>(memoryCore, searchProcessor);
		joSqlOperation = new JoSqlOperation<T>(memoryCore);
	}

	public void save(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, DeprecatedPrevalenceEntityVersionException {
		saveOperation.set(entity, null, false).execute();
	}
	public void save(T entity, String author) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, InternalPrevalenceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException {
		saveOperation.set(entity, author, false).execute();
	}
	public void saveDeep(T entity) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAlgorithmException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		saveOperation.set(entity, null, true).execute();
	}
	public void saveDeep(T entity, String author) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchAlgorithmException, IOException, InternalPrevalenceException, DeprecatedPrevalenceEntityVersionException {
		saveOperation.set(entity, author, true).execute();
	}
	
	public void update(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		updateOperation.set(entity, null, false).execute();
	}
	public  void update(T entity, String author) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		updateOperation.set(entity, author,false).execute();
	}
	public  void updateDeep(T entity) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		updateOperation.set(entity, null, true).execute();
	}	
	public  void updateDeep(T entity, String author) throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		updateOperation.set(entity, author, true).execute();
	}
	
	public void delete(T entity) throws ValidationPrevalenceException, IOException, NoSuchAlgorithmException, ClassNotFoundException, DeprecatedPrevalenceEntityVersionException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InternalPrevalenceException {
		deleteOperation.set(entity, null).execute();
	}
	
	public void execute(CustomOperation customOperation)  throws ValidationPrevalenceException, IOException, ClassNotFoundException, NoSuchAlgorithmException, DeprecatedPrevalenceEntityVersionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, InternalPrevalenceException {
		
	}
	
	public Integer count(Class<T> classe) throws IOException, ValidationPrevalenceException, ClassNotFoundException {
		return filterOperation.count(classe);
	}
	public Integer count(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, ValidationPrevalenceException, ClassNotFoundException {
		return filterOperation.count(classe, filter);
	}
	
	public List<T> listPojo(Class<T> classe) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException {
		return filterOperation.listPojo(classe);
	}
	public List<T> listPojo(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException {
		return filterOperation.listPojo(classe, filter);
	}

	public String listJson(Class<T> classe) throws IOException, ValidationPrevalenceException {
		return filterOperation.listJson(classe);
	}
	public String listJson(Class<T> classe, PrevalenceFilter<T> filter) throws IOException, InterruptedException, ClassNotFoundException, ValidationPrevalenceException {
		return filterOperation.listJson(classe, filter);
	}	
	
	public List<?> joSqlQueryList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException {
		return joSqlOperation.joSqlQueryList(classe, joSqlQuery, parametersBind);
	}

	public List<?> joSqlQueryHavingList(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException {
		return joSqlOperation.joSqlQueryHavingList(classe, joSqlQuery, parametersBind);
	}

	@SuppressWarnings({ "rawtypes" })
	public Map joSqlQueryGroupMap(Class<T> classe, String joSqlQuery, Map<String, Object> parametersBind) throws ValidationPrevalenceException, IOException, QueryParseException, QueryExecutionException, ClassNotFoundException {
		return joSqlOperation.joSqlQueryGroupMap(classe, joSqlQuery, parametersBind);
	}
	
	public static void registerObserver(PrevalenceChangeObserver observer) {
		MemoryCore.register(observer);
	}
	
}
package br.org.pr.jsonprevayler.infrastrutuctre.normalization;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.org.pr.jsonprevayler.PrevalentRepository;
import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.infrastrutuctre.configuration.PrevalenceConfigurator;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;

public class IntegrityInspector {

	private final PrevalenceConfigurator prevalenceConfigurator;
	private final FileCore fileCore; 
	
	public IntegrityInspector(PrevalenceConfigurator prevalenceConfigurator) {
		this.prevalenceConfigurator = prevalenceConfigurator;
		fileCore = new FileCore(prevalenceConfigurator.getPrevalencePath(), prevalenceConfigurator.getSystemName());
	}

	@SuppressWarnings("unchecked")
	public <T extends PrevalenceEntity> List<? extends PrevalenceEntity> listPrevalentRelations(T entity) throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, InterruptedException {
		List<T> directRelations = new ArrayList<>();
		List<Class<? extends PrevalenceEntity>> prevalentClasses = listAllPrevalentClasses();
		prevalentClasses = filterClasses(entity.getClass(), prevalentClasses);
		for (Class<? extends PrevalenceEntity> classLoop : prevalentClasses) {
			directRelations.addAll((Collection<? extends T>) filterEntities(entity, classLoop));
		}
		return directRelations;
	}

	public <T extends PrevalenceEntity> void validateExcluision(T entity)  throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, InterruptedException {
		List<? extends PrevalenceEntity> relations = listPrevalentRelations(entity);
		if (relations.isEmpty()) {
			return;
		}
		throw new ValidationPrevalenceException("The entity " + entity + " do not deleted because it's related in this other entities: "  + relations);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends PrevalenceEntity> List<? extends PrevalenceEntity> filterEntities(T entity, Class<? extends PrevalenceEntity> classePrevalent) throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, InterruptedException {
		List<T> prevalentClassesForEntity = new ArrayList<>();
		PrevalentRepository prevalentLoop = new PrevalentRepository(prevalenceConfigurator);
		List<? extends PrevalenceEntity> allEntities = prevalentLoop.listPojo(classePrevalent);
		for (PrevalenceEntity entityLoop : allEntities) {
			JsonSerializationInstructions serializationInstrucions = PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(entityLoop);
			for (PrevalenceEntity prevalenceEntityVerifyLoop : serializationInstrucions.getPrevalentObjects()) {
				if (!prevalenceEntityVerifyLoop.getClass().getCanonicalName().equals(entity.getClass().getCanonicalName())) {
					continue;
				}
				if (prevalenceEntityVerifyLoop.getId().equals(entity.getId())) {
					prevalentClassesForEntity.add((T) prevalenceEntityVerifyLoop);
				}
			}
		}
		return prevalentClassesForEntity;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Class<? extends PrevalenceEntity>> listAllPrevalentClasses() {		
		File prevalenceDir = fileCore.getPrevalenceDir();		
		List<Class<? extends PrevalenceEntity>> prevalentClasses = new ArrayList<>();
		for (File directoryLoop : prevalenceDir.listFiles()) {
			if (!directoryLoop.isDirectory()) {
				continue;
			}
			try {
				Class classe = Class.forName(directoryLoop.getName());
				if (PrevalenceEntity.class.isAssignableFrom(classe)) {
					prevalentClasses.add((Class<? extends PrevalenceEntity>) classe);
				}
			} catch (Exception e) {
				continue;
			}
		}		
		return prevalentClasses;
	}	
	
	@SuppressWarnings("rawtypes")
	public List<Class<? extends PrevalenceEntity>> filterClasses(Class<? extends PrevalenceEntity> entityClass, List<Class<? extends PrevalenceEntity>> prevalentClasses) throws ClassNotFoundException {
		List<Class<? extends PrevalenceEntity>> filtredClasses = new ArrayList<Class<? extends PrevalenceEntity>>();
		for (Class<? extends PrevalenceEntity> classLoop : prevalentClasses) {
			if (entityClass.getCanonicalName().equals(classLoop.getCanonicalName())) {
				continue;
			}
			for (Method method : PrevalentAtributesValuesIdentificator.listPojoGets(entityClass)) {
				String atributeName = PrevalentAtributesValuesIdentificator.getAtributeName(method);
				Field field = PrevalentAtributesValuesIdentificator.getGetDeclaredFieldSecure(entityClass, atributeName);
				Class returnType = PrevalentAtributesValuesIdentificator.getDeclaredReturnType(method, field);
				if (entityClass.getCanonicalName().equals(returnType.getCanonicalName())) {
					filtredClasses.add(classLoop);
					continue;
				}
			}
		}
		return filtredClasses;
	}
	
}
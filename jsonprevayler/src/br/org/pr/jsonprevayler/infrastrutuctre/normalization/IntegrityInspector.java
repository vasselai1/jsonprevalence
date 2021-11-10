package br.org.pr.jsonprevayler.infrastrutuctre.normalization;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;
import br.org.pr.jsonprevayler.exceptions.ValidationPrevalenceException;
import br.org.pr.jsonprevayler.pojojsonrepository.core.FileCore;

public class IntegrityInspector {

	private final FileCore fileCore;
	private Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository;
	
	public IntegrityInspector(FileCore fileCore, Map<Class<? extends PrevalenceEntity>, Map<Long, ? super PrevalenceEntity>> pojoRepository) {
		this.fileCore = fileCore;
		this.pojoRepository = pojoRepository;
	}

	public List<PrevalenceEntity> listPrevalentRelations(PrevalenceEntity entity) throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, InterruptedException {
		List<PrevalenceEntity> directRelations = new ArrayList<>();
		List<Class<? extends PrevalenceEntity>> prevalentClasses = fileCore.listAllPrevalentClasses();
		prevalentClasses = filterClasses(entity.getClass(), prevalentClasses);
		for (Class<? extends PrevalenceEntity> classLoop : prevalentClasses) {
			directRelations.addAll(filterEntities(entity, classLoop));
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
	
	@SuppressWarnings({ "unchecked" })
	private List<PrevalenceEntity> filterEntities(PrevalenceEntity entity, Class<? extends PrevalenceEntity> classePrevalent) throws ClassNotFoundException, IOException, ValidationPrevalenceException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, InterruptedException {
		List<PrevalenceEntity> prevalentsEntitiesRelationed = new ArrayList<>();		
		Set<? extends PrevalenceEntity> allEntities = (Set<? extends PrevalenceEntity>) pojoRepository.get(classePrevalent).values();
		for (PrevalenceEntity entityLoop : allEntities) {
			JsonSerializationInstructions serializationInstrucions = PrevalentAtributesValuesIdentificator.getJsonSerializationInstructions(entityLoop);
			for (PrevalenceEntity prevalenceEntityVerifyLoop : serializationInstrucions.getPrevalentObjects()) {
				if (!prevalenceEntityVerifyLoop.getClass().getCanonicalName().equals(entity.getClass().getCanonicalName())) {//TODO verificar se polimorfismo ok?
					continue;
				}
				if (prevalenceEntityVerifyLoop.getId().equals(entity.getId())) {
					prevalentsEntitiesRelationed.addAll((Collection<? extends PrevalenceEntity>) prevalenceEntityVerifyLoop);
				}
			}
		}
		return prevalentsEntitiesRelationed;
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
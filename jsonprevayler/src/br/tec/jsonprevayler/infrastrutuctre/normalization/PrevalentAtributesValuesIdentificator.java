package br.tec.jsonprevayler.infrastrutuctre.normalization;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import br.tec.jsonprevayler.exceptions.ValidationPrevalenceException;

public class PrevalentAtributesValuesIdentificator {
	
	private static final List<MappingType> MAPPINGS_TO_GET_OBJECTS = Arrays.asList(MappingType.ENTITY, MappingType.ENTITY_COLLECTION, MappingType.ENTITY_MAP);
	
	public static <T extends PrevalenceEntity> JsonSerializationInstructions getJsonSerializationInstructions(T entity) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<String> ignores = new ArrayList<String>();
		List<String> adds = new ArrayList<String>();
		List<Method> getMethods = new ArrayList<Method>();
		List<T> prevalentObjects = new ArrayList<T>();
		JsonSerializationInstructions instruction = new JsonSerializationInstructions(ignores, adds, getMethods, prevalentObjects);
		if (entity == null) {
			return instruction;
		}
		validateAutoRelation(entity.getClass(), null, null);
		for (Method method : listPojoGets(entity.getClass())) {
			String atributeName = getAtributeName(method);
			Field field = entity.getClass().getDeclaredField(atributeName);
			MappingType mapType = getMappingType(method, field);
			if (MAPPINGS_TO_GET_OBJECTS.contains(mapType)) {
				prevalentObjects.addAll(getPrevalentObjetcs(entity, method));
			}
			if (MappingType.NONE.equals(mapType) || MappingType.ENTITY_MAP.equals(mapType)) {
				continue;
			}
			ignores.add(atributeName + ".*");
			adds.add(atributeName + ".class");
			if (MappingType.ENTITY.equals(mapType) || MappingType.ENTITY_COLLECTION.equals(mapType)) {
				adds.add(atributeName + ".id");
			}
			getMethods.add(method);
		}
		return instruction;
	} 
	
	@SuppressWarnings({ "rawtypes" })
	public static MappingType getMappingType(Method method, Field field) throws ValidationPrevalenceException, ClassNotFoundException {
		String atributeName = getAtributeName(method);
		Class returnType = method.getReturnType();
		if (Map.class.isAssignableFrom(returnType)) {
			return getMappintTypeForMap(atributeName, returnType, field);
		}
		if (Collection.class.isAssignableFrom(returnType)) {
			return getMappingTypeForCollection(returnType, field);
		}
		if (PrevalenceEntity.class.isAssignableFrom(returnType)) {
			return MappingType.ENTITY;
		}
		return MappingType.NONE;
	}

	@SuppressWarnings({ "rawtypes" })
	public static MappingType getMappingTypeForCollection(Class returnType, Field field) throws ClassNotFoundException {
		ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
		if (parametrizedType.getActualTypeArguments().length >= 1) {
			Type type = parametrizedType.getActualTypeArguments()[0];
			Class testClass = Class.forName(type.getTypeName());
			if (PrevalenceEntity.class.isAssignableFrom(testClass)) {
				return MappingType.ENTITY_COLLECTION;
			}			
		}
		return MappingType.NONE;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Class getClassForCollection(Class returnType, Field field) throws ClassNotFoundException {
		ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
		if (parametrizedType.getActualTypeArguments().length >= 1) {
			Type type = parametrizedType.getActualTypeArguments()[0];
			Class testClass = Class.forName(type.getTypeName());
			return testClass;			
		}
		return null;
	}	
	
	@SuppressWarnings({ "rawtypes" })
	public static MappingType getMappintTypeForMap(String atributeName, Class returnType, Field field) throws ValidationPrevalenceException, ClassNotFoundException {
		ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
		if (parametrizedType.getActualTypeArguments().length >= 2) {
			Type typeKey = parametrizedType.getActualTypeArguments()[0];
			Type typeValue = parametrizedType.getActualTypeArguments()[1];
			Class testKeyClass = Class.forName(typeKey.getTypeName());
			Class testValueClass = Class.forName(typeValue.getTypeName());
			if (PrevalenceEntity.class.isAssignableFrom(testValueClass)) {
				if (!Long.class.isAssignableFrom(testKeyClass)) {
					throw new ValidationPrevalenceException("Keys in " + atributeName + " do not Long! Keys in maps must be Long, equal to entity id instance!" );
				}
				return MappingType.ENTITY_MAP;
			}
		}
		return MappingType.NONE;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Class getClassForMap(Class returnType, Field field) throws ValidationPrevalenceException, ClassNotFoundException {
		ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
		if (parametrizedType.getActualTypeArguments().length >= 2) {
			Type typeValue = parametrizedType.getActualTypeArguments()[1];
			Class testValueClass = Class.forName(typeValue.getTypeName());
			return testValueClass;
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void validateAutoRelation(Class classConfirm, List<Class> visitedClasses, Class firstClass) throws ValidationPrevalenceException, NoSuchFieldException, SecurityException, ClassNotFoundException {		
		if (visitedClasses == null) {
			visitedClasses = new ArrayList<Class>();
		}
		if (firstClass == null) {
			firstClass = classConfirm;
		}
		visitedClasses.add(classConfirm);
		List<Method> methods = listPojoGets(classConfirm);
		if (methods.isEmpty()) {
			return;
		}
		for (Method method : listPojoGets(classConfirm)) {
			String atributeName = getAtributeName(method);
			Field field = getGetDeclaredFieldSecure(classConfirm, atributeName);
			if (field == null) {
				continue;
			}
			Class returnType = getDeclaredReturnType(method, field);
			if (classConfirm.getCanonicalName().equals(returnType.getCanonicalName()) || firstClass.getCanonicalName().equals(returnType.getCanonicalName())) {
				throw new ValidationPrevalenceException("Pojo autorelation (autocomposition) not suported in Flexjson, this causes infinite loop in serialization! Please remove all autorelations for " + classConfirm.getCanonicalName());
			}			 
			if (visitedClasses.contains(returnType)) {
				continue;
			}			
			validateAutoRelation(returnType, visitedClasses, firstClass);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Field getGetDeclaredFieldSecure(Class classConfirm, String atributeName) {
		try {
			return classConfirm.getDeclaredField(atributeName);
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getDeclaredReturnType(Method method, Field field) throws ClassNotFoundException {
		Class returnType = method.getReturnType();
		if (Map.class.isAssignableFrom(returnType)) {
			ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
			if (parametrizedType.getActualTypeArguments().length >= 2) {				
				Type typeValue = parametrizedType.getActualTypeArguments()[1];
				returnType = Class.forName(typeValue.getTypeName());
			}			
		}
		if (Collection.class.isAssignableFrom(returnType)) {
			ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
			if (parametrizedType.getActualTypeArguments().length >= 1) {
				Type type = parametrizedType.getActualTypeArguments()[0];
				returnType = Class.forName(type.getTypeName());
			}
		}
		return returnType;
	}
	
	public static String getAtributeName(Method method) {
		String name = method.getName().replace("get", "");
		return ("" + name.charAt(0)).toLowerCase() + name.substring(1);
	}
	
	@SuppressWarnings("rawtypes")
	public static  List<Method> listPojoGets(Class classe) {
		List<Method> getMethods = new ArrayList<Method>();
		for (Method method : classe.getDeclaredMethods()) {
			if (method.getParameterCount() > 0) {
				continue;
			}
			if (!method.getName().startsWith("get")) {
				continue;
			}
			getMethods.add(method);
		}
		return getMethods;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T extends PrevalenceEntity> Collection<T> getPrevalentObjetcs(T entity, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class returnType = method.getReturnType();
		Object objectValue = method.invoke(entity);
		if (Map.class.isAssignableFrom(returnType)) {
			return ((Map<Long, T>) objectValue).values();
		}
		if (Collection.class.isAssignableFrom(returnType)) {
			return (Collection<T>) objectValue;
		}
		if (PrevalenceEntity.class.isAssignableFrom(returnType)) {
			return Arrays.asList((T) objectValue);
		}
		return new ArrayList<T>();
	} 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends PrevalenceEntity> Set<Class<T>> listPrevalenceRelations(Class<T> classe) throws ClassNotFoundException, ValidationPrevalenceException, NoSuchFieldException, SecurityException {
		Set<Class<T>> prevalenceRelations = new TreeSet<Class<T>>();
		for (Method method : listPojoGets(classe.getClass())) {
			String atributeName = getAtributeName(method);
			Field field = classe.getDeclaredField(atributeName);
			Class returnType = method.getReturnType();
			MappingType mapType = getMappingType(method, field);
			if (MappingType.NONE.equals(mapType)) {
				continue;
			}
			if (PrevalenceEntity.class.isAssignableFrom(returnType)) {
				prevalenceRelations.add((Class<T>) returnType);
				continue;
			}
			Class testClass = null;
			if (Map.class.isAssignableFrom(returnType)) {
				testClass = getClassForMap(returnType, field);
			}
			if (Collection.class.isAssignableFrom(returnType)) {
				testClass = getClassForCollection(returnType, field);
			}
			if (PrevalenceEntity.class.isAssignableFrom(testClass)) {
				prevalenceRelations.add((Class<T>) testClass);
				continue;
			}
		}
		return prevalenceRelations;
	}
	
	@SuppressWarnings("rawtypes")
	public static <T extends PrevalenceEntity> List<LoadInstruction> getLoadInstructions(T entity) throws NoSuchFieldException, SecurityException, ClassNotFoundException, ValidationPrevalenceException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		validateAutoRelation(entity.getClass(), null, null);
		List<LoadInstruction> loadInstructions = new ArrayList<LoadInstruction>();
		Class classe = entity.getClass(); 
		for (Method getMethod : listPojoGets(classe)) {
			String atributeName = getAtributeName(getMethod);
			Field field = classe.getDeclaredField(atributeName);			
			MappingType mappingType = getMappingType(getMethod, field);
			if (MAPPINGS_TO_GET_OBJECTS.contains(mappingType)) {
				Method setMethod = getSetMethod(classe, getMethod);
				Object originalValue = getMethod.invoke(entity);
				loadInstructions.add(new LoadInstruction(atributeName, field, mappingType, getMethod, setMethod, originalValue));
			}
		}
		return loadInstructions;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Method getSetMethod(Class classe, Method getMethod) throws NoSuchMethodException, SecurityException {
		Class<?> returnTypeClass = getMethod.getReturnType();
		String methodSetName = getMethod.getName().replace("get", "set");
		return classe.getDeclaredMethod(methodSetName, returnTypeClass);
	}
	
}
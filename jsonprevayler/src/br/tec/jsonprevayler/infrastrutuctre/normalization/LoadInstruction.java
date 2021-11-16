package br.tec.jsonprevayler.infrastrutuctre.normalization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LoadInstruction {
	
	private String atributeName;
	private Field field;
	private MappingType mappingType;
	private Method getMethod;
	private Method setMethod;
	private Object originalValue;
	
	public LoadInstruction(String atributeName, Field field, MappingType mappingType, Method getMethod, Method setMethod, Object originalValue) {
		this.atributeName = atributeName;
		this.field = field;
		this.mappingType = mappingType;
		this.getMethod = getMethod;
		this.setMethod = setMethod;
		this.originalValue = originalValue;
	}
	
	public String getAtributeName() {
		return atributeName;
	}
	public Field getField() {
		return field;
	}
	public MappingType getMappingType() {
		return mappingType;
	}
	public Method getGetMethod() {
		return getMethod;
	}
	public Method getSetMethod() {
		return setMethod;
	}
	public Object getOriginalValue() {
		return originalValue;
	}

}
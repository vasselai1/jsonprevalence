package br.org.pr.jsonprevayler.infrastrutuctre.normalization;

import java.lang.reflect.Method;
import java.util.List;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

public class JsonSerializationInstructions {
	
	private List<String> ignores;
	private List<String> adds;
	private List<Method> getMethods;
	private List<? extends PrevalenceEntity> prevalentObjects;
	
	public JsonSerializationInstructions(List<String> ignores, List<String> adds, List<Method> getMethods, List<? extends PrevalenceEntity> prevalentObjects) {
		this.ignores = ignores;
		this.adds = adds;
		this.getMethods = getMethods;
		this.prevalentObjects = prevalentObjects;
	}

	public List<String> getIgnores() {
		return ignores;
	}
	public List<String> getAdds() {
		return adds;
	}
	public List<Method> getGetMethods() {
		return getMethods;
	}
	public List<? extends PrevalenceEntity> getPrevalentObjects() {
		return prevalentObjects;
	}

	@Override
	public String toString() {
		return "IgnoreJsonSerializationInstruction [ignores=" + ignores + ", adds=" + adds + "]";
	}
	
}
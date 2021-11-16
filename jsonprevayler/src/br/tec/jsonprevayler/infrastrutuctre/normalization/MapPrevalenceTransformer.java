package br.tec.jsonprevayler.infrastrutuctre.normalization;

import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;
import flexjson.JSONContext;
import flexjson.Path;
import flexjson.TypeContext;
import flexjson.transformer.MapTransformer;
import flexjson.transformer.TransformerWrapper;

public class MapPrevalenceTransformer extends MapTransformer {
	
	@Override
	public void transform(Object object) {
		if (isPrevaleceMap(object)) {
			replaceTransform(object);
		} else {
			super.transform(object);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void replaceTransform(Object object) {
		JSONContext context = getContext();
	    Path path = context.getPath();
	    Map value = (Map) object;
	    TypeContext typeContext = getContext().writeOpenObject();
	    for (Object key : value.keySet()) {
	    	path.enqueue(key != null ? key.toString() : null);
            if (context.isIncluded(key != null ? key.toString() : null, value.get(key))) {
                TransformerWrapper transformer = (TransformerWrapper)context.getTransformer(value.get(key));
                if(!transformer.isInline()) {
                	if (!typeContext.isFirst()) {
                		getContext().writeComma();
                	}
	                typeContext.setFirst(false);
	                if( key != null ) {
	                	getContext().writeName(key.toString());
	                } else {
	                	getContext().writeName(null);
	                }
                }
                if( key != null ) {
                    typeContext.setPropertyName(key.toString());
                } else {
                    typeContext.setPropertyName(null);
                }
                StringBuilder builder = new StringBuilder();
                PrevalenceEntity entity = ((value.get(key) != null) && (value.get(key) instanceof PrevalenceEntity)) ? (PrevalenceEntity) value.get(key) : null; 
                if (entity != null) {
                	builder.append("{\"class\":\"").append(entity.getClass().getCanonicalName()).append("\",\"id\":").append(entity.getId()).append("}");
                	getContext().write(builder.toString());
                } else {
                	transformer.transform(value.get(key));
                }
	        }
	        path.pop();
        }
	        getContext().writeCloseObject();
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isPrevaleceMap(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object instanceof Map)) {
			return false;
		}
		Map map = (Map) object;
		if (map.isEmpty()) {
			return false;
		}
		return (map.values().iterator().next() instanceof PrevalenceEntity);
	}
}

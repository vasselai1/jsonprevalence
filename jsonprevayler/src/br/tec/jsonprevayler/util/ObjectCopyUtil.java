package br.tec.jsonprevayler.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import br.tec.jsonprevayler.exceptions.InternalPrevalenceException;

public class ObjectCopyUtil {

	@SuppressWarnings("unchecked")
	public static<T> T copyEntity(T object) throws InternalPrevalenceException {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(ObjectCopyUtil.class.getName()), e, "Error in binary copy while write object for entity = %1$s, toString = %2$s", object.getClass().getSimpleName(), object.toString());
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
		try {
			ObjectInputStream ois = new ObjectInputStream(bis);
			return (T) ois.readObject();
		} catch (Exception e) {
			throw LoggerUtil.error(Logger.getLogger(ObjectCopyUtil.class.getName()), e, "Error in binary copy while read object for entity = %1$s, toString = %2$s", object.getClass().getSimpleName(), object.toString());
		}
	}	
	
	public static <T> List<T> copyList(Class<T> classe, Collection<T> entityCollection) throws InternalPrevalenceException {
		List<T> retorno = new ArrayList<T>();
		if (entityCollection == null) {
			return retorno;
		}
		for (T entity : entityCollection) {
			T copied = (T) copyEntity(entity);
			retorno.add(copied);
		}
		return retorno;
	}
	
}
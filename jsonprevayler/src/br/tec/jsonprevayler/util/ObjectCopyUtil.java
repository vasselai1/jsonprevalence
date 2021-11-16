package br.tec.jsonprevayler.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectCopyUtil {

	@SuppressWarnings("unchecked")
	public static<T> T copyEntity(T object) throws IOException, ClassNotFoundException {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (T) ois.readObject();
	}	
	
	public static <T> List<T> copyList(Class<T> classe, Collection<T> entityCollection) throws ClassNotFoundException, IOException {
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
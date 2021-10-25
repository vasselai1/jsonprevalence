package br.org.pr.jsonprevayler.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.org.pr.jsonprevayler.entity.PrevalenceEntity;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MappedSuperClassPrevalenceRepository {
	public Class<? extends PrevalenceEntity> mapping();
}

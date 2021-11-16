package br.tec.jsonprevayler.test.entity;

import java.util.Date;
import java.util.List;

import br.tec.jsonprevayler.entity.PrevalenceEntity;

public class User implements PrevalenceEntity {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String mail;
	private String passwd;
	private Date saveTime;
	private Date updateTime;
	private String mailConfirmationCode;
	private Date confirmationEmailTime;
	private List<String> nomes;
//	private MapCollectionEntity autoRelation;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public Date getSaveTime() {
		return saveTime;
	}
	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getMailConfirmationCode() {
		return mailConfirmationCode;
	}
	public void setEmailConfirmationCode(String mailConfirmationCode) {
		this.mailConfirmationCode = mailConfirmationCode;
	}
	public Date getConfirmationEmailTime() {
		return confirmationEmailTime;
	}
	public void setConfirmationEmailTime(Date confirmationEmailTime) {
		this.confirmationEmailTime = confirmationEmailTime;
	}
	public List<String> getNomes() {
		return nomes;
	}
	public void setNomes(List<String> nomes) {
		this.nomes = nomes;
	}
//	public MapCollectionEntity getAutoRelation() {
//		return autoRelation;
//	}
//	public void setAutoRelation(MapCollectionEntity autoRelation) {
//		this.autoRelation = autoRelation;
//	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", mail=" + mail + "]";
	}

}
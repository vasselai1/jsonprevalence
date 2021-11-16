package br.tec.jsonprevayler.test.entity;

import java.util.List;
import java.util.Map;

import br.tec.jsonprevayler.entity.PrevalenceEntity;

public class MapCollectionEntity implements PrevalenceEntity {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private List<String> names;
	private Map<Long, String> codesNames;
	private User user;
	private List<User> usersList;
	private Map<Long, User> usersMap;
	//private MapCollectionEntity autoRelation;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	public Map<Long, String> getCodesNames() {
		return codesNames;
	}
	public void setCodesNames(Map<Long, String> codesNames) {
		this.codesNames = codesNames;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<User> getUsersList() {
		return usersList;
	}
	public void setUsersList(List<User> usersList) {
		this.usersList = usersList;
	}
	public Map<Long, User> getUsersMap() {
		return usersMap;
	}
	public void setUsersMap(Map<Long, User> usersMap) {
		this.usersMap = usersMap;
	}
//	public MapCollectionEntity getAutoRelation() {
//		return autoRelation;
//	}
//	public void setAutoRelation(MapCollectionEntity autoRelation) {
//		this.autoRelation = autoRelation;
//	}
	
}
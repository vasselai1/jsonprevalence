package jsonprevayler.entity;

import java.util.List;

public class Synonym implements PrevalenceEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	private List<String> synonyns;
	
	public Synonym() {
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<String> getSynonyns() {
		return synonyns;
	}
	public void setSynonyns(List<String> synonyns) {
		this.synonyns = synonyns;
	}
	@Override
	public String toString() {
		return "Synonym [id=" + id + ", synonyns=" + synonyns + "]";
	}
	
}
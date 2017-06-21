package org.dbpedia.dbtax;

public class Page {
	
	private String name;
	private int namespace;
	
	public Page(String name, int namespace) {
		super();
		this.name = name;
		this.namespace = namespace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNamespace() {
		return namespace;
	}
	public void setNamespace(int namespace) {
		this.namespace = namespace;
	}
	
}

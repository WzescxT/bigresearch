package com.monetware.model.search;

public class ProjectNameModel {
	
	
	private long id;
	private String name;
	private int createUser;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCreateUser() {
		return createUser;
	}
	public void setCreateUser(int createUser) {
		this.createUser = createUser;
	}
	@Override
	public String toString() {
		return "ProjectNameModel [id=" + id + ", name=" + name + ", createUser=" + createUser + "]";
	}
	
	

}

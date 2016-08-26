package com.tunshu.entity;

public class DistrictEntity {
	private String name;
	private String zipcode;
	
	public DistrictEntity() {
		super();
	}

	public DistrictEntity(String name, String zipcode) {
		super();
		this.name = name;
		this.zipcode = zipcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@Override
	public String toString() {
		return "DistrictEntity [name=" + name + ", zipcode=" + zipcode + "]";
	}

}

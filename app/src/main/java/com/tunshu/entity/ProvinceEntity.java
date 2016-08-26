package com.tunshu.entity;

import java.util.List;

public class ProvinceEntity {
	private String name;
	private List<CityEntity> cityList;
	
	public ProvinceEntity() {
		super();
	}

	public ProvinceEntity(String name, List<CityEntity> cityList) {
		super();
		this.name = name;
		this.cityList = cityList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CityEntity> getCityList() {
		return cityList;
	}

	public void setCityList(List<CityEntity> cityList) {
		this.cityList = cityList;
	}

	@Override
	public String toString() {
		return "ProvinceEntity [name=" + name + ", cityList=" + cityList + "]";
	}
	
}

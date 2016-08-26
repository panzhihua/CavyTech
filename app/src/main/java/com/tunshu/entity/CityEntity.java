package com.tunshu.entity;

import java.util.List;

public class CityEntity {
	private String name;
	private List<DistrictEntity> districtList;

	public CityEntity() {
		super();
	}

	public CityEntity(String name, List<DistrictEntity> districtList) {
		super();
		this.name = name;
		this.districtList = districtList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DistrictEntity> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(List<DistrictEntity> districtList) {
		this.districtList = districtList;
	}

	@Override
	public String toString() {
		return "CityEntity [name=" + name + ", districtList=" + districtList
				+ "]";
	}


}

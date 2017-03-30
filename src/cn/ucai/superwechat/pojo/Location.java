package cn.ucai.superwechat.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Location implements Serializable,Comparable<Location>{
	private static final long serialVersionUID = 1L;
	private Integer mlocationId;
	private String mlocationUserName;
	private Double mlocationLatitude;
	private Double mlocationLongitude;
	private Boolean mlocationIsSearched;
	private String mlocationLastUpdateTime;

	public Location() {
		super();
	}

	public Location(String mlocationUserName, Double mlocationLatitude, Double mlocationLongitude,
			Boolean mlocationIsSearched, String mlocationLastUpdateTime) {
		this.mlocationUserName = mlocationUserName;
		this.mlocationLatitude = mlocationLatitude;
		this.mlocationLongitude = mlocationLongitude;
		this.mlocationIsSearched = mlocationIsSearched;
		this.mlocationLastUpdateTime = mlocationLastUpdateTime;
	}

	public Location(Integer mlocationId, String mlocationUserName, Double mlocationLatitude, Double mlocationLongitude,
			Boolean mlocationIsSearched, String mlocationLastUpdateTime) {
		this.mlocationId = mlocationId;
		this.mlocationUserName = mlocationUserName;
		this.mlocationLatitude = mlocationLatitude;
		this.mlocationLongitude = mlocationLongitude;
		this.mlocationIsSearched = mlocationIsSearched;
		this.mlocationLastUpdateTime = mlocationLastUpdateTime;
	}

	public Integer getMLocationId() {
		return this.mlocationId;
	}

	public void setMLocationId(Integer mlocationId) {
		this.mlocationId = mlocationId;
	}

	public String getMLocationUserName() {
		return this.mlocationUserName;
	}

	public void setMLocationUserName(String mlocationUserName) {
		this.mlocationUserName = mlocationUserName;
	}

	public Double getMLocationLatitude() {
		return this.mlocationLatitude;
	}

	public void setMLocationLatitude(Double mlocationLatitude) {
		this.mlocationLatitude = mlocationLatitude;
	}

	public Double getMLocationLongitude() {
		return this.mlocationLongitude;
	}

	public void setMLocationLongitude(Double mlocationLongitude) {
		this.mlocationLongitude = mlocationLongitude;
	}

	public Boolean getMLocationIsSearched() {
		return this.mlocationIsSearched;
	}

	public void setMLocationIsSearched(Boolean mlocationIsSearched) {
		this.mlocationIsSearched = mlocationIsSearched;
	}

	public String getMLocationLastUpdateTime() {
		return this.mlocationLastUpdateTime;
	}

	public void setMLocationLastUpdateTime(String mlocationLastUpdateTime) {
		this.mlocationLastUpdateTime = mlocationLastUpdateTime;
	}

	@Override
	public String toString() {
		return "Location [" + "this.mlocationId=" + mlocationId + "this.mlocationUserName=" + mlocationUserName
				+ "this.mlocationLatitude=" + mlocationLatitude + "this.mlocationLongitude=" + mlocationLongitude
				+ "this.mlocationIsSearched=" + mlocationIsSearched + "this.mlocationLastUpdateTime="
				+ mlocationLastUpdateTime;
	}

	@Override
	public int compareTo(Location o) {
		double EARTH_RADIUS = 6378137.0;
		double dLat = Math.toRadians(o.mlocationLatitude - this.mlocationLatitude); 
        double dLng = Math.toRadians(o.mlocationLongitude - this.mlocationLongitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(this.mlocationLatitude)) * Math.cos(Math.toRadians(o.mlocationLatitude)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return new BigDecimal(distance).intValue();
	}

}
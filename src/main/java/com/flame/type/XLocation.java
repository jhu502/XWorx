package com.flame.type;

public class XLocation {
	private static final Double DEFAULT_LATITUDE = 0.0D;
	private static final Double DEFAULT_LONGITUDE = 0.0D;
	private static final Double DEFAULT_ELEVATION = 0.0D;
	private Double latitude;
	private Double longitude;
	private Double elevation;

	public XLocation() {
		this.latitude = DEFAULT_LATITUDE;
		this.longitude = DEFAULT_LONGITUDE;
		this.elevation = DEFAULT_ELEVATION;
	}

	public XLocation(Double longitude, Double latitude) {
		this(longitude, latitude, 0.0D);
	}

	public XLocation(Double longitude, Double latitude, Double elevation) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.elevation = elevation;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
}

package com.flame.minio;

public class MinioItem {
	private String objectName;
	private Long size;
	private String versionId;
	private String encodingType;
	private boolean isLatest;
	private String lastModified;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

	public boolean isLatest() {
		return isLatest;
	}

	public void setLatest(boolean isLatest) {
		this.isLatest = isLatest;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

}

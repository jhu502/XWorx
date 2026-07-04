package xw.content.entity;

import xw.content.ContentItem;
import xw.content.ContentType;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "XApplicationData", uniqueConstraints = {})
public class XApplicationData extends ContentItem {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "refer_path", length = 200)
	private String referPath = "";

	@Basic
	@Column(name = "inner_name", length = 200)
	private String innerName = "";

	@Basic
	@Column(name = "file_name", length = 100)
	private String fileName = "";

	@Basic
	@Column(name = "file_size")
	private Long fileSize;

	@Enumerated(EnumType.STRING)
	@Column(name = "role_type", nullable = false)
	private ContentType roleType;

	@Basic
	@Column(name = "checksum")
	private Long checkSum;

	@Basic
	@Column(name = "uploaded_path", length = 1000)
	private String uploadedPath = "";

	public String getReferPath() {
		return referPath;
	}

	public void setReferPath(String referPath) {
		this.referPath = referPath;
	}

	public String getInnerName() {
		return innerName;
	}

	public void setInnerName(String innerName) {
		this.innerName = innerName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public ContentType getRoleType() {
		return roleType;
	}

	public void setRoleType(ContentType roleType) {
		this.roleType = roleType;
	}

	public Long getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(Long checkSum) {
		this.checkSum = checkSum;
	}

	public String getUploadedPath() {
		return uploadedPath;
	}

	public void setUploadedPath(String uploadedPath) {
		this.uploadedPath = uploadedPath;
	}

}

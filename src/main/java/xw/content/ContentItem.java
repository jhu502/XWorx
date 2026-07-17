package xw.content;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import com.flame.orm.XObject;

@MappedSuperclass
public class ContentItem extends XObject {
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false)
	private ContentType contentType;

	@Basic
	@Column(name = "comments", length = 1000)
	private String comments = "";

	@Basic
	@Column(name = "file_name", length = 100)
	private String fileName = "";

	@Basic
	@Column(name = "file_size")
	private Long fileSize;

	@Basic
	@Column(name = "inner_name", length = 200)
	private String innerName = "";
	
	@Basic
	@Column(name = "refer_path", length = 200)
	private String referPath = "";

	@Basic
	@Column(name = "checksum")
	private Long checkSum;

	@Basic
	@Column(name = "uploaded_path", length = 1000)
	private String uploadedPath = "";
	
	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public String getInnerName() {
		return innerName;
	}

	public void setInnerName(String innerName) {
		this.innerName = innerName;
	}

	public String getReferPath() {
		return referPath;
	}

	public void setReferPath(String referPath) {
		this.referPath = referPath;
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

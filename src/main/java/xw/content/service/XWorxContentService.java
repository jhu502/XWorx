package xw.content.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Service;

import com.flame.minio.MinioHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import xw.content.ContentType;
import xw.content.IContentHolder;
import xw.content.entity.XApplicationData;
import xw.content.entity.XHolderToContent;

@Service
public class XWorxContentService {

	@SuppressWarnings("unchecked")
	public List<XApplicationData> getRelatedContentItem(IContentHolder contentHolder, ContentType contentType) {
		String hql = "select a from XApplicationData a, XHolderToContent b where b.left.id = :id and b.right.id = a.id and a.contentType = :type";
		return (List<XApplicationData>) PersistenceHelper.service().query(hql, new Object[][] { { "id", contentHolder.getXid() }, { "type", contentType } });
	}

	@SuppressWarnings("unchecked")
	public List<XHolderToContent> getRelatedHolder2Content(IContentHolder contentHolder, ContentType contentType) {
		String hql = "select b from XApplicationData a, XHolderToContent b where b.left.id = :id and b.right.id = a.id and a.contentType = :type";
		return (List<XHolderToContent>) PersistenceHelper.service().query(hql, new Object[][] { { "id", contentHolder.getXid() }, { "type", contentType } });
	}

	public XApplicationData uploadContent(String fileName, long size, InputStream inputStream, ContentType contentType) {
		XApplicationData appData = new XApplicationData();
		appData.setFileName(fileName);
		appData.setFileSize(size);
		appData.setContentType(contentType);
		appData.setReferPath(MinioHelper.XWORX_VAULT);
		try {
			FileItem fileItem = MinioHelper.getFileItem(inputStream, fileName);
			String innerName = MinioHelper.service().upload(fileItem, MinioHelper.XWORX_VAULT);
			appData.setInnerName(innerName);
		} catch (IOException e) {
			throw new XException(e);
		}
		appData = PersistenceHelper.service().save(appData);

		return appData;
	}

	public void uploadContent(IContentHolder contentHolder, String fileName, long size, InputStream inputStream, ContentType contentType) {
		XApplicationData appData = new XApplicationData();
		appData.setFileName(fileName);
		appData.setFileSize(size);
		appData.setContentType(contentType);
		appData.setReferPath(MinioHelper.XWORX_VAULT);
		try {
			FileItem fileItem = MinioHelper.getFileItem(inputStream, fileName);
			String innerName = MinioHelper.service().upload(fileItem, MinioHelper.XWORX_VAULT);
			appData.setInnerName(innerName);
		} catch (IOException e) {
			throw new XException(e);
		}
		appData = PersistenceHelper.service().save(appData);
		XHolderToContent holde2Content = XHolderToContent.newXHolderToContent(contentHolder, appData);
		PersistenceHelper.service().save(holde2Content);
		//org.hibernate.cfg.PropertyContainer xx;
	}

	public void uploadPrimaryContent(IContentHolder contentHolder, String fileName, long size, InputStream inputStream) {
		List<XHolderToContent> list = this.getRelatedHolder2Content(contentHolder, ContentType.PRIMARY);
		if (list == null || list.isEmpty()) {
			XApplicationData appData = this.uploadContent(fileName, size, inputStream, ContentType.PRIMARY);
			XHolderToContent holde2Content = XHolderToContent.newXHolderToContent(contentHolder, appData);
			PersistenceHelper.service().save(holde2Content);
		} else {
			XApplicationData appData = this.uploadContent(fileName, size, inputStream, ContentType.PRIMARY);
			XHolderToContent holder2Content = list.get(0);
			holder2Content.setApplicationData(appData);
			PersistenceHelper.service().save(holder2Content);
		}
	}

	public void uploadThumb3DContent(IContentHolder contentHolder, String fileName, long size, InputStream inputStream) {
		List<XHolderToContent> list = this.getRelatedHolder2Content(contentHolder, ContentType.THUMBNAIL3D);
		if (list == null || list.isEmpty()) {
			XApplicationData appData = this.uploadContent(fileName, size, inputStream, ContentType.THUMBNAIL3D);
			XHolderToContent holde2Content = XHolderToContent.newXHolderToContent(contentHolder, appData);
			PersistenceHelper.service().save(holde2Content);
		} else {
			XApplicationData appData = this.uploadContent(fileName, size, inputStream, ContentType.THUMBNAIL3D);
			XHolderToContent holder2Content = list.get(0);
			holder2Content.setApplicationData(appData);
			PersistenceHelper.service().save(holder2Content);
		}
	}
}

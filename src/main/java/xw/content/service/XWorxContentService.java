package xw.content.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Service;

import com.flame.minio.MinioHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import xw.content.ContentItem;
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

	public List<XApplicationData> getAllContentItems(IContentHolder contentHolder) {
		List<XApplicationData> result = new ArrayList<>();
		for (ContentType ct : ContentType.values()) {
			List<XApplicationData> list = this.getRelatedContentItem(contentHolder, ct);
			if (list != null) {
				result.addAll(list);
			}
		}
		return result;
	}

	public ContentItem uploadContent(String fileName, long size, InputStream inputStream, ContentType contentType) {
		ContentItem contentItem = contentType.newContentItem();

		contentItem.setFileName(fileName);
		contentItem.setFileSize(size);
		contentItem.setContentType(contentType);
		contentItem.setReferPath(MinioHelper.XWORX_VAULT);
		try {
			FileItem fileItem = MinioHelper.getFileItem(inputStream, fileName);
			String innerName = MinioHelper.service().upload(fileItem, MinioHelper.XWORX_VAULT);
			contentItem.setInnerName(innerName);
		} catch (IOException e) {
			throw new XException(e);
		}

		return PersistenceHelper.service().save(contentItem);
	}

	public void uploadContent(IContentHolder contentHolder, String fileName, long size, InputStream inputStream, ContentType contentType) {
		ContentItem contentItem = contentType.newContentItem();
		contentItem.setFileName(fileName);
		contentItem.setFileSize(size);
		try {
			FileItem fileItem = MinioHelper.getFileItem(inputStream, fileName);
			String innerName = MinioHelper.service().upload(fileItem, MinioHelper.XWORX_VAULT);
			contentItem.setInnerName(innerName);
		} catch (IOException e) {
			throw new XException(e);
		}
		contentItem.setReferPath(MinioHelper.XWORX_VAULT);
		contentItem = PersistenceHelper.service().save(contentItem);
		XHolderToContent holdeContent = XHolderToContent.newInstance(contentHolder, contentItem);
		PersistenceHelper.service().save(holdeContent);
	}

	public void uploadPrimaryContent(IContentHolder contentHolder, String fileName, long size, InputStream inputStream) {
		List<XHolderToContent> contentList = this.getRelatedHolder2Content(contentHolder, ContentType.PRIMARY);

		if (contentList == null || contentList.isEmpty()) {
			ContentItem appData = this.uploadContent(fileName, size, inputStream, ContentType.PRIMARY);
			XHolderToContent holdeContent = XHolderToContent.newInstance(contentHolder, appData);
			PersistenceHelper.service().save(holdeContent);
		} else {
			ContentItem contentItem = this.uploadContent(fileName, size, inputStream, ContentType.PRIMARY);
			XHolderToContent holder2Content = contentList.get(0);
			holder2Content.setContentItem(contentItem);
			PersistenceHelper.service().save(holder2Content);
		}
	}

	public void uploadThumb3DContent(IContentHolder contentHolder, String fileName, long size, InputStream inputStream) {
		List<XHolderToContent> contentList = this.getRelatedHolder2Content(contentHolder, ContentType.THUMBNAIL3D);

		if (contentList == null || contentList.isEmpty()) {
			ContentItem contentItem = this.uploadContent(fileName, size, inputStream, ContentType.THUMBNAIL3D);
			XHolderToContent holdeContent = XHolderToContent.newInstance(contentHolder, contentItem);
			PersistenceHelper.service().save(holdeContent);
		} else {
			ContentItem contentItem = this.uploadContent(fileName, size, inputStream, ContentType.THUMBNAIL3D);
			XHolderToContent holderContent = contentList.get(0);
			holderContent.setContentItem(contentItem);
			PersistenceHelper.service().save(holderContent);
		}
	}

	/**
	 * 上传 ZIP 压缩包中的资源文件，逐文件解压并以 XResourceData 方式上传。
	 * 每个 ZIP 条目的相对路径写入 XResourceData.uploadedPath。
	 *
	 * @param contentHolder 内容持有者
	 * @param zipInputStream ZIP 文件输入流
	 * @throws IOException 读取 ZIP 流失败时抛出
	 */
	public void uploadResourceZipContent(IContentHolder contentHolder, InputStream zipInputStream) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
			byte[] buffer = new byte[8192];
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}
				// 将当前条目读入字节数组
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int len;
				while ((len = zis.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				byte[] entryBytes = baos.toByteArray();
				String entryPath = entry.getName();
				String entryFileName = new File(entryPath).getName();

				// 创建 XResourceData，设置 uploadedPath 为 ZIP 中的相对路径
				ContentItem contentItem = ContentType.RESOURCE.newContentItem();
				contentItem.setFileName(entryFileName);
				contentItem.setFileSize((long) entryBytes.length);
				contentItem.setUploadedPath(entryPath);
				contentItem.setReferPath(MinioHelper.XWORX_VAULT);

				// 上传到 MinIO
				try (ByteArrayInputStream bais = new ByteArrayInputStream(entryBytes)) {
					FileItem fileItem = MinioHelper.getFileItem(bais, entryFileName);
					String innerName = MinioHelper.service().upload(fileItem, MinioHelper.XWORX_VAULT);
					contentItem.setInnerName(innerName);
				}

				// 持久化并建立与持有者的关联
				contentItem = PersistenceHelper.service().save(contentItem);
				XHolderToContent holderContent = XHolderToContent.newInstance(contentHolder, contentItem);
				PersistenceHelper.service().save(holderContent);
			}
		}
	}
}

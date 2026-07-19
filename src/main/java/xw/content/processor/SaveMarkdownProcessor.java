package xw.content.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.fileupload.FileItem;

import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.minio.MinioHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.XException;
import com.flame.xui.XCommandBean;

import xw.content.entity.XApplicationData;

public class SaveMarkdownProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		XObject primaryObj = commandBean.getPrimaryObj();
		if (primaryObj == null || !(primaryObj instanceof XApplicationData contentData)) {
			formResult.setStatus(FormStatus.FAILURE);
			formResult.setMessage("Invalid content item.");
			return formResult;
		}

		String content = commandBean.getTextParameter("content");
		if (content == null) {
			formResult.setStatus(FormStatus.FAILURE);
			formResult.setMessage("No content provided.");
			return formResult;
		}

		byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
			FileItem fileItem = MinioHelper.getFileItem(inputStream, contentData.getFileName());
			String innerName = MinioHelper.service().upload(fileItem, MinioHelper.XWORX_VAULT);
			contentData.setInnerName(innerName);
			contentData.setFileSize((long) bytes.length);
			PersistenceHelper.service().save(contentData);
		} catch (Exception e) {
			throw new XException(e);
		}

		formResult.setStatus(FormStatus.SUCCESS);
		formResult.setMessage("Markdown saved successfully.");
		return formResult;
	}
}

package xw.content.processor;

import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.orm.PersistenceHelper;
import com.flame.xui.XCommandBean;
import com.flame.util.XException;
import jakarta.servlet.http.HttpServletRequest;
import xw.content.ContentType;
import xw.content.IContentHolder;
import xw.content.XContentHelper;

public class UploadContentProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		Object primary = commandBean.getPrimaryObj();
		if (primary == null || !(primary instanceof IContentHolder)) {
			formResult.setStatus(FormStatus.FAILURE);
			formResult.setMessage("Invalid content holder.");
			return formResult;
		}

		HttpServletRequest request = commandBean.getRequest();
		if (request instanceof MultipartHttpServletRequest multipartRequest) {
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
			if (fileMap.isEmpty()) {
				formResult.setStatus(FormStatus.FAILURE);
				formResult.setMessage("No file selected.");
				return formResult;
			}

			// Read contentType from request param, default to SECONDARY
			ContentType contentType = ContentType.SECONDARY;
			String ctParam = multipartRequest.getParameter("contentType");
			if (ctParam != null && !ctParam.isEmpty()) {
				try {
					contentType = ContentType.valueOf(ctParam);
				} catch (IllegalArgumentException ignored) {
				}
			}

			for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
				MultipartFile partFile = entry.getValue();
				try (InputStream instream = partFile.getInputStream()) {
					String filename = partFile.getOriginalFilename();
					long size = partFile.getSize();
					XContentHelper.service().uploadContent((IContentHolder) primary, filename, size, instream, contentType);
				} catch (Exception e) {
					throw new XException(e);
				}
			}

			// Flush persistence context so new items appear immediately
			PersistenceHelper.service().query("select a from XHolderToContent a", new Object[][] {});
		} else {
			formResult.setStatus(FormStatus.FAILURE);
			formResult.setMessage("Request is not multipart.");
		}

		return formResult;
	}
}

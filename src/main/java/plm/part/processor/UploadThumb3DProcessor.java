package plm.part.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import xw.content.IContentHolder;
import xw.content.XContentHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import jakarta.servlet.http.HttpServletRequest;

public class UploadThumb3DProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		Object primary = commandBean.getPrimaryObj();
		if (primary == null) {
			formResult.setStatus(FormStatus.FAILURE);
		}

		HttpServletRequest request = commandBean.getRequest();
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			//获取上传上来的文件
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
			for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
				MultipartFile partFile = entry.getValue();
				try (InputStream instream = partFile.getInputStream();) {
					String filename = partFile.getOriginalFilename();
					if (filename == null || !filename.toLowerCase().endsWith(".ol"))
						throw new XException("请选择正确的文件类型.");
					long size = partFile.getSize();
					XContentHelper.service().uploadThumb3DContent((IContentHolder) primary, filename, size, instream);
				} catch (IOException e) {
					throw new XException(e);
				}

			}
			PersistenceHelper.service().query("select a from XHolderToContent a", new Object[][] {});
		}

		return formResult;
	}

}

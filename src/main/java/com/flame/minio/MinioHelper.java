package com.flame.minio;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.flame.config.basic.BasicConfiguration;

public class MinioHelper {
	protected static final Logger logger = LoggerFactory.getLogger(MinioHelper.class);
	public static final String XWORX_VAULT = "xworxvault";
	private static MinioServiceImpl service;

	private MinioHelper() {
	}

	public static MinioServiceImpl service() {
		if (service == null) {
			service = BasicConfiguration.getBean(MinioServiceImpl.class);
		}

		return service;
	}

	public static FileItem getFileItem(InputStream instream, String fileName) throws IOException {
		FileItemFactory factory = new DiskFileItemFactory(16, null);
		FileItem fileItem = factory.createItem("file", MediaType.MULTIPART_FORM_DATA_VALUE, true, fileName);
		IOUtils.copy(instream, fileItem.getOutputStream());
		return fileItem;
	}
}

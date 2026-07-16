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

/**
 * MinIO 操作工具类，提供 {@link MinioServiceImpl} 的静态访问入口
 * 以及用于上传的 FileItem 构造方法。
 */
public class MinioHelper {
	protected static final Logger logger = LoggerFactory.getLogger(MinioHelper.class);

	/** 默认存储 Bucket 名称 */
	public static final String XWORX_VAULT = "xworxvault";

	private static MinioServiceImpl service;

	private MinioHelper() {
	}

	/**
	 * 获取 {@link MinioServiceImpl} 单例，延迟初始化。
	 *
	 * @return MinioServiceImpl 实例
	 */
	public static MinioServiceImpl service() {
		if (service == null) {
			service = BasicConfiguration.getBean(MinioServiceImpl.class);
		}
		return service;
	}

	/**
	 * 根据输入流构造 FileItem，用于 MinIO 上传。
	 *
	 * <p>使用 {@link DiskFileItemFactory} 创建临时文件项，
	 * 内容类型<b>必须</b>为 {@link MediaType#APPLICATION_OCTET_STREAM_VALUE}
	 * 而非 {@link MediaType#MULTIPART_FORM_DATA_VALUE}，原因：</p>
	 * <ol>
	 *   <li>FileItem 的 content type 最终经
	 *       {@link MinioServiceImpl#upload(FileItem, String)}
	 *       → {@code PutObjectArgs.contentType()} 传入 MinIO SDK</li>
	 *   <li>MinIO SDK 将其作为 HTTP {@code Content-Type} 头随 S3 API 请求发出</li>
	 *   <li>若为 {@code multipart/form-data}，MinIO 服务端将其视为额外的认证方式，
	 *       与 SDK 自动生成的 S3 V4 签名头（{@code x-amz-content-sha256}、
	 *       {@code x-amz-date}、{@code Authorization}）并存，
	 *       触发 Invalid Request (request has multiple authentication types) 400 错误</li>
	 *   <li>{@code application/octet-stream} 是通用二进制流 MIME 类型，
	 *       不会干扰认证流程</li>
	 * </ol>
	 *
	 * @param instream 文件输入流
	 * @param fileName 原始文件名
	 * @return 包含流数据的 FileItem
	 * @throws IOException 读取流或写入临时文件失败时抛出
	 */
	public static FileItem getFileItem(InputStream instream, String fileName) throws IOException {
		FileItemFactory factory = new DiskFileItemFactory(16, null);
		FileItem fileItem = factory.createItem("file", MediaType.APPLICATION_OCTET_STREAM_VALUE, true, fileName);
		IOUtils.copy(instream, fileItem.getOutputStream());
		return fileItem;
	}
}

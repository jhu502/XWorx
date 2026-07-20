package com.flame.minio;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flame.util.XException;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.Resource;

/**
 * MinIO 对象存储服务实现。
 *
 * <p>封装 {@link MinioClient} 的常用操作，包括 Bucket 管理、对象上传/下载、
 * 列表查询及批量删除。所有方法在遇到 MinIO 异常时统一包装为 {@link XException} 抛出。</p>
 */
@Service
public class MinioServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

	@Resource
	private MinioClient minioClient;

	private MinioServiceImpl() {
	}

	/**
	 * 确保指定 Bucket 存在，不存在则自动创建。
	 *
	 * @param name Bucket 名称
	 */
	public void existBucket(String name) {
		try {
			boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
			if (!exists) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
			}
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	/**
	 * 创建 Bucket。
	 *
	 * @param bucketName Bucket 名称
	 * @return 创建成功返回 {@code true}
	 */
	public Boolean makeBucket(String bucketName) {
		try {
			minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
		} catch (Exception e) {
			throw new XException(e);
		}
		return true;
	}

	/**
	 * 删除 Bucket。
	 *
	 * @param bucketName Bucket 名称
	 * @return 删除成功返回 {@code true}
	 */
	public Boolean removeBucket(String bucketName) {
		try {
			minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
			return true;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	/**
	 * 上传文件到指定 Bucket。
	 *
	 * <p>文件名会自动拼接时间戳以避免重名冲突：
	 * {@code 原始名_时间戳.扩展名}。</p>
	 *
	 * @param fileItem   上传的文件项，包含文件名、流和内容类型
	 * @param bucketName 目标 Bucket 名称
	 * @return 生成后的存储文件名（含时间戳）
	 */
	public String upload(FileItem fileItem, String bucketName) {
		String fileName = fileItem.getName();
		if (fileName == null)
			throw new XException("文件名为空");

		String[] split = fileName.split("\\.");
		if (split.length > 1) {
			fileName = split[0] + "_" + System.currentTimeMillis() + "." + split[1];
		} else {
			fileName = fileName + System.currentTimeMillis();
		}
		try (InputStream stream = fileItem.getInputStream()) {
			minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(stream, stream.available(), -1).contentType(fileItem.getContentType()).build());
		} catch (Exception e) {
			throw new XException(e);
		}
		return fileName;
	}

	/**
	 * 下载文件，返回字节数组响应实体。
	 *
	 * <p>将对象内容读入内存，适合小文件下载。大文件请使用 {@link #downloadContent} 获取流自行处理。</p>
	 *
	 * @param fileName   对象名称
	 * @param bucketName Bucket 名称
	 * @return 包含文件字节数组和下载头的响应实体
	 */
	public ResponseEntity<byte[]> download(String fileName, String bucketName) {
		try (InputStream in = this.downloadContent(fileName, bucketName); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			IOUtils.copy(in, out);
			byte[] bytes = out.toByteArray();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			headers.setContentLength(bytes.length);
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	/**
	 * 获取对象输入流，调用方负责关闭。
	 *
	 * @param fileName   对象名称
	 * @param bucketName Bucket 名称
	 * @return 对象的 {@link InputStream}，可用于流式下载
	 */
	public InputStream downloadContent(String fileName, String bucketName) {
		GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(fileName).build();
		try {
			return minioClient.getObject(objectArgs);
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	/**
	 * 列出 Bucket 中的所有对象。
	 *
	 * @param bucketName Bucket 名称
	 * @return 对象信息列表
	 */
	public List<MinioItem> listObjects(String bucketName) {
		Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
		List<MinioItem> objectItems = new ArrayList<>();
		try {
			for (Result<Item> result : results) {
				Item item = result.get();
				MinioItem minioItem = new MinioItem();
				minioItem.setObjectName(item.objectName());
				minioItem.setSize(item.size());
				minioItem.setVersionId(item.versionId());
				minioItem.setLastModified(item.lastModified().toString());
				minioItem.setLatest(item.isLatest());
				objectItems.add(minioItem);
			}
		} catch (Exception e) {
			throw new XException(e);
		}
		return objectItems;
	}

	/**
	 * 批量删除 Bucket 中的多个对象。
	 *
	 * @param bucketName Bucket 名称
	 * @param objects    要删除的对象名称列表
	 * @return 删除结果迭代器，包含每个对象的删除状态
	 */
	public Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> objects) {
		List<DeleteObject> dos = objects.stream().map(DeleteObject::new).collect(Collectors.toList());
		Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
		// MinIO SDK 惰性 Iterable：必须遍历消费才会发起 HTTP Delete 请求
		for (Result<DeleteError> result : results) {
			try {
				DeleteError error = result.get();
				if (error != null) {
					logger.warn("Failed to delete MinIO object in bucket [{}]: {}", bucketName, error.message());
				}
			} catch (Exception e) {
				logger.warn("Error consuming MinIO delete result in bucket [{}]: {}", bucketName, e.getMessage());
			}
		}
		return results;
	}

}

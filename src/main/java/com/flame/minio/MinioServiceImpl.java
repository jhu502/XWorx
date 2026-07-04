package com.flame.minio;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
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

@Service
public class MinioServiceImpl {
	@Resource
	private MinioClient minioClient;

	private MinioServiceImpl() {
	}

	/**
	 * description: 判断bucket是否存在，不存在则创建
	 *
	 * @return: void
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
	 * 创建存储bucket
	 * @param bucketName 存储bucket名称
	 * @return Boolean
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
	 * 删除存储bucket
	 * @param bucketName 存储bucket名称
	 * @return Boolean
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
	 * description: 上传文件
	 *
	 * @param multipartFile
	 * @return: java.lang.String
	
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
	 * description: 下载文件
	 *
	 * @param fileName
	 * @return: org.springframework.http.ResponseEntity<byte [ ]>
	 */
	public ResponseEntity<byte[]> download(String fileName, String bucketName) {
		try (InputStream in = this.downloadContent(fileName, bucketName); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			IOUtils.copy(in, out);
			//封装返回值
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

	public InputStream downloadContent(String fileName, String bucketName) {
		GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(fileName).build();
		try {
			return minioClient.getObject(objectArgs);
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	/**
	 * 查看文件对象
	 * @param bucketName 存储bucket名称
	 * @return 存储bucket内文件对象信息
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
	 * 批量删除文件对象
	 * @param bucketName 存储bucket名称
	 * @param objects 对象名称集合
	 */
	public Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> objects) {
		List<DeleteObject> dos = objects.stream().map(DeleteObject::new).collect(Collectors.toList());
		return minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
	}

}

package com.flame.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

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

public class MinioUtils {
	private static MinioClient minioClient;
	private static String bucketName;

	public class ObjectItem {
		private String objectName;
		private Long size;

		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}
	}

	public static void initMinioUtil(MinioClient client, String bucket) {
		minioClient = client;
		bucketName = bucket;
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
	public List<String> upload(MultipartFile[] multipartFile) {
		List<String> names = new ArrayList<>(multipartFile.length);
		for (MultipartFile file : multipartFile) {
			String fileName = file.getOriginalFilename();
			String[] split = fileName.split("\\.");
			if (split.length > 1) {
				fileName = split[0] + "_" + System.currentTimeMillis() + "." + split[1];
			} else {
				fileName = fileName + System.currentTimeMillis();
			}
			try (InputStream in = file.getInputStream();) {
				minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(in, in.available(), -1).contentType(file.getContentType()).build());
			} catch (Exception e) {
				e.printStackTrace();
			}
			names.add(fileName);
		}
		return names;
	}

	/**
	 * description: 下载文件
	 *
	 * @param fileName
	 * @return: org.springframework.http.ResponseEntity<byte [ ]>
	 */
	public ResponseEntity<byte[]> download(String fileName) {
		GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(fileName).build();
		ResponseEntity<byte[]> responseEntity = null;
		try (InputStream in = minioClient.getObject(objectArgs); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			IOUtils.copy(in, out);
			//封装返回值
			byte[] bytes = out.toByteArray();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			headers.setContentLength(bytes.length);
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setAccessControlExposeHeaders(Arrays.asList("*"));
			responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
		} catch (Exception e) {
			throw new XException(e);
		}
		return responseEntity;
	}

	/**
	 * 查看文件对象
	 * @param bucketName 存储bucket名称
	 * @return 存储bucket内文件对象信息
	 */
	public List<ObjectItem> listObjects(String bucketName) {
		Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
		List<ObjectItem> objectItems = new ArrayList<>();
		try {
			for (Result<Item> result : results) {
				Item item = result.get();
				ObjectItem objectItem = new ObjectItem();
				objectItem.setObjectName(item.objectName());
				objectItem.setSize(item.size());
				objectItems.add(objectItem);
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
		List<DeleteObject> dos = objects.stream().map(e -> new DeleteObject(e)).collect(Collectors.toList());
		Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
		return results;
	}

}

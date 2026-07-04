package com.flame.config.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flame.util.JsonUtils;

@RestControllerAdvice
@ResponseBody
public class RestExceptionHandler {
	protected static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)
	public void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
		logger.error(e.getMessage(), e);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write(JsonUtils.toJson(new Result<Exception>(e)));
		writer.flush();
		writer.close();
	}

	public static class Result<T> implements Serializable {
		private static final long serialVersionUID = 1L;
		// 状态码 正确为0
		private String code = "0";
		// 错误描述
		private String msg = "";

		private T data = null;

		public Result() {
		}

		public String getCode() {
			return code;
		}

		public String getMsg() {
			return msg;
		}

		public T getData() {
			return this.data;
		}

		public Result(T data) {
			if (data instanceof String) {
				this.code = "0";
				this.msg = (String) data;
				this.data = data;
			} else if (data instanceof Exception) {
				Exception e = (Exception) data;
				this.code = "-1";
				this.msg = e.getLocalizedMessage();
				this.data = null;
			} else {
				this.code = "0";
				this.msg = "Succeed";
				this.data = data;
			}
		}
	}
}

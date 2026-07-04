package com.flame.config.system;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.flame.util.JsonUtils;

import jakarta.servlet.ServletException;

public class MultiLoginExpiredStrategy implements SessionInformationExpiredStrategy {
	// private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		Map<String, Object> map = new HashMap<>(16);
		map.put("code", 0);
		map.put("msg", "已经另一台机器登录，您被迫下线。" + event.getSessionInformation().getLastRequest());
		
		String json = JsonUtils.toJson(map);

		event.getResponse().setContentType("application/json;charset=UTF-8");
		event.getResponse().getWriter().write(json);

		// 如果是跳转html页面，url代表跳转的地址
		// redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(), "url");
	}
}

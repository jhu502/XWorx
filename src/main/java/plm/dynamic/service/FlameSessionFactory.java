package plm.dynamic.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flame.config.basic.BasicConfiguration;
import com.flame.util.ObjectAnalysis;
import com.flame.util.XException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import plm.dynamic.engine.cvm.DefaultEmulator;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.XWorxModel.XParam;
import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.engine.load.XWorxDataLoader;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalParam.Source;
import plm.dynamic.engine.mdb.CalSelection;
import plm.dynamic.service.FlameSession.XParamValBean;
import plm.part.XPart;

@Controller
@RequestMapping(value = "/XUI$")
public class FlameSessionFactory {
	private static Logger logger = LoggerFactory.getLogger(FlameSessionFactory.class);
	private static final String CHOICE_GUID = "choiceGuid";
	private static final String ACTION = "action";
	private static Map<String, FlameSession> sessionMap = new ConcurrentHashMap<>();
	private static Map<String, EmulatorSession> emulatorMap = new ConcurrentHashMap<>();
	private static Timer timer = new Timer();

	static {
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					for (FlameSession session : sessionMap.values()) {
						long L0 = System.currentTimeMillis() - session.sessionTime.getTime();
						if (L0 > 300000) {
							sessionMap.remove(session.sessionId);
							logger.trace("SessionID: " + session.sessionId + " timeout, deleted.");
						}
					}

					for (EmulatorSession session : emulatorMap.values()) {
						long L0 = System.currentTimeMillis() - session.sessionTime.getTime();
						if (L0 > 300000) {
							emulatorMap.remove(session.detailName);
							logger.trace("Emulator: " + session.detailName + " timeout, deleted.");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000, 60000);
	}

	static class EmulatorSession {
		protected String detailName = null;
		protected Date sessionTime = new Date();
		private Emulator emulator;

		public static EmulatorSession newEmulatorSession(Emulator emulator) {
			EmulatorSession session = new EmulatorSession();
			session.setEmulator(emulator);
			return session;
		}

		public void setEmulator(Emulator emulator) {
			this.emulator = emulator;
			this.detailName = this.emulator.getDetailName();
		}

		public Emulator getEmulator() {
			return this.emulator;
		}

		public void refreshSessionTime() {
			this.sessionTime = new Date();
		}
	}

	public static Emulator buildEmulatorEvn(XPart topPart, String[] status) throws XException {
		String sessionKey = topPart.getNumber() + "-" + topPart.getVersion();
		logger.debug("Loading whole CTO:" + sessionKey);

		try {
			DefaultEmulator emulator = (DefaultEmulator) XWorxDataLoader.newDataLoader(topPart).loadData2Emulator();
			emulator.buildExpressionRuntime();
			emulator.preliminaryAnalysis();

			if (logger.isDebugEnabled()) {
				logger.debug("Parameter & Value:********************************************************************************************************");
				Iterator<Entry<String, CalCharacter>> it = emulator.getAllOptionChoices().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, CalCharacter> entry = it.next();
					CalCharacter param = entry.getValue();
					logger.debug(entry.getKey() + "^(" + (param.isRequired() ? "Y" : "N") + "," + (param.isDisplay() ? "Y" : "N") + ")=" + param.getEnabledChoices());
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Print OD Expression:******************************************************************************************************");
				Iterator<Entry<String, CalSelection>> it = emulator.getOdExpressions().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, CalSelection> entry = it.next();
					logger.trace(entry.getKey() + "=" + entry.getValue().getExpression());
				}
			}
			if (logger.isTraceEnabled()) {
				String dynamicHome = BasicConfiguration.getXWHome() + File.separator + "storage" + File.separator + "dynamic";
				ObjectAnalysis.generateXML4Object(emulator, dynamicHome + File.separator + topPart.getNumber() + "_EmulatorData.xml");
			}

			return emulator;
		} finally {
		}

	}

	public static FlameSession generateSimulatorSession(XPart ctoPart, String[] status) throws XException {
		FlameSession vsession = new FlameSession();
		sessionMap.put(vsession.getSessionID(), vsession);

		long l0 = System.currentTimeMillis();
		Emulator emulator = buildEmulatorEvn(ctoPart, status);
		vsession.setSimulator(XWorxSimulator.buildSimulator(emulator));
		long l1 = System.currentTimeMillis();
		logger.debug("-Loading time-------------------------:" + (l1 - l0) + "ms");

		return vsession;
	}

	public static FlameSession getSimulatorSession(String sessionID) {
		return sessionMap.get(sessionID);
	}

	public static void trace(Object obj) {
		if (logger.isTraceEnabled()) {
			String dynamicHome = BasicConfiguration.getXWHome() + File.separator + "storage" + File.separator + "dynamic";
			ObjectAnalysis.generateXML4Object(obj, dynamicHome + File.separator + obj.getClass().getName() + "_" + obj.hashCode() + ".xml");
		}
	}

	@ResponseBody
	@PostMapping(value = { "/simulate/{requestId}" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object simulateRequest(@PathVariable String requestId, @RequestParam MultiValueMap<String, Object> multiMap, HttpServletRequest request, HttpServletResponse response) {
		if (requestId == null)
			return null;

		String requestInfo = null;
		boolean isPushValue = true;
		if ("SingleSelect".equals(requestId)) {
			List<Object> choiceGuid = multiMap.get(CHOICE_GUID);
			List<Object> actions = multiMap.get(ACTION);
			if (actions != null && actions.contains("UNSELECT"))
				isPushValue = false;

			if (!choiceGuid.isEmpty())
				requestInfo = (String) choiceGuid.get(0);
		} else {
			requestInfo = requestId;
		}

		if (requestInfo == null)
			throw new XException("请求信息为空");

		XParamValBean paramValBean = FlameSession.convertRowId2XGUID(requestInfo);
		FlameSession xworxSession = FlameSessionFactory.getSimulatorSession(paramValBean.getSessionId());
		XWorxSimulator simulator = xworxSession.getSimulator().getSimulator(paramValBean.getSimulatorId());
		if (isPushValue) {
			simulator.pushInputOption(paramValBean.getParameterId(), paramValBean.getValue(), Source.INPUT);
		} else {
			simulator.popupInputOption(paramValBean.getParameterId(), paramValBean.getValue(), null);
		}

		Map<String, XParam> resultList = new HashMap<>();
		Emulator emulator = simulator.getEmulator(paramValBean.getEmulatorId());
		for (CalCharacter option : emulator.getCalCharacts()) {
			CalParam calParam = simulator.getCalParameter(option.getUUID());
			if (calParam.getRedraw()) {
				String key = paramValBean.getSessionId() + "~" + paramValBean.getSimulatorId() + "~" + paramValBean.getEmulatorId() + "~" + calParam.getUUID();
				resultList.put(key, XParam.newXParam(calParam));
			}
		}

		return resultList;
	}
}

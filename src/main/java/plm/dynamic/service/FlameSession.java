package plm.dynamic.service;

import com.flame.util.XException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plm.dynamic.engine.cvm.DefaultEmulator;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.cvm.Simulator;
import plm.dynamic.engine.cvm.XWorxSimulator;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalChoice.Status;
import plm.dynamic.engine.mdb.CalParam;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class FlameSession {
	private static Logger logger = LoggerFactory.getLogger(FlameSession.class);
	protected String sessionId = UUID.randomUUID().toString();
	protected Date sessionTime = new Date();
	private XWorxSimulator xsimulator;

	public static class XParamValBean {
		String sessionId;
		String simulatorId;
		String emulatorId;
		String parameterId;
		String value;

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public String getSimulatorId() {
			return simulatorId;
		}

		public String getSimulatorGUID() {
			return this.sessionId + "~" + this.simulatorId;
		}

		public void setSimulatorId(String simulatorId) {
			this.simulatorId = simulatorId;
		}

		public String getEmulatorId() {
			return emulatorId;
		}

		public String getEmulatorGUID() {
			return this.sessionId + "~" + this.simulatorId + "~" + this.emulatorId;
		}

		public void setEmulatorId(String emulatorId) {
			this.emulatorId = emulatorId;
		}

		public String getParameterId() {
			return parameterId;
		}

		public String getParameterGUID() {
			return this.sessionId + "~" + this.simulatorId + "~" + this.emulatorId + "~" + this.parameterId;
		}

		public void setParameterId(String parameterId) {
			this.parameterId = parameterId;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public String getSessionID() {
		return this.sessionId;
	}

	public void refreshSessionTime() {
		this.sessionTime = new Date();
	}

	public XWorxSimulator getSimulator() {
		return this.xsimulator;
	}

	public void setSimulator(XWorxSimulator simulator) {
		this.xsimulator = simulator;
	}

	public Emulator getEmulator(String uuid) {
		return this.xsimulator.getEmulator(uuid);
	}

	public CalParam getCalParameter(String name) {
		return this.xsimulator.getCalParameter(name);
	}

	public Map<String, CalParam> getCalParameters() {
		return this.xsimulator.getCalParameters();
	}

	public Map<String, Emulator> getAllEmulators() {
		return ((DefaultEmulator) this.xsimulator.getEmulator()).getAllSubEmulators();
	}

	public void executeOptionAnalysis(CalParam parameter) {
		this.xsimulator.executeOptionAnalysis(parameter);
	}

	public Simulator pushInputOption(String simulatorId, String uuid, Object value) {
		long L0 = System.currentTimeMillis();
		XWorxSimulator ownedSimulator = this.xsimulator.getSimulator(simulatorId);
		try {
			/**
			 * 如果value==null，表示用户清除了对参数param的值的选择；
			 */
			ownedSimulator.pushInputOption(uuid, value, null);

			return ownedSimulator;
		} finally {
			/**
			 * 下面的代码是打印Debug信息
			 */
			if (logger.isDebugEnabled()) {
				logger.debug(" :--------------------------------------------------------------Print CV List");
				Map<String, CalParam> selectmap = ownedSimulator.getCalParameters();
				for (Entry<String, CalParam> _entry : selectmap.entrySet()) {
					logger.debug(_entry.getValue().toString());
				}

				for (String parameter : ownedSimulator.getCalParameters().keySet()) {
					CalParam calselect = ownedSimulator.getCalParameter(parameter);
					if (calselect != null) {
						logger.debug("Characteristics:-----------------------------------------------------------------------------------------" + calselect.getName());

						for (CalChoice choice : calselect.getEnabledChoices()) {
							logger.debug(choice.value().toString());
						}

						for (CalChoice choice : calselect.getAllOptions()) {
							if (Status.DISABLED.equals(choice.getStatus())) {
								logger.debug(choice.value() + " (" + choice.getStatus() + ")   :" + choice.getPrompt());
							}
						}
					} else {
						throw new XException("M205", "There isn't parameter " + uuid + " in CTO.");
					}
				}
			}
			long L1 = System.currentTimeMillis();
			logger.debug(" :--------------------------------------------------------------End Analysis (" + (L1 - L0) + "ms)");
		}
	}

	public static XParamValBean convertRowId2XGUID(String guid) {
		XParamValBean valBean = new XParamValBean();
		String[] vals = guid.split("~");
		if (vals.length > 0)
			valBean.sessionId = vals[0];
		if (vals.length > 1)
			valBean.simulatorId = vals[1];
		if (vals.length > 2)
			valBean.emulatorId = vals[2];
		if (vals.length > 3)
			valBean.parameterId = vals[3];
		if (vals.length > 4) {
			String _guid = valBean.getSessionId() + "~" + valBean.getSimulatorId() + "~" + valBean.getEmulatorId() + "~" + valBean.getParameterId();
			valBean.value = guid.substring(_guid.length() + 1);
		}

		return valBean;
	}
}

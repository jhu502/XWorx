package plm.dynamic.engine.cvm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flame.util.XException;

import plm.dynamic.OptionMode;
import plm.dynamic.engine.exprs.Expression;
import plm.dynamic.engine.mdb.CalAssign;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalParam.Source;
import plm.dynamic.engine.type.ExtendType;

/**
 * 
 * This is a abstract class, realizing some basic function.
 * @author hujin
 * 
 */
public abstract class AbstractSimulator extends UuidObject implements Simulator {
	private static final long serialVersionUID = 1L;
	private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");
	private static final Pattern XXID_PATTERN = Pattern.compile("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}.[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");
	private static final Pattern VAR_PATTERN = Pattern.compile("([_a-zA-Z][_a-zA-Z0-9]*\\.)*[_a-zA-Z][_a-zA-Z0-9]*");
	private static final Logger logger = LoggerFactory.getLogger(AbstractSimulator.class);
	private Emulator emulator;
	protected Expression ruleExpressObject = null;
	private Set<String> enabledEmulators = new HashSet<>();
	protected Set<String> input = new LinkedHashSet<>(); //存放手工进行值设置的特性的顺序
	protected Map<String, CalParam> validParams = new LinkedHashMap<>(); //Map<UUID, CalParam>
	private Map<String, Emulator> ALL_EMULATORS = new LinkedHashMap<>();
	private Status status = Status.INWORK; //标识Simulator当前的状态：正在选配状态、选配完成状态

	/**
	 * 在模拟选配界面对已选择的特性值清除选中的值时，需要对所有的特性的可选值进行重新计算
	 * 
	 * @param paramUUid
	 * @param value
	 * @return
	 */
	protected void resetSimulator(String paramUUid, CalAssign value) {
		CalParam calParam = this.getCalParameter(paramUUid);
		Map<String, CalAssign> inputMap = new HashMap<>();
		Map<String, CalAssign> configMap = new HashMap<>();
		Map<String, CalAssign> cascadeMap = new HashMap<>();

		/**
		 * 获取和记录已选择了值的特性 & 值，并清除其选择的值，为后面重新进行计算
		 */
		for (CalParam param : this.validParams.values()) {
			if (param.getUUID().equals(paramUUid))
				continue;

			if (Source.INPUT.equals(param.getSource())) {
				inputMap.put(param.getUUID(), param.getAssign());
			} else if (Source.CONFIG.equals(param.getSource())) {
				configMap.put(param.getUUID(), param.getAssign());
			} else if (Source.CASCADE.equals(param.getSource())) {
				cascadeMap.put(param.getUUID(), param.getAssign());
			}
		}
		Emulator emulator = this.getEmulator();
		if (emulator instanceof DefaultEmulator) {
			DefaultEmulator xEmulator = (DefaultEmulator) emulator;
			xEmulator.initializeSimulator(this);
		}

		/**
		 * 计算配置赋值；
		 */
		for (Entry<String, CalAssign> entry : configMap.entrySet()) {
			CalParam param = this.getCalParameter(entry.getKey());
			if (!param.hasValue()) {
				this.setInputOption(entry.getKey(), entry.getValue(), Source.CONFIG);
			}
		}

		/**
		 * 计算级联赋值；
		 */
		for (Entry<String, CalAssign> entry : cascadeMap.entrySet()) {
			CalParam param = this.getCalParameter(entry.getKey());
			if (!param.hasValue()) {
				this.setInputOption(entry.getKey(), entry.getValue(), Source.CASCADE);
			}
		}

		/**
		 * 根据选配时挑选参数的顺序对输入参数进行重新运算；
		 */
		for (String uuid : input) {
			CalAssign assign = inputMap.get(uuid);
			if (assign != null) {
				this.setInputOption(uuid, assign, Source.INPUT);
			}
		}

		/**
		 * 如果当前是清除某个特征的值，将该特性从历史选择列表中删除；
		 * 如果是某个特性选择新值，将该特性&值添加进选择列表；
		 */
		if (!isBlank(paramUUid)) {
			if (value == null || value.isNull()) {
				inputMap.remove(paramUUid);
				this.input.remove(paramUUid);
			} else if (Source.UNKNOWN.equals(calParam.getSource()) || Source.INPUT.equals(calParam.getSource()) || Source.DEFAULT.equals(calParam.getSource())) {
				this.setInputOption(paramUUid, value, Source.INPUT);
				inputMap.put(paramUUid, value);
				this.input.add(paramUUid);
			}
		}

		/**
		 * 对默认值进行重新计算，条件：1. 默认值不为空； 2. 默认值在启用的； 3. 特性不在已选择的列表中；
		 */
		for (CalParam param : this.getCalParameters().values()) {
			if (!param.hasValue()) {
				String defaultVal = param.getDefaultValue();
				if (!isBlank(defaultVal) && param.isEnabledOption(defaultVal)) {
					this.setInputOption(param.getUUID(), CalAssign.toCalAssign(param, defaultVal), Source.DEFAULT);
				}
			}
		}
	}

	private Emulator _getEmulator(Emulator emulator, String uuid) {
		Emulator result = null;

		for (Emulator _emulator : emulator.getSubLayerEmulators()) {
			if (!this.ALL_EMULATORS.containsKey(_emulator.getUUID())) {
				this.ALL_EMULATORS.put(_emulator.getUUID(), _emulator);
			}

			if (uuid.equals(_emulator.getUUID())) {
				return _emulator;
			}

			result = _getEmulator(_emulator, uuid);
			if (result != null) {
				return result;
			}
		}

		return result;
	}

	public Emulator getEmulator() {
		return this.emulator;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Simulator getSimulator(String uuid) {
		if (uuid.equals(this.getUUID()) || uuid.equals(this.getEmulator().getUUID())) {
			return this;
		}

		return null;
	}

	public void setEmulator(Emulator emulator) {
		this.emulator = emulator;
	}

	public Emulator getEmulator(String uuid) {
		Emulator _emulator = ALL_EMULATORS.get(uuid);
		if (_emulator == null) {
			if (uuid.equals(this.getEmulator().getUUID())) {
				_emulator = this.getEmulator();
				this.ALL_EMULATORS.put(_emulator.getUUID(), _emulator);
			} else {
				_emulator = _getEmulator(this.getEmulator(), uuid);
			}
		}

		return _emulator;
	}

	public Expression getExpression() {
		return this.ruleExpressObject;
	}

	public Set<String> getEnabledEmulator() {
		return this.enabledEmulators;
	}

	public CalParam getCalParameter(String uuid) {
		return this.validParams.get(uuid);
	}

	public Map<String, CalParam> getCalParameters() {
		return this.validParams;
	}

	protected void addInputOption(String uuid, CalAssign value, Source source) {
		Map<String, CalCharacter> optionChoices = ((DefaultEmulator) this.emulator).getAllOptionChoices();

		if (!optionChoices.containsKey(uuid)) {
			logger.warn("Parameter:" + uuid + " is redundancy input parameter, please verify!");
			// throw new XException("Parameter:" + param + " is illegal, please add a valid parameter.");
		}

		CalParam calParam = this.validParams.get(uuid);
		if (calParam != null) {
			/**
			 * 如果输入的特性值是null，说明用户在模拟界面清除了特性的值
			 */
			if (value.isNull()) {
				calParam.setValue(value).setSource(Source.UNKNOWN);
			} else {
				calParam.setValue(value).setSource(source);
			}
		} else {
			//Process redundant parameter & option.
		}
	}

	/**
	 * 设置特性&值，并进行运算，当前设置的值是否违反约束；
	 * 
	 * @param uuid		特性名称
	 * @param value		特性值
	 * @param source	特性值的来源有两种：输入(Source.Input)、默认(Source.Default)	
	 */
	public void setInputOption(String uuid, CalAssign value, Source source) {
		CalParam calParam = this.getCalParameter(uuid);

		this.validateParameterValue(calParam, value);
		this.addInputOption(uuid, value, source);
		this.executeOptionAnalysis(calParam);
		this.getEmulator().calculateEmulatorStatus(this);
		this.getEmulator().calculateCharactStatus(this);
	}

	@Override
	public void pushInputOption(String paramId, Object value, Source source) {
		CalParam calparam = this.getCalParameter(paramId);
		if (calparam == null) {
			if (isReferId(paramId)) {
				CalCharacter charact = this.emulator.getCalCharact(paramId);
				if (charact != null) {
					calparam = this.getCalParameter(charact.getUUID());
				}
			} else if (isXxid(paramId)) {
				String[] ids = paramId.split(".");
				String simId = ids[0];
				String pamId = ids[1];
				Simulator simulator = this.getSimulator(simId);
				if (simulator != null) {
					calparam = simulator.getCalParameter(pamId);
				}
			}
		}

		String uuid = calparam.getUUID();
		CalAssign inValue = CalAssign.toCalAssign(calparam, value);
		if (inValue.value() instanceof ExtendType) {
			((ExtendType) inValue.value()).validate();
		}

		try {
			// 记录用户选配的顺序
			if (inValue.isNull()) {
				this.input.remove(uuid);
			} else {
				this.input.add(uuid);
			}

			/**
			 * 处理用户修改特性的选择值
			 */
			if (calparam.hasValue()) {
				this.resetSimulator(uuid, inValue);
			} else { // 为未选值的特性设置值
				this.setInputOption(uuid, inValue, source == null ? Source.INPUT : source);
			}
		} catch (XException e) {
			/**
			 * 如果发生Exception，重置所有的特性然后重新计算，并且将当前特性&值清除
			 */
			this.resetSimulator(uuid, null);
			throw e;
		}
	}

	@Override
	public void popupInputOption(String paramId, Object value, Source source) {
		CalParam calparam = this.getCalParameter(paramId);
		if (calparam == null) {
			if (isReferId(paramId)) {
				CalCharacter charact = this.emulator.getCalCharact(paramId);
				if (charact != null) {
					calparam = this.getCalParameter(charact.getUUID());
				}
			} else if (isXxid(paramId)) {
				String[] ids = paramId.split(".");
				String simId = ids[0];
				String pamId = ids[1];
				Simulator simulator = this.getSimulator(simId);
				if (simulator != null) {
					calparam = simulator.getCalParameter(pamId);
				}
			}
		}

		String uuid = calparam.getUUID();
		CalAssign assign = calparam.getAssign();
		if (assign == null)
			return;

		if (assign.value() == null) {
			calparam.setValue(null);
		} else if (assign.value() instanceof ExtendType) {
			((ExtendType) assign.value()).validate();
		} else if (assign.value() instanceof Collection) {
			Collection<?> values = (Collection<?>) assign.value();
			if (values.contains(value)) {
				values.remove(value);
			}
		} else if (assign.value().equals(value)) {
			calparam.setValue(null);
		}

		try {
			/** 处理用户修改特性的选择值 */
			if (calparam.hasValue()) {
				this.resetSimulator(uuid, assign);
			} else {
				this.resetSimulator(uuid, null);
			}
		} catch (XException e) {
			/**
			 * 如果发生Exception，重置所有的特性然后重新计算，并且将当前特性&值清除
			 */
			this.resetSimulator(uuid, null);
			throw e;
		}
	}

	public void pushConfigOption(String uuid, Object value) {
		this.pushInputOption(uuid, value, Source.CONFIG);
	}

	public void pushCascadeOption(String name, Object value) {
		CalCharacter calOption = this.getEmulator().getCalCharact(name);
		if (calOption != null) {
			this.pushInputOption(calOption.getUUID(), value, Source.CASCADE);
		}
	}

	public void pushGlobalOption(String name, Object value) {
		CalCharacter calOption = this.getEmulator().getCalCharact(name);
		if (calOption != null) {
			this.pushInputOption(calOption.getUUID(), value, Source.INPUT);
		}
	}

	private void validateParameterValue(CalParam param, CalAssign value) {
		if (!value.isNull()) {
			Object object = value.value();
			if (OptionMode.LIST.equals(param.getOptionMode())) {
				if (!param.hasEnabledChoice(object)) {
					throw new XException("M005", "选项" + param.getDetailName() + "=" + object + "是非法值!");
				} else if (object instanceof Collection) {
					Collection<?> coll = (Collection<?>) object;
					for (Object _obj : coll) {
						if (!param.hasEnabledChoice(_obj)) {
							throw new XException("M105", "选项" + param.getDetailName() + "=" + object + "是非法值!");
						}
					}
				}
			}
		}
	}

	@Override
	public void executeOptionAnalysis(CalParam parameter) {
		this.getEmulator().simulateConfiguration(this, parameter);
	}

	protected static boolean isBlank(Object value) {
		if (value == null)
			return true;

		if ("".equals(value))
			return true;

		return false;
	}

	protected static boolean isUuid(String uuid) {
		Matcher isUUID = UUID_PATTERN.matcher(uuid);
		return isUUID.matches();
	}

	protected static boolean isXxid(String uuid) {
		Matcher isXXID = XXID_PATTERN.matcher(uuid);
		return isXXID.matches();
	}

	protected static boolean isReferId(String var) {
		Matcher isVAR = VAR_PATTERN.matcher(var);
		return isVAR.matches();
	}

	public String toString() {
		return this.emulator.getDetailName();
	}
}

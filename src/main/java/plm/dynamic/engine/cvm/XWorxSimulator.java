package plm.dynamic.engine.cvm;

import plm.dynamic.engine.cvm.Emulator.ModuleCascadeMapping;
import plm.dynamic.engine.cvm.XWorxModel.XParam;
import plm.dynamic.engine.cvm.XWorxModel.XValue;
import plm.dynamic.engine.exprs.Expression;
import plm.dynamic.engine.mdb.CalAssign;
import plm.dynamic.engine.mdb.CalCharacter;
import plm.dynamic.engine.mdb.CalChoice;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalParam.Source;
import com.flame.util.XException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 根据uuid号从整个Simulator树中去获取对应的Simulator，整个模型选配器的结构：
 *  a. Simulator与SuperBOM对应，Simulator结构是SuperBOM之间使用结构，一个SuperBOM只有唯一的Simulator；(Simulator间会跨SuperBOM建立结构)
 *	b. Emualtor与Dynamic Part对应，Emulator结构是Dynamic Part的BOM结构，每个Dynamic部件对应一个Emualtor；(Emulator不会跨SuperBOM建立结构)
 *  c. SuperBOM有外部型态时，如果需要加载外部的型态SuperBOM，那么被加载的型态SuperBOM就会生成一个Simulator，	这个新的Simulator就会被关联到原先Simulator的underSimulators中，形成SuperBOM间的使用结构；
 * 
 * [XWorxSimulator] 
 * 	   |
 * 	   |============Simulator-变量关联=============> Emulator	(MD800-Solution)
 * 	   |												|-Emulator	(MD800-Module)
 *     |												|-Emulator	(MD800-Module)
 *     |												|-Emulator	(MD800-Module)
 *     |												|-Emulator	(MD800-Module)
 *     |												|-Emulator	(MD800-Module)
 *     |-[XWorxSimulator]	<----Emulator-UUID关联------	|-Emulator	(MD800-Module)
 *     |-[XWorxSimulator]	<----Emulator-UUID关联------	|-Emulator	(MD800-Module)
 *     |-[XWorxSimulator]	<----Emulator-UUID关联------	|-Emulator	(MD800-Module)
 *     			|
 * 				|==========Simulator-变量关联==========>	Emulator(MD800)
 * 				|-[XWorxSimulator]	<-----Emulator-UUID关联-----	|-Emulator	(整流器-Module)
 * 				|												|-Emulator	(逆变器-Module)
 * 				|-[XWorxSimulator]	<-----Emulator-UUID关联-----	|-Emulator	(逆变器-Module)
 * 				|-[XWorxSimulator]	<-----Emulator-UUID关联-----	|-Emulator	(逆变器-Module)
 * 				|-[XWorxSimulator]	<-----Emulator-UUID关联-----	|-Emulator	(逆变器-Module)
 * 				|-[XWorxSimulator]	<-----Emulator-UUID关联-----	|-Emulator	(逆变器-Module)
 * 						 |
 * 						 |==========Simulator-变量关联==========> Emulator(逆变器)
 * 																	  |-Emulator  (可配置部件)
 * 																	  |-Emulator  (可配置部件)
 * 																	  |-Emulator  (可配置部件)
 * @author Hujin
 *
 */
public class XWorxSimulator extends AbstractSimulator {
	private static final long serialVersionUID = 1L;
	/**
	 * Map<Emulator-UUID, Simulator>：
	 * 		Emulator-UUID：父层SuperBom Module的Emulator UUID号，Simulator自身对应的Emulator通过其emulator属性关联
	 */
	private Map<String, XWorxSimulator> underSimulators = new HashMap<>();

	private XWorxSimulator() {
	}

	public static XWorxSimulator buildSimulator(Emulator emulator) {
		XWorxSimulator simulator = new XWorxSimulator();
		simulator.setEmulator(emulator);

		Class<Expression> evalClass = emulator.getRuleExpressionClass();
		if (evalClass != null) {
			try {
				Constructor<Expression> constructor = evalClass.getConstructor();
				simulator.ruleExpressObject = constructor.newInstance();
			} catch (XException e) {
				throw e;
			} catch (Exception e) {
				throw new XException(e);
			}
		}
		if (emulator instanceof DefaultEmulator) {
			DefaultEmulator xEmulator = (DefaultEmulator) emulator;
			xEmulator.initializeSimulator(simulator);
		}

		return simulator;
	}

	public void addSimulator(String upEmulatorId, XWorxSimulator simulator) {
		this.underSimulators.put(upEmulatorId, simulator);
	}

	public Emulator deepEmulator(String emulatorId, int deep) {
		if (deep > 10) {
			throw new XException("形态嵌套，请检查");
		}

		Emulator _emulator = this.getEmulator(emulatorId);
		if (_emulator != null) {
			return _emulator;
		}

		int _deep = deep + 1;
		for (XWorxSimulator xsimulator : this.underSimulators.values()) {
			_emulator = xsimulator.deepEmulator(emulatorId, _deep);
			if (_emulator != null) {
				return _emulator;
			}
		}

		return _emulator;
	}

	/**
	 * getSimulator和pickSimulator的功能类似，都是通过uuid去搜索Simulator，但是区别在于：
	 * 1. getSimulator仅仅去比较Simulator对应Emulator结构的顶层Emulator的uuid；
	 * 2. pickSimulator会去比较Simulator对应Emulator结构的所有Emulator的uuid；
	 * 
	 * @param uuid
	 * @return
	 */
	@Override
	public XWorxSimulator getSimulator(String uuid) {
		if (uuid.equals(this.getUUID()) || uuid.equals(this.getEmulator().getUUID())) {
			return this;
		}

		for (Entry<String, XWorxSimulator> entry : this.underSimulators.entrySet()) {
			String key = entry.getKey();
			XWorxSimulator simulator = entry.getValue();
			if (uuid.equals(key)) {
				return simulator;
			} else {
				XWorxSimulator _simulator = simulator.getSimulator(uuid);
				if (_simulator != null) {
					return _simulator;
				}
			}
		}

		return null;
	}

	//	public CalParam getCalParameter(String uuid) {
	//		CalParam calParam = super.getCalParameter(uuid);
	//		if (calParam == null) {
	//			for (Simulator simulator : underSimulators.values()) {
	//				calParam = simulator.getCalParameter(uuid);
	//				if (calParam != null) {
	//					return calParam;
	//				}
	//			}
	//		}
	//
	//		return calParam;
	//	}

	@Override
	public void pushInputOption(String paramId, Object value, Source source) {
		/**
		 * 重置CalParam的Flag，用来准确标识当前调用那些CalParam被赋了值或更新了状态
		 */
		Map<String, CalAssign> origAssign = new HashMap<>();
		for (CalParam param : this.getCalParameters().values()) {
			origAssign.put(param.getUUID(), param.getAssign());
			param.setRedraw(false);
		}

		try {
			super.pushInputOption(paramId, value, source);
			/**
			 * 级联运算处理
			 */
			this.handleParameterCascade();
		} catch (XException e) {
			/**
			 * 如果发生Exception，重置所有的特性然后重新计算，并且将当前特性&值清除
			 */
			if (isReferId(paramId)) {
				CalCharacter calOption = this.getEmulator().getCalCharact(paramId);
				if (calOption != null) {
					CalParam calParam = this.getCalParameter(calOption.getUUID());
					if (calParam != null) {
						this.resetSimulator(calParam.getUUID(), null);
					}
				}
			} else {
				this.resetSimulator(paramId, null);
			}
			throw e;
		} finally {
			for (CalParam param : this.getCalParameters().values()) {
				CalAssign assign = origAssign.get(param.getUUID());
				if (assign == null) {
					if (param.getAssign() != null) {
						param.setRedraw(true);
					}
				} else {
					if (!assign.equals(param.getAssign())) {
						param.setRedraw(true);
					}
				}
			}
		}
	}

	@Override
	public void popupInputOption(String uuid, Object value, Source source) {
		/**
		 * 重置CalParam的Flag，用来准确标识当前调用那些CalParam被赋了值或更新了状态
		 */
		Map<String, CalAssign> origAssign = new HashMap<>();
		for (CalParam param : this.getCalParameters().values()) {
			origAssign.put(param.getUUID(), param.getAssign());
			param.setRedraw(false);
		}

		try {
			super.popupInputOption(uuid, value, null);
			/**
			 * 级联运算处理
			 */
			this.handleParameterCascade();
		} catch (XException e) {
			/**
			 * 如果发生Exception，重置所有的特性然后重新计算，并且将当前特性&值清除
			 */
			this.resetSimulator(uuid, null);
			throw e;
		} finally {
			for (CalParam param : this.getCalParameters().values()) {
				CalAssign assign = origAssign.get(param.getUUID());
				if (assign == null) {
					if (param.getAssign() != null) {
						param.setRedraw(true);
					}
				} else {
					if (!assign.equals(param.getAssign())) {
						param.setRedraw(true);
					}
				}
			}
		}
	}

	@Override
	public void executeOptionAnalysis(CalParam parameter) {
		this.handleParameterCascade();
		super.executeOptionAnalysis(parameter);
	}

	private void clearParameterCascade() {
		Map<String, CalAssign> inputMap = new HashMap<String, CalAssign>();
		Map<String, CalAssign> configMap = new HashMap<String, CalAssign>();

		/**
		 * 获取和记录已选择了值的特性 & 值，并清除其选择的值，为后面重新进行计算
		 */
		for (CalParam param : this.getCalParameters().values()) {
			if (Source.INPUT.equals(param.getSource())) {
				inputMap.put(param.getUUID(), param.getAssign());
			} else if (Source.CONFIG.equals(param.getSource())) {
				configMap.put(param.getUUID(), param.getAssign());
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
		 * 根据选配时挑选参数的顺序对输入参数进行重新运算；
		 */
		for (String uuid : input) {
			CalAssign assign = inputMap.get(uuid);
			if (assign != null) {
				this.setInputOption(uuid, assign, Source.INPUT);
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

	/**
	 * 多个SuperBOM进行级联运算
	 */
	private void handleParameterCascade() {
		for (Entry<String, XWorxSimulator> entry : this.underSimulators.entrySet()) {
			String em_uuid = entry.getKey();
			XWorxSimulator subSimulator = entry.getValue();
			subSimulator.clearParameterCascade();
			Emulator upEmulator = this.deepEmulator(em_uuid, 0);
			CalCharacter calOption = upEmulator.getCalCharact("VariantModule");
			if (calOption == null)
				calOption = upEmulator.getCalCharact("variantModule");
			if (calOption != null) {
				CalParam calParam = this.getCalParameter(calOption.getUUID());
				if (calParam.hasValue()) {
					String moduleNumber = (String) calParam.getValue();
					ModuleCascadeMapping cascadeMapping = upEmulator.getModuleCascadeMapping().get(moduleNumber);
					/**
					 * 开始执行级联运算
					 */
					if (cascadeMapping == null) {
						for (CalCharacter p_option : upEmulator.getCalCharacts()) {
							CalParam p_param = this.getCalParameter(p_option.getUUID()); // 获取上层Module的Parameter的值
							if (p_param.hasValue()) {
								subSimulator.pushCascadeOption(p_param.getName(), p_param.getValue());
							}
						}
					} else if (cascadeMapping.isDefaultMapping()) {
						for (CalCharacter p_option : upEmulator.getCalCharacts()) {
							if (!cascadeMapping.containsMapping(p_option.getName())) {
								CalParam p_param = this.getCalParameter(p_option.getUUID()); // 获取上层Module的Parameter的值
								if (p_param.hasValue()) {
									subSimulator.pushCascadeOption(p_param.getName(), p_param.getValue());
								}
							}
						}
					}

					if (cascadeMapping != null) {
						for (Entry<String, Object> _entry : cascadeMapping.getParamMapping().entrySet()) {
							String paramName = _entry.getKey();
							Object object = _entry.getValue();
							if (object instanceof CalCharacter) {
								CalCharacter p_option = (CalCharacter) object;
								CalParam p_param = this.getCalParameter(p_option.getUUID()); // 获取上层Module的Parameter的值
								if (p_param.hasValue()) {
									subSimulator.pushCascadeOption(paramName, p_param.getValue());
								}
							} else {
								if (object != null) {
									subSimulator.pushCascadeOption(paramName, object);
								}
							}
						}
					}
				}
			}
		}
	}

	private void handleParameterCascade(String upEmulatorId, XWorxSimulator subSimulator) {
		subSimulator.clearParameterCascade();
		Emulator upEmulator = this.deepEmulator(upEmulatorId, 0);
		CalCharacter calOption = upEmulator.getCalCharact("VariantModule");
		if (calOption == null)
			calOption = upEmulator.getCalCharact("variantModule");
		if (calOption != null) {
			CalParam calParam = this.getCalParameter(calOption.getUUID());
			if (calParam.hasValue()) {
				String moduleNumber = (String) calParam.getValue();
				ModuleCascadeMapping cascadeMapping = upEmulator.getModuleCascadeMapping().get(moduleNumber);
				/**
				 * 开始执行级联运算
				 */
				if (cascadeMapping == null) {
					for (CalCharacter p_option : upEmulator.getCalCharacts()) {
						CalParam p_param = this.getCalParameter(p_option.getUUID()); // 获取上层Module的Parameter的值
						if (p_param.hasValue()) {
							subSimulator.pushCascadeOption(p_param.getName(), p_param.getValue());
						}
					}
				} else if (cascadeMapping.isDefaultMapping()) {
					for (CalCharacter p_option : upEmulator.getCalCharacts()) {
						if (!cascadeMapping.containsMapping(p_option.getName())) {
							CalParam p_param = this.getCalParameter(p_option.getUUID()); // 获取上层Module的Parameter的值
							if (p_param.hasValue()) {
								subSimulator.pushCascadeOption(p_param.getName(), p_param.getValue());
							}
						}
					}
				}

				if (cascadeMapping != null) {
					for (Entry<String, Object> _entry : cascadeMapping.getParamMapping().entrySet()) {
						String paramName = _entry.getKey();
						Object object = _entry.getValue();
						if (object instanceof CalCharacter) {
							CalCharacter p_option = (CalCharacter) object;
							CalParam p_param = this.getCalParameter(p_option.getUUID()); // 获取上层Module的Parameter的值
							if (p_param.hasValue()) {
								subSimulator.pushCascadeOption(paramName, p_param.getValue());
							}
						} else {
							if (object != null) {
								subSimulator.pushCascadeOption(paramName, object);
							}
						}
					}
				}
			}
		}
	}

	private XWorxModel _createXWorxModel(Simulator simulator, Emulator emulator, Emulator parent) {
		XWorxModel xmodel = new XWorxModel();
		xmodel.setId(emulator.getUUID());
		xmodel.setSimulator(simulator.getUUID());
		xmodel.setDynamic(true);
		xmodel.setNumber(emulator.getNumber());
		xmodel.setDetailName(emulator.getDetailName());
		xmodel.setOid(emulator.getOid());
		xmodel.setName(emulator.getDetailName());
		if (parent == null) {
			xmodel.setQuantity(1);
		} else {
			xmodel.setQuantity(emulator.getQuantity());
		}
		for (CalCharacter calOption : emulator.getCalCharacts()) {
			CalParam calParam = simulator.getCalParameter(calOption.getUUID());
			XParam xparam = new XParam();
			xparam.uuid = calParam.getUUID();
			xparam.name = calParam.getName();
			xparam.displayName = calParam.getDisplayName();
			xparam.decription = calParam.getDescription();
			xparam.globalId = calParam.getGlobalId();
			xparam.value = calParam.getValue();
			xparam.source = calParam.getSource().toString();
			xparam.readonly = calParam.isReadonly();
			xparam.required = calParam.isRequired();
			xparam.optionType = calParam.getOptionMode().toString();
			xparam.valueType = calParam.getBaseType().getName();
			xparam.mapAttribute = calParam.getMapAttribute();
			for (CalChoice choice : calParam.getChoices()) {
				XValue xvalue = new XValue();
				xvalue.value = choice.value();
				xvalue.prompt = choice.getPrompt();
				xvalue.status = choice.getStatus().toString();

				xparam.options.put(xvalue.value, xvalue);
			}
			xmodel.addParameters(calParam.getName(), xparam);
		}

		return xmodel;
	}

	private XWorxModel _genConfigModel(XWorxSimulator simulator, Emulator emulator, Emulator parent) {
		XWorxModel xmodel = null;

		CalCharacter moduleOption = emulator.getCalCharact("VariantModule");
		if (moduleOption == null)
			moduleOption = emulator.getCalCharact("variantModule");
		if (moduleOption == null) {
			xmodel = this._createXWorxModel(simulator, emulator, parent);
			for (Emulator _emulator : emulator.getSubLayerEmulators()) {
				XWorxModel _xmodel = this._genConfigModel(simulator, _emulator, emulator);
				if (_xmodel != null) {
					xmodel.addChildModel(_xmodel);
				}
			}
		} else {
			CalParam calParam = simulator.getCalParameter(moduleOption.getUUID());
			if (calParam.hasValue()) {
				if (!"N/A".equals(calParam.getValue())) {
					XWorxSimulator _simulator = simulator.getSimulator(emulator.getUUID());
					if (_simulator == null) {
						xmodel = this._createXWorxModel(simulator, emulator, null);
					} else {
						xmodel = this._createXWorxModel(simulator, emulator, parent);
						XWorxModel _xmodel = this._genConfigModel(_simulator, _simulator.getEmulator(), null);
						if (_xmodel != null) {
							xmodel.addChildModel(_xmodel);
						}
					}
				}
			} else {
				xmodel = this._createXWorxModel(simulator, emulator, parent);
				for (Emulator _emulator : emulator.getSubLayerEmulators()) {
					XWorxModel _xmodel = this._genConfigModel(simulator, _emulator, emulator);
					if (_xmodel != null) {
						xmodel.addChildModel(_xmodel);
					}
				}
			}
		}

		return xmodel;
	}

	public XWorxModel genConfigModel() {
		XWorxModel resultModel = this._genConfigModel(this, this.getEmulator(), null);
		return resultModel;
	}

	public static void main(String[] args) {
		System.out.println(CalParam.Source.INPUT.toString());
	}
}

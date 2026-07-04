package com.thing.runtime;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flame.thing.Argument;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IServiceDefinition;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.oracle.truffle.api.object.DynamicObject;
import com.thing.ThingEntityHelper;

import jakarta.annotation.PostConstruct;

/**
 * 管理ThingModel及其Script Service在内存中的结构, 提供方法去动态加载ServiceDefinition到Script的运行环境;
 * 调用loadServiceDefinition()方法可以将新增、修改的ServiceDefinition加载进Script执行环境;
 * 调用loadPropertyDefinition()方法可以将新增、修改的PropertyDefinition加载进Script执行环境;
 *
 * @author Hujin
 *
 */
@Service
public class ThingPerformService {
	private static final Logger logger = LoggerFactory.getLogger(ThingPerformService.class);
	private String PREFIX_PT = "proto$$type$$";
	private String PREFIX_FT = "func$$tion$$";
	private FlameJScriptFactory factory;
	private final Map<String, ScriptModelPrototype> PROTOTYPEMODEL_MAP = new ConcurrentHashMap<>();
	private final Map<String, ScriptModelFunction> FUNCTIONMODEL_MAP = new ConcurrentHashMap<>();

	/**
	 * 在所有的Bean准备完成后开始此初始化方法的调用，避免出现空指针异常
	 */
	@PostConstruct
	public void initScriptExecuteEnv() {
		factory = FlameJScriptFactory.getFactory();
		factory.putGlobalMember("ThingHelper", ThingEntityHelper.thing());
		factory.putGlobalMember("EntityHelper", ThingEntityHelper.service());
	}

	public DynamicObject genDynamicObject(IServiceDefinition serviceDef) {
		StringBuilder script = new StringBuilder();
		List<Argument> arguments = serviceDef.getArguments();
		boolean bool = true;
		script.append("function ").append(serviceDef.getName()).append("(");
		for (Argument arg : arguments) {
			if (bool) {
				script.append(arg.getName());
			} else {
				script.append(",").append(arg.getName());
			}
			bool = false;
		}
		script.append(") {");
		script.append("let self=this.self;let result;");
		script.append(serviceDef.getCode());
		script.append("\nreturn result;");
		script.append("}");
		logger.trace("\n" + script.toString());
		DynamicObject funcObj = factory.genJSFunction(script.toString());

		return funcObj;
	}
	/**
	 * 在修改了ServiceDefinition后，需要将其加载进ScriptClassModel中
	 * 
	 * @param serviceDef ServiceDefinition
	 */
	public void loadServiceDefinition(IServiceDefinition serviceDef) {
		IThingModel thingModel = (IThingModel) serviceDef.getServiceProvider();
		String modelKey = thingModel.getModelKey();

		ScriptModelPrototype prototype = this.PROTOTYPEMODEL_MAP.get(modelKey);
		if (prototype == null) {
			prototype = this._genScriptModelPrototype(modelKey);
		} else {
			prototype.etlServiceDefinition(serviceDef);
		}
	}

	/**
	 * 在修改了PropertyDefinition后，需要将其加载进ScriptClassModel中
	 * 
	 * @param propertyDef PropertyDefinition
	 */
	public void loadPropertyDefinition(IPropertyDefinition propertyDef) {
		IThingModel thingModel = (IThingModel) propertyDef.getPropertyProvider();
		String modelKey = thingModel.getModelKey();

		ScriptModelPrototype prototype = this.PROTOTYPEMODEL_MAP.get(modelKey);
		if (prototype == null) {
			prototype = this._genScriptModelPrototype(modelKey);
		} else {
			prototype.etlPropertyDefinition(propertyDef);
		}
	}

	private synchronized ScriptModelPrototype _genScriptModelPrototype(String modelNumber) {
		factory.loadJavascript("var " + this.getVarName(modelNumber) + " = " + this.getVarName(modelNumber) + " || {};");
		ScriptModelPrototype prototypeModel = new ScriptModelPrototype();
		prototypeModel.setScriptObject(factory.getMember(this.getVarName(modelNumber)));
		this.PROTOTYPEMODEL_MAP.put(modelNumber, prototypeModel);

		IThingModel thingModel = (IThingModel) ThingModelHelper.manager().getThingModel(modelNumber);

		/**
		 * 将某个ThingModel中的所有的ServiceDefinition加载进内存对象ScriptProtoModel中;
		 */
		List<IServiceDefinition> serviceList = ThingModelHelper.manager().getServiceDefinition(thingModel);
		for (IServiceDefinition serviceDef : serviceList) {
			prototypeModel.etlServiceDefinition(serviceDef);
		}

		/**
		 * 将某个ThingModel中的所有的PropertyDefinition加载进内存对象ScriptProtoModel中;
		 */
		List<IPropertyDefinition> propList = ThingModelHelper.manager().getPropertyDefinition(thingModel);
		for (IPropertyDefinition propDef : propList) {
			prototypeModel.etlPropertyDefinition(propDef);
		}

		/**
		 * 处理父ScriptProtoModel
		 */
		if (thingModel.getThingModel() != null) {
			ScriptModelPrototype parentModel = this.getScriptModelPrototype(thingModel.getThingModel().getModelKey());
			prototypeModel.setParentPrototype(parentModel);
		}

		return prototypeModel;
	}

	protected ScriptModelPrototype getScriptModelPrototype(String modelNumber) {
		ScriptModelPrototype prototype = this.PROTOTYPEMODEL_MAP.get(modelNumber);
		if (prototype == null) {
			prototype = this._genScriptModelPrototype(modelNumber);
		}
		return prototype;
	}

	/**
	 * 给ThingModel生成对应的ScriptClassModel对象 和 js函数:
	 * function func$$tion$$XUser(o) { this.self = o; };
	 *
	 * a. js函数func$$tion$$XUser传入的对象是java对象：IThingEntity，调用self就可以调用IThingEntity对象的属性、方法;
	 * b. ThingModel实例化的每个Thing都会有个IThingEntity对象，ThingModel中自定义的property的当前值就存放在IThingEntity的对象中;
	 * c. Flamethrower在解析js的调用时会去判断当前调用是调用函数和读写方法，如果读取方法就会转向读写IThingEntity对象的properties属性，
	 * 这一块通过修改HostObject、JSFunctionCallNode来实现；
	 *
	 * @param modelNumber
	 * @return
	 */
	private synchronized ScriptModelFunction _genScriptModelFunction(String modelNumber) {
		StringBuffer script = new StringBuffer("function " + PREFIX_FT + modelNumber + "(o) {");
		script.append("this.self = o;");
		script.append("};");
		factory.loadJavascript(script.toString());
		ScriptModelFunction modelFunc = new ScriptModelFunction();
		modelFunc.function = factory.getMember(PREFIX_FT + modelNumber);
		this.FUNCTIONMODEL_MAP.put(modelNumber, modelFunc);

		ScriptModelPrototype prototype = getScriptModelPrototype(modelNumber);
		modelFunc.setModelPrototype(prototype);

		return modelFunc;
	}

	protected ScriptModelFunction getScriptModelFunction(String modelNumber) {
		ScriptModelFunction modelFunc = this.FUNCTIONMODEL_MAP.get(modelNumber);
		if (modelFunc == null) {
			modelFunc = this._genScriptModelFunction(modelNumber);
		}

		return modelFunc;
	}

	public ScriptModelFunction getScriptModelFunction(IThingModel thingModel) {
		ScriptModelFunction function = this.getScriptModelFunction(thingModel.getModelKey());

		return function;
	}

	private String getVarName(String modelNumber) {
		return PREFIX_PT + modelNumber;
	}
}

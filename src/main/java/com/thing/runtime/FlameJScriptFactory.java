package com.thing.runtime;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.js.lang.JavaScriptLanguage;
import com.oracle.truffle.js.nodes.ScriptNode;
import com.oracle.truffle.js.nodes.function.JSFunctionExpressionNode;
import com.oracle.truffle.js.parser.JSParser;
import com.oracle.truffle.js.runtime.JSContext;
import com.oracle.truffle.js.runtime.JSRealm;
import com.oracle.truffle.js.runtime.builtins.JSFunction;
import com.oracle.truffle.js.runtime.builtins.JSFunctionData;
import com.oracle.truffle.js.runtime.builtins.JSFunctionObject;
import com.oracle.truffle.js.runtime.objects.JSDynamicObject;
import com.oracle.truffle.js.runtime.objects.Null;
import com.flame.util.XException;

public class FlameJScriptFactory {
	private static FlameJScriptFactory singleton = null;
	private Engine engine = null;
	private Builder builder = null;
	private Context context = null;
	private Value binding = null;

	private FlameJScriptFactory() {
	}

	public static FlameJScriptFactory getFactory() {
		if (singleton == null) {
			singleton = new FlameJScriptFactory();
			singleton.engine = Engine.newBuilder().out(System.out).err(System.err).build();
			singleton.builder = Context.newBuilder(JavaScriptLanguage.ID).engine(singleton.engine).allowCreateThread(true).allowExperimentalOptions(true).allowAllAccess(true);
		}

		return singleton;
	}

	public Context getContext() {
		if (this.context == null) {
			this.context = builder.build();
		}
		return this.context;
	}

	public Value getBindings() {
		if (this.binding == null) {
			this.binding = this.getContext().getBindings("js");
		}

		return this.binding;
	}

	public void putGlobalMember(String name, Object obj) {
		this.getBindings().putMember(name, obj);
	}

	public void loadJavascript(String script) {
		this.getContext().eval("js", script);
	}

	public Value getMember(String key) {
		return this.getBindings().getMember(key);
	}

	public JSContext getJSContext() {
		return getJSRealm().getContext();
	}

	public JSRealm getJSRealm() {
		return JavaScriptLanguage.getJSRealm(getContext());
	}

	public JSDynamicObject getGlobalObject() {
		return getJSRealm().getGlobalObject();
	}

	public DynamicObject genJSFunction(String script) {
		return this.parseFirstFunction(script).getJSFunction();
	}

	public class ParsedFunction {
		private final JSFunctionData functionData;
		private JSFunctionObject functionObj;

		public ParsedFunction(JSFunctionData functionData) {
			this.functionData = functionData;
		}

		public JSFunctionObject getJSFunction() {
			if (this.functionObj == null) {
				this.functionObj = JSFunction.create(getJSRealm(), functionData);
			}

			return this.functionObj;
		}

		public Object call(Object[] args) {
			JSFunctionObject funObj = this.getJSFunction();

			return JSFunction.call(funObj, Null.instance, args);
		}

		public RootNode getRootNode() {
			return ((RootCallTarget) functionData.getCallTarget()).getRootNode();
		}
	}

	public ParsedFunction parseFirstFunction(String source) {
		return new ParsedFunction(findFirstNodeInstance(parse(source).getRootNode(), JSFunctionExpressionNode.class).getFunctionData());
	}

	private static <T> T findFirstNodeInstance(Node root, Class<T> clazz) {
		if (clazz.isInstance(root)) {
			return clazz.cast(root);
		}
		for (Node child : root.getChildren()) {
			T node = findFirstNodeInstance(child, clazz);
			if (node != null) {
				return node;
			}
		}
		return null;
	}

	private JSParser getParser() {
		return (JSParser) getJSContext().getEvaluator();
	}

	public ScriptNode parse(String script) {
		return getParser().parseScript(getJSContext(), script);
	}

	public static class DObject {
		private String name;

		public DObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static void sample() {
		FlameJScriptFactory factory = FlameJScriptFactory.getFactory();
		StringBuilder script = new StringBuilder();
		script.append("var FT_ThingModel = {");
		script.append("    baidu:function() {");
		script.append("	       let self = this.self;");
		script.append("	       print('baidu function:' + self.getName());");
		script.append("	       return 'www.baidu.com';");
		script.append("    }");
		script.append("};");
		factory.loadJavascript(script.toString());
		Value ftThingModel = factory.getMember("FT_ThingModel");

		script = new StringBuilder();
		script.append("function ThingModel(o) {");
		script.append("	   this.self = o;");
		script.append("};");
		factory.loadJavascript(script.toString());
		Value thingModel = factory.getMember("ThingModel");
		thingModel.putMember("prototype", ftThingModel);

		Value fthing = thingModel.newInstance(new DObject("Baidu Engine"));
		System.out.println(fthing.invokeMember("baidu"));

		//-------------------------------------------------------------------------------------\\
		script = new StringBuilder();
		script.append("var FT_XGroup = {");
		script.append("    google:function() {");
		script.append("	       let self = this.self;");
		script.append("	       print('google function:' + self.getName());");
		script.append("	       return 'www.google.com';");
		script.append("    }");
		script.append("};");
		factory.loadJavascript(script.toString());
		Value ftXGroup = factory.getMember("FT_XGroup");
		ftXGroup.putMember("__proto__", ftThingModel);

		script = new StringBuilder();
		script.append("function XGroup(o) {");
		script.append("	   this.self = o;");
		script.append("};");
		factory.loadJavascript(script.toString());
		Value xGroup = factory.getMember("XGroup");
		xGroup.putMember("prototype", ftXGroup);

		Value ftgroup = xGroup.newInstance(new DObject("Google Engine"));
		System.out.println(ftgroup.invokeMember("baidu"));
		System.out.println(ftgroup.invokeMember("google"));

		StringBuilder google = new StringBuilder();
		google.append("function google() {");
		google.append("	   let self = this.self;");
		google.append("	   print('google function:' + self.getName());");
		google.append("	   return 'www.google.com'");
		google.append("}");

		DynamicObject funcObj = factory.genJSFunction(google.toString());
		ftThingModel.putMember("google", funcObj);
		System.out.println(fthing.invokeMember("google"));
	}

	public static void sample0() {
		StringBuilder script = new StringBuilder();
		script.append("function FThing(o) {");
		script.append("	   this.self = o;");
		script.append("};");
		script.append("FThing.prototype = {");
		script.append("	   baidu:function() {");
		script.append("	   	   let self = this.self;");
		script.append("	   	   print('baidu function:' + self.getName());");
		script.append("	   	   return 'www.baidu.com';");
		script.append("	   }");
		script.append("};");
		script.append("FThing.prototype.constructor=FThing;");

		StringBuilder google = new StringBuilder();
		google.append("function google() {");
		google.append("	   let self = this.self;");
		google.append("	   print('google function:' + self.getName());");
		google.append("	   return 'www.google.com'");
		google.append("}");

		FlameJScriptFactory factory = FlameJScriptFactory.getFactory();
		Context context = factory.getContext();
		try {
			for (int i = 0; i < 1; i++) {
				context.eval("js", script.toString());
				Value binding = context.getBindings("js");
				Value fThing = binding.getMember("FThing");
				Value prototype = fThing.getMember("prototype");

				Value fthing = fThing.newInstance(new DObject("Search Engine"));
				System.out.println(fthing.invokeMember("baidu"));

				DynamicObject funcObj = factory.genJSFunction(google.toString());
				prototype.putMember("google", funcObj);
				System.out.println(fthing.invokeMember("google"));

				FutureTask<Void> future0 = new FutureTask<Void>(new Callable<Void>() {
					int j = 10;

					public Void call() throws Exception {
						while (j-- > 0) {
							System.out.println("A:" + fthing.invokeMember("baidu"));
						}

						return null;
					}
				});
				new Thread(future0).start();

				FutureTask<Void> future1 = new FutureTask<Void>(new Callable<Void>() {
					int j = 10;

					public Void call() throws Exception {
						while (j-- > 0) {
							System.out.println("B:" + fthing.invokeMember("google"));
						}

						return null;
					}
				});
				new Thread(future1).start();

				System.out.println(future0.get());
				System.out.println(future1.get());
			}
		} catch (InterruptedException e) {
			throw new XException(e);
		} catch (ExecutionException e) {
			throw new XException(e);
		} finally {
			context.close();
		}
	}

	public static void main(String[] args) {
		sample();
	}
}

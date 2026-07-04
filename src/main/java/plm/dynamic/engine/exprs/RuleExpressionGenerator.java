package plm.dynamic.engine.exprs;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plm.dynamic.engine.cvm.AbstractRule;
import plm.dynamic.engine.cvm.DefaultEmulator;
import plm.dynamic.engine.rule.DriverTableRule;
import plm.dynamic.engine.rule.EvaluationRule;
import plm.dynamic.engine.rule.ExpressionRule;
import com.flame.type.XBaseType;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

/**
 * 主要用来进行BOM结构的生成
 * 
 * @author hujin
 */
public class RuleExpressionGenerator implements Generator {
	private static final Logger logger = LoggerFactory.getLogger(RuleExpressionGenerator.class);
	private String name;
	private Exception exception;
	private DefaultEmulator emulator = null;

	public RuleExpressionGenerator(DefaultEmulator emulator) {
		this.emulator = emulator;
		this.setName(emulator.getNumber());
	}

	public Exception getException() {
		return this.exception;
	}

	public void setException(Exception e) {
		this.exception = e;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private void appendFieldMethod(CtClass ctcls, String methodName, AbstractRule calRule) throws Exception {
		try {
			Class<?>[] optionTypes = XBaseType.toPrototypes(calRule.getCharactType());
			String[] variables = calRule.getVariables();
			String sentence = calRule.getSentence();
			StringBuffer body = new StringBuffer();
			body.append("public static ").append(calRule.getResultType().getName()).append(" ").append(methodName).append("(");
			for (int i = 0; i < optionTypes.length; i++) {
				Class<?> cls = optionTypes[i];
				if (i == 0)
					body.append(cls.getName()).append(" ").append(variables[i]);
				else
					body.append(", ").append(cls.getName()).append(" ").append(variables[i]);

			}
			body.append(") {\n");
			if (sentence.contains("result ")) {
				body.append(sentence);
			} else {
				body.append("return ").append(sentence).append(";\n");
			}
			body.append("}");
			logger.trace(body.toString());

			ctcls.addMethod(CtNewMethod.make(body.toString(), ctcls));
		} catch (Exception e) {
			this.exception = e;
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(ClassPool clsPool, Map<String, Class<Expression>> duplication) {
		logger.debug("Start append od expression.");
		synchronized (clsPool) {

			/**
			 * SelectionODGenerator Class如果已经生成过，不需要重复生成，直接使用即可
			 */
			String className = "RuleExpression_" + this.name;
			if (duplication.containsKey(className)) {
				this.emulator.setRuleExpressionClass(duplication.get(className));
				return;
			}

			clsPool.importPackage(Expression.class.getPackage().getName());

			CtClass ctClass = null;
			try {
				ctClass = clsPool.get(RuleExpressionTemplate.class.getName());
				/**
				 * 设置名称后就会复制一个新的CtClass，如果在设置名称前对CtlClass进行修改，就是对ExpressionTemplate进行修改
				 */
				ctClass.setName(className);

				for (ExpressionRule expression : this.emulator.getAllExpressionRules().values()) {
					logger.trace("Type:" + expression.getResultType() + " Name:" + expression.getMethodName() + "  Express:" + expression);
					appendFieldMethod(ctClass, expression.getMethodName(), expression);
				}

				for (EvaluationRule evaluation : this.emulator.getAllEvaluationRules().values()) {
					logger.trace("Type:" + evaluation.getResultType() + " Name:" + evaluation.getMethodName() + "  Express:" + evaluation);
					appendFieldMethod(ctClass, evaluation.getMethodName(), evaluation);
				}

				for (DriverTableRule driverTable : this.emulator.getAllDriverTableRules().values()) {
					logger.trace("Type:" + driverTable.getResultType() + " Name:" + driverTable.getMethodName() + "  Express:" + driverTable);
					appendFieldMethod(ctClass, driverTable.getMethodName(), driverTable);
				}

				Class<Expression> genClass = (Class<Expression>) ctClass.toClass();
				duplication.put(className, genClass);
				emulator.setRuleExpressionClass(genClass);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				this.exception = e;
			} finally {
				if (ctClass != null) {
					ctClass.defrost();
					ctClass.detach();
				}
			}
		}
	}
}

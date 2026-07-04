package plm.dynamic.engine.exprs;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.exprs.g4.RuleExpressionParser;
import plm.dynamic.engine.exprs.g4.RuleExpressionParserBaseListener;
import plm.dynamic.engine.mdb.CalCharacter;
import com.flame.util.XException;

public class RuleExpressionAnalyzeListener extends RuleExpressionParserBaseListener {
	private StringBuffer expression = new StringBuffer();
	private StringBuffer sentence = new StringBuffer();
	private Map<String, String> paramMap = new HashMap<String, String>();
	private Map<String, String> clazzMap = new HashMap<String, String>();
	private Map<String, CalCharacter> optionMap = new TreeMap<String, CalCharacter>();
	private Integer icount = 0;
	private Emulator emulator;

	public RuleExpressionAnalyzeListener(Emulator emulator) {
		this.emulator = emulator;
	}

	public String getExpression() {
		return this.expression.toString();
	}

	public String getSentence() {
		return this.sentence.toString();
	}

	public Map<String, String> getParamMap() {
		return this.paramMap;
	}

	public Map<String, String> getClazzMap() {
		return this.clazzMap;
	}

	public Map<String, CalCharacter> getOptionMap() {
		return this.optionMap;
	}

	@Override
	public void enterObject(RuleExpressionParser.ObjectContext ctx) {
		String text = ctx.getText();
		String variable = paramMap.get(text);
		if (variable == null) {
			variable = (String) LookUpProperties.getLookUpProperties().get(text);
			if (variable == null) {
				if (this.emulator == null) { //用来直接测试表达式，因为测试表达式Emulator环境不是必须的
					variable = "P" + (icount++);
				} else {
					CalCharacter calOption = emulator.getCalCharact(text);
					variable = calOption.getVariableName();
					optionMap.put(calOption.getVariableName(), calOption);
				}
				paramMap.put(text, variable);
			} else {
				clazzMap.put(text, variable);
			}
		}

		expression.append(variable);
		sentence.append(ctx.getText());
	}

	@Override
	public void enterCalc(RuleExpressionParser.CalcContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterLogic(RuleExpressionParser.LogicContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterMinus(RuleExpressionParser.MinusContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterBool(RuleExpressionParser.BoolContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterFunction(RuleExpressionParser.FunctionContext ctx) {
		String fullname = LookUpProperties.getLookUpProperties().getProperty(ctx.getText() + "()");
		if (fullname == null) {
			throw new XException("函数：" + ctx.getText() + "不存在！");
		}
		expression.append(fullname);
		sentence.append(fullname);
	}

	@Override
	public void enterMethod(RuleExpressionParser.MethodContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterString(RuleExpressionParser.StringContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override public void enterLongs(RuleExpressionParser.LongsContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterDigits(RuleExpressionParser.DigitsContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterQmark(RuleExpressionParser.QmarkContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterColon(RuleExpressionParser.ColonContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterComps(RuleExpressionParser.CompsContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterNil(RuleExpressionParser.NilContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterPl(RuleExpressionParser.PlContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterPr(RuleExpressionParser.PrContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterDot(RuleExpressionParser.DotContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterNot(RuleExpressionParser.NotContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

	@Override
	public void enterComma(RuleExpressionParser.CommaContext ctx) {
		expression.append(ctx.getText());
		sentence.append(ctx.getText());
	}

}

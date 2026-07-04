// Generated from D:/SourceSpace/SpaceFlame/XModule-PLM/src/main/java/plm/dynamic/engine/exprs/g4/RuleExpressionParser.g4 by ANTLR 4.13.2

  package plm.dynamic.engine.exprs.g4;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RuleExpressionParser}.
 */
public interface RuleExpressionParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#ruleExpr}.
	 * @param ctx the parse tree
	 */
	void enterRuleExpr(RuleExpressionParser.RuleExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#ruleExpr}.
	 * @param ctx the parse tree
	 */
	void exitRuleExpr(RuleExpressionParser.RuleExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(RuleExpressionParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(RuleExpressionParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#calc}.
	 * @param ctx the parse tree
	 */
	void enterCalc(RuleExpressionParser.CalcContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#calc}.
	 * @param ctx the parse tree
	 */
	void exitCalc(RuleExpressionParser.CalcContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#minus}.
	 * @param ctx the parse tree
	 */
	void enterMinus(RuleExpressionParser.MinusContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#minus}.
	 * @param ctx the parse tree
	 */
	void exitMinus(RuleExpressionParser.MinusContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#logic}.
	 * @param ctx the parse tree
	 */
	void enterLogic(RuleExpressionParser.LogicContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#logic}.
	 * @param ctx the parse tree
	 */
	void exitLogic(RuleExpressionParser.LogicContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#bool}.
	 * @param ctx the parse tree
	 */
	void enterBool(RuleExpressionParser.BoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#bool}.
	 * @param ctx the parse tree
	 */
	void exitBool(RuleExpressionParser.BoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#charact}.
	 * @param ctx the parse tree
	 */
	void enterCharact(RuleExpressionParser.CharactContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#charact}.
	 * @param ctx the parse tree
	 */
	void exitCharact(RuleExpressionParser.CharactContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(RuleExpressionParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(RuleExpressionParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#method}.
	 * @param ctx the parse tree
	 */
	void enterMethod(RuleExpressionParser.MethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#method}.
	 * @param ctx the parse tree
	 */
	void exitMethod(RuleExpressionParser.MethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#referid}.
	 * @param ctx the parse tree
	 */
	void enterReferid(RuleExpressionParser.ReferidContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#referid}.
	 * @param ctx the parse tree
	 */
	void exitReferid(RuleExpressionParser.ReferidContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(RuleExpressionParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(RuleExpressionParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#digits}.
	 * @param ctx the parse tree
	 */
	void enterDigits(RuleExpressionParser.DigitsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#digits}.
	 * @param ctx the parse tree
	 */
	void exitDigits(RuleExpressionParser.DigitsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#longs}.
	 * @param ctx the parse tree
	 */
	void enterLongs(RuleExpressionParser.LongsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#longs}.
	 * @param ctx the parse tree
	 */
	void exitLongs(RuleExpressionParser.LongsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#pl}.
	 * @param ctx the parse tree
	 */
	void enterPl(RuleExpressionParser.PlContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#pl}.
	 * @param ctx the parse tree
	 */
	void exitPl(RuleExpressionParser.PlContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#pr}.
	 * @param ctx the parse tree
	 */
	void enterPr(RuleExpressionParser.PrContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#pr}.
	 * @param ctx the parse tree
	 */
	void exitPr(RuleExpressionParser.PrContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#point}.
	 * @param ctx the parse tree
	 */
	void enterPoint(RuleExpressionParser.PointContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#point}.
	 * @param ctx the parse tree
	 */
	void exitPoint(RuleExpressionParser.PointContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#nil}.
	 * @param ctx the parse tree
	 */
	void enterNil(RuleExpressionParser.NilContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#nil}.
	 * @param ctx the parse tree
	 */
	void exitNil(RuleExpressionParser.NilContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#dot}.
	 * @param ctx the parse tree
	 */
	void enterDot(RuleExpressionParser.DotContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#dot}.
	 * @param ctx the parse tree
	 */
	void exitDot(RuleExpressionParser.DotContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#not}.
	 * @param ctx the parse tree
	 */
	void enterNot(RuleExpressionParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#not}.
	 * @param ctx the parse tree
	 */
	void exitNot(RuleExpressionParser.NotContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#colon}.
	 * @param ctx the parse tree
	 */
	void enterColon(RuleExpressionParser.ColonContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#colon}.
	 * @param ctx the parse tree
	 */
	void exitColon(RuleExpressionParser.ColonContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#qmark}.
	 * @param ctx the parse tree
	 */
	void enterQmark(RuleExpressionParser.QmarkContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#qmark}.
	 * @param ctx the parse tree
	 */
	void exitQmark(RuleExpressionParser.QmarkContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#comma}.
	 * @param ctx the parse tree
	 */
	void enterComma(RuleExpressionParser.CommaContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#comma}.
	 * @param ctx the parse tree
	 */
	void exitComma(RuleExpressionParser.CommaContext ctx);
	/**
	 * Enter a parse tree produced by {@link RuleExpressionParser#comps}.
	 * @param ctx the parse tree
	 */
	void enterComps(RuleExpressionParser.CompsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RuleExpressionParser#comps}.
	 * @param ctx the parse tree
	 */
	void exitComps(RuleExpressionParser.CompsContext ctx);
}
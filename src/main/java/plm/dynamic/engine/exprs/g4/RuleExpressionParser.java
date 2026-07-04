// Generated from D:/SourceSpace/SpaceFlame/XModule-PLM/src/main/java/plm/dynamic/engine/exprs/g4/RuleExpressionParser.g4 by ANTLR 4.13.2

  package plm.dynamic.engine.exprs.g4;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RuleExpressionParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TRUE=1, FALSE=2, NIL=3, PT_L=4, PT_R=5, DOT=6, QUOTE=7, ADD=8, SUB=9, 
		MUL=10, DIV=11, PER=12, QMARK=13, COMMA=14, COLON=15, EQ=16, NE=17, GT=18, 
		LT=19, GE=20, LE=21, AND=22, OR=23, NOT=24, LONG=25, NAME=26, DIGITS=27, 
		STRING=28, EXPONENT=29, WS=30;
	public static final int
		RULE_ruleExpr = 0, RULE_object = 1, RULE_calc = 2, RULE_minus = 3, RULE_logic = 4, 
		RULE_bool = 5, RULE_charact = 6, RULE_function = 7, RULE_method = 8, RULE_referid = 9, 
		RULE_string = 10, RULE_digits = 11, RULE_longs = 12, RULE_pl = 13, RULE_pr = 14, 
		RULE_point = 15, RULE_nil = 16, RULE_dot = 17, RULE_not = 18, RULE_colon = 19, 
		RULE_qmark = 20, RULE_comma = 21, RULE_comps = 22;
	private static String[] makeRuleNames() {
		return new String[] {
			"ruleExpr", "object", "calc", "minus", "logic", "bool", "charact", "function", 
			"method", "referid", "string", "digits", "longs", "pl", "pr", "point", 
			"nil", "dot", "not", "colon", "qmark", "comma", "comps"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'true'", "'false'", "'null'", "'('", "')'", "'.'", "'\"'", "'+'", 
			"'-'", "'*'", "'/'", "'%'", "'?'", "','", "':'", "'=='", "'!='", "'>'", 
			"'<'", "'>='", "'<='", "'&&'", "'||'", "'!'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TRUE", "FALSE", "NIL", "PT_L", "PT_R", "DOT", "QUOTE", "ADD", 
			"SUB", "MUL", "DIV", "PER", "QMARK", "COMMA", "COLON", "EQ", "NE", "GT", 
			"LT", "GE", "LE", "AND", "OR", "NOT", "LONG", "NAME", "DIGITS", "STRING", 
			"EXPONENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "RuleExpressionParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RuleExpressionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RuleExprContext extends ParserRuleContext {
		public NilContext nil() {
			return getRuleContext(NilContext.class,0);
		}
		public BoolContext bool() {
			return getRuleContext(BoolContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public LongsContext longs() {
			return getRuleContext(LongsContext.class,0);
		}
		public DigitsContext digits() {
			return getRuleContext(DigitsContext.class,0);
		}
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public PlContext pl() {
			return getRuleContext(PlContext.class,0);
		}
		public List<RuleExprContext> ruleExpr() {
			return getRuleContexts(RuleExprContext.class);
		}
		public RuleExprContext ruleExpr(int i) {
			return getRuleContext(RuleExprContext.class,i);
		}
		public PrContext pr() {
			return getRuleContext(PrContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public MinusContext minus() {
			return getRuleContext(MinusContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public List<CommaContext> comma() {
			return getRuleContexts(CommaContext.class);
		}
		public CommaContext comma(int i) {
			return getRuleContext(CommaContext.class,i);
		}
		public CalcContext calc() {
			return getRuleContext(CalcContext.class,0);
		}
		public LogicContext logic() {
			return getRuleContext(LogicContext.class,0);
		}
		public CompsContext comps() {
			return getRuleContext(CompsContext.class,0);
		}
		public QmarkContext qmark() {
			return getRuleContext(QmarkContext.class,0);
		}
		public ColonContext colon() {
			return getRuleContext(ColonContext.class,0);
		}
		public DotContext dot() {
			return getRuleContext(DotContext.class,0);
		}
		public MethodContext method() {
			return getRuleContext(MethodContext.class,0);
		}
		public RuleExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterRuleExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitRuleExpr(this);
		}
	}

	public final RuleExprContext ruleExpr() throws RecognitionException {
		return ruleExpr(0);
	}

	private RuleExprContext ruleExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		RuleExprContext _localctx = new RuleExprContext(_ctx, _parentState);
		RuleExprContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_ruleExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(47);
				nil();
				}
				break;
			case 2:
				{
				setState(48);
				bool();
				}
				break;
			case 3:
				{
				setState(49);
				string();
				}
				break;
			case 4:
				{
				setState(50);
				longs();
				}
				break;
			case 5:
				{
				setState(51);
				digits();
				}
				break;
			case 6:
				{
				setState(52);
				object();
				}
				break;
			case 7:
				{
				setState(53);
				pl();
				setState(54);
				ruleExpr(0);
				setState(55);
				pr();
				}
				break;
			case 8:
				{
				setState(57);
				not();
				setState(58);
				ruleExpr(8);
				}
				break;
			case 9:
				{
				setState(60);
				minus();
				setState(61);
				ruleExpr(7);
				}
				break;
			case 10:
				{
				setState(63);
				function();
				setState(64);
				pl();
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 520094238L) != 0)) {
					{
					setState(65);
					ruleExpr(0);
					setState(71);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(66);
						comma();
						setState(67);
						ruleExpr(0);
						}
						}
						setState(73);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(76);
				pr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(117);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(115);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new RuleExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_ruleExpr);
						setState(80);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(81);
						calc();
						setState(82);
						ruleExpr(7);
						}
						break;
					case 2:
						{
						_localctx = new RuleExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_ruleExpr);
						setState(84);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(85);
						logic();
						setState(86);
						ruleExpr(6);
						}
						break;
					case 3:
						{
						_localctx = new RuleExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_ruleExpr);
						setState(88);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(89);
						comps();
						setState(90);
						ruleExpr(5);
						}
						break;
					case 4:
						{
						_localctx = new RuleExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_ruleExpr);
						setState(92);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(93);
						qmark();
						setState(94);
						ruleExpr(0);
						setState(95);
						colon();
						setState(96);
						ruleExpr(4);
						}
						break;
					case 5:
						{
						_localctx = new RuleExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_ruleExpr);
						setState(98);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(99);
						dot();
						setState(100);
						method();
						setState(101);
						pl();
						setState(111);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 520094238L) != 0)) {
							{
							setState(102);
							ruleExpr(0);
							setState(108);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==COMMA) {
								{
								{
								setState(103);
								comma();
								setState(104);
								ruleExpr(0);
								}
								}
								setState(110);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
						}

						setState(113);
						pr();
						}
						break;
					}
					} 
				}
				setState(119);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectContext extends ParserRuleContext {
		public CharactContext charact() {
			return getRuleContext(CharactContext.class,0);
		}
		public List<ReferidContext> referid() {
			return getRuleContexts(ReferidContext.class);
		}
		public ReferidContext referid(int i) {
			return getRuleContext(ReferidContext.class,i);
		}
		public List<PointContext> point() {
			return getRuleContexts(PointContext.class);
		}
		public PointContext point(int i) {
			return getRuleContext(PointContext.class,i);
		}
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitObject(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_object);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(120);
					referid();
					setState(121);
					point();
					}
					} 
				}
				setState(127);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(128);
			charact();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CalcContext extends ParserRuleContext {
		public TerminalNode ADD() { return getToken(RuleExpressionParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(RuleExpressionParser.SUB, 0); }
		public TerminalNode MUL() { return getToken(RuleExpressionParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(RuleExpressionParser.DIV, 0); }
		public TerminalNode PER() { return getToken(RuleExpressionParser.PER, 0); }
		public CalcContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterCalc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitCalc(this);
		}
	}

	public final CalcContext calc() throws RecognitionException {
		CalcContext _localctx = new CalcContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_calc);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 7936L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MinusContext extends ParserRuleContext {
		public TerminalNode SUB() { return getToken(RuleExpressionParser.SUB, 0); }
		public MinusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minus; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterMinus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitMinus(this);
		}
	}

	public final MinusContext minus() throws RecognitionException {
		MinusContext _localctx = new MinusContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_minus);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(SUB);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LogicContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(RuleExpressionParser.AND, 0); }
		public TerminalNode OR() { return getToken(RuleExpressionParser.OR, 0); }
		public LogicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterLogic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitLogic(this);
		}
	}

	public final LogicContext logic() throws RecognitionException {
		LogicContext _localctx = new LogicContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_logic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			_la = _input.LA(1);
			if ( !(_la==AND || _la==OR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoolContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(RuleExpressionParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(RuleExpressionParser.FALSE, 0); }
		public BoolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitBool(this);
		}
	}

	public final BoolContext bool() throws RecognitionException {
		BoolContext _localctx = new BoolContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CharactContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(RuleExpressionParser.NAME, 0); }
		public CharactContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_charact; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterCharact(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitCharact(this);
		}
	}

	public final CharactContext charact() throws RecognitionException {
		CharactContext _localctx = new CharactContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_charact);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(RuleExpressionParser.NAME, 0); }
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitFunction(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MethodContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(RuleExpressionParser.NAME, 0); }
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitMethod(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_method);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReferidContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(RuleExpressionParser.NAME, 0); }
		public ReferidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_referid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterReferid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitReferid(this);
		}
	}

	public final ReferidContext referid() throws RecognitionException {
		ReferidContext _localctx = new ReferidContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_referid);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(RuleExpressionParser.STRING, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitString(this);
		}
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DigitsContext extends ParserRuleContext {
		public TerminalNode DIGITS() { return getToken(RuleExpressionParser.DIGITS, 0); }
		public TerminalNode EXPONENT() { return getToken(RuleExpressionParser.EXPONENT, 0); }
		public DigitsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digits; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterDigits(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitDigits(this);
		}
	}

	public final DigitsContext digits() throws RecognitionException {
		DigitsContext _localctx = new DigitsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_digits);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			match(DIGITS);
			setState(150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(149);
				match(EXPONENT);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LongsContext extends ParserRuleContext {
		public TerminalNode LONG() { return getToken(RuleExpressionParser.LONG, 0); }
		public LongsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_longs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterLongs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitLongs(this);
		}
	}

	public final LongsContext longs() throws RecognitionException {
		LongsContext _localctx = new LongsContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_longs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			match(LONG);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PlContext extends ParserRuleContext {
		public TerminalNode PT_L() { return getToken(RuleExpressionParser.PT_L, 0); }
		public PlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterPl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitPl(this);
		}
	}

	public final PlContext pl() throws RecognitionException {
		PlContext _localctx = new PlContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_pl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			match(PT_L);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrContext extends ParserRuleContext {
		public TerminalNode PT_R() { return getToken(RuleExpressionParser.PT_R, 0); }
		public PrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterPr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitPr(this);
		}
	}

	public final PrContext pr() throws RecognitionException {
		PrContext _localctx = new PrContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_pr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(PT_R);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PointContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(RuleExpressionParser.DOT, 0); }
		public PointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_point; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterPoint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitPoint(this);
		}
	}

	public final PointContext point() throws RecognitionException {
		PointContext _localctx = new PointContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(DOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NilContext extends ParserRuleContext {
		public TerminalNode NIL() { return getToken(RuleExpressionParser.NIL, 0); }
		public NilContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nil; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterNil(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitNil(this);
		}
	}

	public final NilContext nil() throws RecognitionException {
		NilContext _localctx = new NilContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_nil);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(NIL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DotContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(RuleExpressionParser.DOT, 0); }
		public DotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterDot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitDot(this);
		}
	}

	public final DotContext dot() throws RecognitionException {
		DotContext _localctx = new DotContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_dot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			match(DOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NotContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(RuleExpressionParser.NOT, 0); }
		public NotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitNot(this);
		}
	}

	public final NotContext not() throws RecognitionException {
		NotContext _localctx = new NotContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(NOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColonContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(RuleExpressionParser.COLON, 0); }
		public ColonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colon; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterColon(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitColon(this);
		}
	}

	public final ColonContext colon() throws RecognitionException {
		ColonContext _localctx = new ColonContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_colon);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(COLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QmarkContext extends ParserRuleContext {
		public TerminalNode QMARK() { return getToken(RuleExpressionParser.QMARK, 0); }
		public QmarkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qmark; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterQmark(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitQmark(this);
		}
	}

	public final QmarkContext qmark() throws RecognitionException {
		QmarkContext _localctx = new QmarkContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_qmark);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(QMARK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommaContext extends ParserRuleContext {
		public TerminalNode COMMA() { return getToken(RuleExpressionParser.COMMA, 0); }
		public CommaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comma; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterComma(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitComma(this);
		}
	}

	public final CommaContext comma() throws RecognitionException {
		CommaContext _localctx = new CommaContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_comma);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(COMMA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CompsContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(RuleExpressionParser.EQ, 0); }
		public TerminalNode NE() { return getToken(RuleExpressionParser.NE, 0); }
		public TerminalNode GT() { return getToken(RuleExpressionParser.GT, 0); }
		public TerminalNode LT() { return getToken(RuleExpressionParser.LT, 0); }
		public TerminalNode GE() { return getToken(RuleExpressionParser.GE, 0); }
		public TerminalNode LE() { return getToken(RuleExpressionParser.LE, 0); }
		public CompsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comps; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).enterComps(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RuleExpressionParserListener ) ((RuleExpressionParserListener)listener).exitComps(this);
		}
	}

	public final CompsContext comps() throws RecognitionException {
		CompsContext _localctx = new CompsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_comps);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 4128768L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return ruleExpr_sempred((RuleExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean ruleExpr_sempred(RuleExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u001e\u00af\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0005\u0000F\b\u0000\n\u0000\f\u0000I\t\u0000\u0003"+
		"\u0000K\b\u0000\u0001\u0000\u0001\u0000\u0003\u0000O\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0005\u0000k\b\u0000\n\u0000\f\u0000n\t\u0000\u0003\u0000"+
		"p\b\u0000\u0001\u0000\u0001\u0000\u0005\u0000t\b\u0000\n\u0000\f\u0000"+
		"w\t\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001|\b\u0001\n\u0001"+
		"\f\u0001\u007f\t\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0003\u000b\u0097\b"+
		"\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015"+
		"\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0000\u0001\u0000\u0017"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,\u0000\u0004\u0001\u0000\b\f\u0001\u0000\u0016\u0017"+
		"\u0001\u0000\u0001\u0002\u0001\u0000\u0010\u0015\u00ab\u0000N\u0001\u0000"+
		"\u0000\u0000\u0002}\u0001\u0000\u0000\u0000\u0004\u0082\u0001\u0000\u0000"+
		"\u0000\u0006\u0084\u0001\u0000\u0000\u0000\b\u0086\u0001\u0000\u0000\u0000"+
		"\n\u0088\u0001\u0000\u0000\u0000\f\u008a\u0001\u0000\u0000\u0000\u000e"+
		"\u008c\u0001\u0000\u0000\u0000\u0010\u008e\u0001\u0000\u0000\u0000\u0012"+
		"\u0090\u0001\u0000\u0000\u0000\u0014\u0092\u0001\u0000\u0000\u0000\u0016"+
		"\u0094\u0001\u0000\u0000\u0000\u0018\u0098\u0001\u0000\u0000\u0000\u001a"+
		"\u009a\u0001\u0000\u0000\u0000\u001c\u009c\u0001\u0000\u0000\u0000\u001e"+
		"\u009e\u0001\u0000\u0000\u0000 \u00a0\u0001\u0000\u0000\u0000\"\u00a2"+
		"\u0001\u0000\u0000\u0000$\u00a4\u0001\u0000\u0000\u0000&\u00a6\u0001\u0000"+
		"\u0000\u0000(\u00a8\u0001\u0000\u0000\u0000*\u00aa\u0001\u0000\u0000\u0000"+
		",\u00ac\u0001\u0000\u0000\u0000./\u0006\u0000\uffff\uffff\u0000/O\u0003"+
		" \u0010\u00000O\u0003\n\u0005\u00001O\u0003\u0014\n\u00002O\u0003\u0018"+
		"\f\u00003O\u0003\u0016\u000b\u00004O\u0003\u0002\u0001\u000056\u0003\u001a"+
		"\r\u000067\u0003\u0000\u0000\u000078\u0003\u001c\u000e\u00008O\u0001\u0000"+
		"\u0000\u00009:\u0003$\u0012\u0000:;\u0003\u0000\u0000\b;O\u0001\u0000"+
		"\u0000\u0000<=\u0003\u0006\u0003\u0000=>\u0003\u0000\u0000\u0007>O\u0001"+
		"\u0000\u0000\u0000?@\u0003\u000e\u0007\u0000@J\u0003\u001a\r\u0000AG\u0003"+
		"\u0000\u0000\u0000BC\u0003*\u0015\u0000CD\u0003\u0000\u0000\u0000DF\u0001"+
		"\u0000\u0000\u0000EB\u0001\u0000\u0000\u0000FI\u0001\u0000\u0000\u0000"+
		"GE\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HK\u0001\u0000\u0000"+
		"\u0000IG\u0001\u0000\u0000\u0000JA\u0001\u0000\u0000\u0000JK\u0001\u0000"+
		"\u0000\u0000KL\u0001\u0000\u0000\u0000LM\u0003\u001c\u000e\u0000MO\u0001"+
		"\u0000\u0000\u0000N.\u0001\u0000\u0000\u0000N0\u0001\u0000\u0000\u0000"+
		"N1\u0001\u0000\u0000\u0000N2\u0001\u0000\u0000\u0000N3\u0001\u0000\u0000"+
		"\u0000N4\u0001\u0000\u0000\u0000N5\u0001\u0000\u0000\u0000N9\u0001\u0000"+
		"\u0000\u0000N<\u0001\u0000\u0000\u0000N?\u0001\u0000\u0000\u0000Ou\u0001"+
		"\u0000\u0000\u0000PQ\n\u0006\u0000\u0000QR\u0003\u0004\u0002\u0000RS\u0003"+
		"\u0000\u0000\u0007St\u0001\u0000\u0000\u0000TU\n\u0005\u0000\u0000UV\u0003"+
		"\b\u0004\u0000VW\u0003\u0000\u0000\u0006Wt\u0001\u0000\u0000\u0000XY\n"+
		"\u0004\u0000\u0000YZ\u0003,\u0016\u0000Z[\u0003\u0000\u0000\u0005[t\u0001"+
		"\u0000\u0000\u0000\\]\n\u0003\u0000\u0000]^\u0003(\u0014\u0000^_\u0003"+
		"\u0000\u0000\u0000_`\u0003&\u0013\u0000`a\u0003\u0000\u0000\u0004at\u0001"+
		"\u0000\u0000\u0000bc\n\u0001\u0000\u0000cd\u0003\"\u0011\u0000de\u0003"+
		"\u0010\b\u0000eo\u0003\u001a\r\u0000fl\u0003\u0000\u0000\u0000gh\u0003"+
		"*\u0015\u0000hi\u0003\u0000\u0000\u0000ik\u0001\u0000\u0000\u0000jg\u0001"+
		"\u0000\u0000\u0000kn\u0001\u0000\u0000\u0000lj\u0001\u0000\u0000\u0000"+
		"lm\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000"+
		"\u0000of\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pq\u0001\u0000"+
		"\u0000\u0000qr\u0003\u001c\u000e\u0000rt\u0001\u0000\u0000\u0000sP\u0001"+
		"\u0000\u0000\u0000sT\u0001\u0000\u0000\u0000sX\u0001\u0000\u0000\u0000"+
		"s\\\u0001\u0000\u0000\u0000sb\u0001\u0000\u0000\u0000tw\u0001\u0000\u0000"+
		"\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\u0001\u0001"+
		"\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000xy\u0003\u0012\t\u0000yz\u0003"+
		"\u001e\u000f\u0000z|\u0001\u0000\u0000\u0000{x\u0001\u0000\u0000\u0000"+
		"|\u007f\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000\u0000}~\u0001\u0000"+
		"\u0000\u0000~\u0080\u0001\u0000\u0000\u0000\u007f}\u0001\u0000\u0000\u0000"+
		"\u0080\u0081\u0003\f\u0006\u0000\u0081\u0003\u0001\u0000\u0000\u0000\u0082"+
		"\u0083\u0007\u0000\u0000\u0000\u0083\u0005\u0001\u0000\u0000\u0000\u0084"+
		"\u0085\u0005\t\u0000\u0000\u0085\u0007\u0001\u0000\u0000\u0000\u0086\u0087"+
		"\u0007\u0001\u0000\u0000\u0087\t\u0001\u0000\u0000\u0000\u0088\u0089\u0007"+
		"\u0002\u0000\u0000\u0089\u000b\u0001\u0000\u0000\u0000\u008a\u008b\u0005"+
		"\u001a\u0000\u0000\u008b\r\u0001\u0000\u0000\u0000\u008c\u008d\u0005\u001a"+
		"\u0000\u0000\u008d\u000f\u0001\u0000\u0000\u0000\u008e\u008f\u0005\u001a"+
		"\u0000\u0000\u008f\u0011\u0001\u0000\u0000\u0000\u0090\u0091\u0005\u001a"+
		"\u0000\u0000\u0091\u0013\u0001\u0000\u0000\u0000\u0092\u0093\u0005\u001c"+
		"\u0000\u0000\u0093\u0015\u0001\u0000\u0000\u0000\u0094\u0096\u0005\u001b"+
		"\u0000\u0000\u0095\u0097\u0005\u001d\u0000\u0000\u0096\u0095\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0017\u0001\u0000"+
		"\u0000\u0000\u0098\u0099\u0005\u0019\u0000\u0000\u0099\u0019\u0001\u0000"+
		"\u0000\u0000\u009a\u009b\u0005\u0004\u0000\u0000\u009b\u001b\u0001\u0000"+
		"\u0000\u0000\u009c\u009d\u0005\u0005\u0000\u0000\u009d\u001d\u0001\u0000"+
		"\u0000\u0000\u009e\u009f\u0005\u0006\u0000\u0000\u009f\u001f\u0001\u0000"+
		"\u0000\u0000\u00a0\u00a1\u0005\u0003\u0000\u0000\u00a1!\u0001\u0000\u0000"+
		"\u0000\u00a2\u00a3\u0005\u0006\u0000\u0000\u00a3#\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a5\u0005\u0018\u0000\u0000\u00a5%\u0001\u0000\u0000\u0000\u00a6"+
		"\u00a7\u0005\u000f\u0000\u0000\u00a7\'\u0001\u0000\u0000\u0000\u00a8\u00a9"+
		"\u0005\r\u0000\u0000\u00a9)\u0001\u0000\u0000\u0000\u00aa\u00ab\u0005"+
		"\u000e\u0000\u0000\u00ab+\u0001\u0000\u0000\u0000\u00ac\u00ad\u0007\u0003"+
		"\u0000\u0000\u00ad-\u0001\u0000\u0000\u0000\tGJNlosu}\u0096";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
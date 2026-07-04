package plm.dynamic.engine.exprs;

import com.flame.util.XException;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import plm.dynamic.engine.cvm.Emulator;
import plm.dynamic.engine.exprs.g4.RuleExpressionLexer;
import plm.dynamic.engine.exprs.g4.RuleExpressionParser;
import plm.dynamic.engine.exprs.g4.RuleExpressionParser.RuleExprContext;
import plm.dynamic.engine.exprs.g4.XWorxErrorListener;
import plm.dynamic.engine.mdb.CalCharacter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class RuleExpressionUtility implements Grammar {
    private String sentence;
    private String expression;
    private Map<String, String> paramMap;
    private Map<String, CalCharacter> optionMap;

    public String getSentence() {
        return this.sentence;
    }

    public String getExpression() {
        return this.expression;
    }

    public Map<String, String> getParamMap() {
        return this.paramMap;
    }

    public Map<String, CalCharacter> getOptionMap() {
        return this.optionMap;
    }

    public static RuleExpressionUtility parserExpressionRule(Emulator emulator, String expression) {
        CharStream charStream = CharStreams.fromString(expression);//new ANTLRInputStream(expression);
        XWorxErrorListener errorListener = new XWorxErrorListener();
        RuleExpressionLexer lexer = new RuleExpressionLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RuleExpressionParser parser = new RuleExpressionParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.setBuildParseTree(true);

        RuleExprContext tree = parser.ruleExpr();
        Set<String> errorSet = errorListener.getErrorSet();
        if (!errorSet.isEmpty()) {
            throw new XException("表达式:“" + expression + "”下面位置有错:\n" + errorSet.toString());
        }
        if (tree.exception != null) {
            throw tree.exception;
        }

        org.antlr.v4.gui.Trees.inspect(tree, parser);
        //((RuleExpressionParser.RuleExprContext) tree).inspect(parser);
        RuleExpressionAnalyzeListener listener = new RuleExpressionAnalyzeListener(emulator);
        ParseTreeWalker.DEFAULT.walk(listener, tree);

        RuleExpressionUtility utility = new RuleExpressionUtility();

        utility.expression = listener.getExpression();
        utility.sentence = listener.getSentence();
        utility.paramMap = listener.getParamMap();
        utility.optionMap = listener.getOptionMap();

        return utility;
    }

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        //LBG0001_1.INVERTER_MODEL == (INVERTER_ASSIGNMENT.hasRow (0) ? "SSS" : INVERTER_ASSIGNMENT.getCell (0, "type").toString ())
        //RuleExpressionUtility utility = RuleExpressionUtility.parserExpressionRule(null, "Math.abs(CustomerID) == 11 && CustomerID.equals (\"A\") && (RateOfWork == 8.3 || RateOfWork == 22) || !CustomerID.equals (\"A\")");
        RuleExpressionUtility utility = RuleExpressionUtility.parserExpressionRule(null, "BRAKING_UNIT_COMPONENT.equals(10000L) && OTHER_INTERFACE.equals(\"PROFINET\")");
        System.out.println(utility.getSentence());
        System.out.println(utility.getExpression());
        System.out.println(utility.getOptionMap());

    }
}

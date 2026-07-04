package plm.dynamic.engine.exprs.g4;

import java.util.LinkedHashSet;
import java.util.Set;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class XWorxErrorListener extends BaseErrorListener {
	private Set<String> errorSet = new LinkedHashSet<String>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		errorSet.add("line " + line + ":" + charPositionInLine + ": " + msg);
	}

	public Set<String> getErrorSet() {
		return this.errorSet;
	}
}
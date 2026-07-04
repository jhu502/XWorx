package plm.dynamic.engine.cvm;

import java.util.Map;
import java.util.Set;

import plm.dynamic.engine.exprs.Expression;
import plm.dynamic.engine.mdb.CalAssign;
import plm.dynamic.engine.mdb.CalParam;
import plm.dynamic.engine.mdb.CalParam.Source;

/**
 * 
 * @author hujin
 * @version 1.1
 * 
 */
public interface Simulator {
	public enum Status {
		INWORK("InWork"), COMPLETED("Completed");

		private String value;

		Status(String value) {
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	public String getUUID();
	
	public Status getStatus();

	public Emulator getEmulator();

	public Emulator getEmulator(String uuid);

	public void setEmulator(Emulator emulator);

	public Expression getExpression();

	public Set<String> getEnabledEmulator();

	public Map<String, CalParam> getCalParameters();

	public CalParam getCalParameter(String uuid);

	public void setInputOption(String uuid, CalAssign value, Source source);

	public void pushInputOption(String uuid, Object value, Source source);

	public void popupInputOption(String uuid, Object value, Source source);

	public void pushConfigOption(String uuid, Object value);

	public void pushCascadeOption(String name, Object value);

	public void pushGlobalOption(String name, Object value);

	public void executeOptionAnalysis(CalParam parameter);
}

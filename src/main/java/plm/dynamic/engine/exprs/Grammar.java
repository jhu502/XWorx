package plm.dynamic.engine.exprs;

import java.util.Map;

import plm.dynamic.engine.mdb.CalCharacter;

public interface Grammar {
	public String getExpression();
	
	public Map<String, CalCharacter> getOptionMap();
}

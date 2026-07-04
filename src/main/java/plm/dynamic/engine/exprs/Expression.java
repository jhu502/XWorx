package plm.dynamic.engine.exprs;

import java.util.Map;

import plm.dynamic.engine.cvm.Simulator;

public interface Expression extends java.io.Serializable{
	public void setCharValues(Map<String, Object> values);
	
	public void setSimulator(Simulator simulator);
	
	public Object executeMethod(String name, Class<?>[] cls, Object[] objs);
}

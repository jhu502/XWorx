package plm.dynamic.engine.exprs;

import java.util.Map;

import javassist.ClassPool;

public interface Generator {
	public void setName(String name);

	public void execute(ClassPool clsPool, Map<String, Class<Expression>>  duplication);

	public void setException(Exception e);
	
	public Exception getException();
}

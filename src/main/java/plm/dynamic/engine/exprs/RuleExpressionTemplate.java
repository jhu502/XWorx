package plm.dynamic.engine.exprs;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plm.dynamic.engine.cvm.Simulator;
import com.flame.util.XException;

public class RuleExpressionTemplate extends ExpressionTemplate {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(RuleExpressionTemplate.class);

	public boolean execExpression() {
		return true;
	}

	public long calcExpression() {
		return 0L;
	}

	@Override
	public void setSimulator(Simulator simulator) {
	}

	@Override
	public void setCharValues(Map<String, Object> values) {
		if (values == null)
			return;
		try {
			/**
			 * 对所有的Field的值进行初始化
			 */
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field != null) {
					if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {// Exclude static member variable
						if (field.getType().equals(String.class)) {
							field.set(this, "");
						} else if (field.getType().equals(long.class)) {
							field.set(this, 0L);
						} else if (field.getType().equals(int.class)) {
							field.set(this, 0);
						} else if (field.getType().equals(double.class)) {
							field.set(this, 0);
						} else if (field.getType().equals(boolean.class)) {
							field.set(this, false);
						}
					}
				}
			}

			for (Field field : fields) {
				if (field != null) {
					Object val = field.get(this);
					if (val == null) {
						logger.debug("Parameter:" + field.getName() + " wasn't assigned value.");
					}
				}
			}
		} catch (Exception e) {
			XException.throwException(e);
		}
	}

	@Override
	public Object executeMethod(String mname, Class<?>[] cls, Object[] objs) {
		try {
			Method method = this.getClass().getMethod(mname, cls);
			try {
				return method.invoke(this, objs);
			} catch (InvocationTargetException e) {
				Throwable throwable = e.getTargetException();
				if (throwable instanceof NullPointerException) {
					Type result = method.getGenericReturnType();
					if (result.equals(boolean.class)) {
						return false;
					} else if (result.equals(long.class)) {
						return 0;
					}
				} else {
					throw e;
				}
			}
		} catch (Exception e) {
			XException.throwException(e);
		}
		return false;
	}
}

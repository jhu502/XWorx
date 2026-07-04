package plm.dynamic.engine.exprs;

public abstract class ExpressionTemplate implements Expression {
	private static final long serialVersionUID = 1L;

	public static Double toDouble(Object d) {
		if (d == null) {
			return null;
		}

		if (d instanceof Double) {
			return toDouble((Double) d);
		} else {
			return Double.parseDouble(d.toString());
		}
	}

	public static Double toDouble(Double d) {
		return d;
	}

	public static Double toDouble(double d) {
		return Double.valueOf(d);
	}

	public static Double toDouble(Long l) {
		return l.doubleValue();
	}

	public static Double toDouble(long l) {
		return Long.valueOf(l).doubleValue();
	}

	public static Double toDouble(Float d) {
		return Double.valueOf(d);
	}

	public static Double toDouble(float d) {
		return Double.valueOf(d);
	}

	public static Double toDouble(Integer i) {
		return Double.valueOf(i);
	}

	public static Double toDouble(int i) {
		return Double.valueOf(i);
	}

	public static Long toLong(Object l) {
		if (l == null) {
			return null;
		}
		if (l instanceof Long) {
			return toLong((Long) l);
		} else {
			return Long.parseLong(l.toString());
		}
	}

	public static Long toLong(Double d) {
		return d.longValue();
	}

	public static Long toLong(double d) {
		return Double.valueOf(d).longValue();
	}

	public static Long toLong(Long l) {
		return l;
	}

	public static Long toLong(long l) {
		return Long.valueOf(l);
	}

	public static Long toLong(Integer i) {
		return Long.valueOf(i);
	}

	public static Long toLong(int i) {
		return Long.valueOf(i);
	}

	public static Boolean toBoolean(Object b) {
		if (b == null) {
			return null;
		}
		if (b instanceof Boolean) {
			return (Boolean) b;
		} else {
			return Boolean.parseBoolean(b.toString());
		}
	}

	public static Boolean toBoolean(boolean b) {
		return Boolean.valueOf(b);
	}

	public static void main(String[] args) {
		long x = Double.valueOf(Math.random() * 100).longValue();
		System.out.println(x);
	}
}

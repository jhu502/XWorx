package plm.dynamic.engine.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.flame.util.XException;

public class DriverTable extends ExtendType {
	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private List<String> input = new ArrayList<String>();
	private List<String> output = new ArrayList<String>();
	private List<Rule> rows = new ArrayList<Rule>();
	private String importFlag;

	public static class Rule {
		private String sn;
		private String condition;
		private List<Driver> output = new ArrayList<Driver>();

		public String getSn() {
			return sn;
		}

		public void setSn(String sn) {
			this.sn = sn;
		}

		public String getCondition() {
			return condition;
		}

		public void setCondition(String condition) {
			this.condition = condition;
		}

		public List<Driver> getOutput() {
			return output;
		}

		public void setOutput(List<Driver> output) {
			this.output = output;
		}
	}

	public static class Driver {
		String parameter;
		String expression;
		String type;

		public String getParameter() {
			return parameter;
		}

		public void setParameter(String parameter) {
			this.parameter = parameter;
		}

		public String getExpression() {
			return expression;
		}

		public void setExpression(String expression) {
			this.expression = expression;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static DriverTable valueOf(String content) {
		try {
			return mapper.readValue(content, DriverTable.class);
		} catch (Exception e) {
			throw new XException(e.getMessage(), e);
		}
	}

	public static DriverTable valueOf(InputStream inputStream) {
		try {
			return mapper.readValue(inputStream, DriverTable.class);
		} catch (Exception e) {
			throw new XException(e.getMessage(), e);
		}
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

	public List<String> getOutput() {
		return output;
	}

	public void setOutput(List<String> output) {
		this.output = output;
	}

	public List<Rule> getRows() {
		return rows;
	}

	public void setRows(List<Rule> rows) {
		this.rows = rows;
	}
	
	public String getImportFlag() {
		return this.importFlag;
	}
	
	public void setImportFlag(String flag) {
		this.importFlag = flag;
	}

	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream(new File("D:\\WorkSpace\\Inovance\\docs\\sample\\LBG0001_0.INVERTER_MODEL.json"));
		DriverTable driverTable = mapper.readValue(fis, DriverTable.class);
		System.out.println(driverTable);
	}
}

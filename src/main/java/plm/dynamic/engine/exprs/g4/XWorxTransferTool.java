package plm.dynamic.engine.exprs.g4;

public class XWorxTransferTool {

	public static void main(String[] args) throws InterruptedException {
		StringBuilder absPath = new StringBuilder();
		absPath.append(new java.io.File("").getAbsolutePath()).append("\\src\\main\\java");

		String path = XWorxTransferTool.class.getPackage().getName();
		String[] ps = path.split("\\.");
		int length = ps.length;
		if (length > 0) {
			for (int i = 0; i < length - 1; i++) {
				absPath.append("\\").append(ps[i]);
			}
		}
		
		//org.antlr.v4.Tool.main(new String[] { "-o", absPath.toString() + "\\g4", absPath.toString() + "\\g4\\RuleExpressionLexer.g4" });
		org.antlr.v4.Tool.main(new String[] { "-o", absPath.toString() + "\\g4", absPath.toString() + "\\g4\\RuleExpressionParser.g4" });
	}
}

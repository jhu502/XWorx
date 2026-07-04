package plm.dynamic.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.config.basic.BasicConfiguration;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import jakarta.servlet.http.HttpServletResponse;
import plm.dynamic.CaseType;
import plm.dynamic.XCaseTable;

public class ExportXCaseTableProcessor extends DefaultFormProcessor {
	private static final String SEP = File.separator;

	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		Object[] objs = (Object[]) commandBean.getParameter("rowIds");
		if (objs == null || objs.length == 0)
			return formResult;

		String oid = (String) objs[0];
		XCaseTable caseTable = (XCaseTable) PersistenceHelper.getPersistable(oid);
		CaseType caseType = caseTable.getType();
		String caseTemplate = BasicConfiguration.getXWHome() + SEP + "codebase" + SEP + "templates" + SEP + "plm" + SEP + "dynamic" + SEP;
		if (CaseType.FLATTABLE.equals(caseType)) {
			caseTemplate += "FlatTable_Template.xlsx";
		} else if (CaseType.BLOCKTYPE.equals(caseType)) {
			caseTemplate += "BlockTable_Template.xlsx";
		}
		try (Workbook workbook = new XSSFWorkbook(new FileInputStream(caseTemplate));) {
			HttpServletResponse response = commandBean.getResponse();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(caseTable.getNumber(), "UTF-8") + ".xlsx");
			response.setContentType("application/octet-stream");
			response.flushBuffer();
			OutputStream outStream = response.getOutputStream();
			workbook.write(outStream);
			outStream.flush();
		} catch (IOException e) {
			throw new XException(e);
		}

		return formResult;
	}

}

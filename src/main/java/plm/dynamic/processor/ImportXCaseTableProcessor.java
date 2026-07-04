package plm.dynamic.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

import jakarta.servlet.http.HttpServletRequest;
import plm.dynamic.XCaseTable;
import plm.dynamic.bean.XCaseHead;
import plm.part.XPart;
import plm.util.MSOfficeUtil;

public class ImportXCaseTableProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		String _number = commandBean.getTextParameter("number");
		String _name = commandBean.getTextParameter("name");
		String _description = commandBean.getTextParameter("description");

		XPart context = (XPart) commandBean.getPrimaryObj();

		XCaseTable caseTable = XCaseTable.newXCaseTable(context);
		caseTable.setNumber(_number);
		caseTable.setName(_name);
		caseTable.setDescription(_description);

		HttpServletRequest request = commandBean.getRequest();
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			//获取上传上来的文件
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
			for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
				MultipartFile file = entry.getValue();
				try (Workbook workbook = new XSSFWorkbook(file.getInputStream());) {
					Sheet sheet = null;
					for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
						Sheet _sheet = workbook.getSheetAt(i);
						String sheetType = MSOfficeUtil.getStringFromSheet(_sheet, 0, 0);
						if ("ImportSheetType=FlatTable".equals(sheetType)) {
							sheet = _sheet;
							break;
						}
					}

					if (sheet == null)
						throw new XException("CaseTable模板不正确!");

					XCaseHead caseHead = caseTable.getHead();
					Row headRow = sheet.getRow(6);
					int _colNum = headRow.getPhysicalNumberOfCells();
					for (int col = 0; col < _colNum; col++) {
						String value = MSOfficeUtil.getStringFromRow(headRow, col);
						if (FlameUtils.isBlank(value))
							break;

						caseHead.addColumn(value);
					}
					int colNum = caseHead.length();
					int _rowNum = sheet.getLastRowNum();
					for (int j = 7; j < _rowNum; j++) {
						Row row = sheet.getRow(j);
						if ("#EOF".equals(MSOfficeUtil.getStringFromRow(row, 0)))
							break;

						List<Object> caseRow = new ArrayList<>();
						boolean bool = false;
						for (int i = 0; i < colNum; i++) {
							String value = MSOfficeUtil.getStringFromRow(row, i);
							if (!FlameUtils.isBlank(value))
								bool = true;

							caseRow.add(value);
						}
						if (bool)
							caseTable.addCaseRow(caseRow);
					}
				} catch (IOException e) {
					throw new XException(e);
				}
			}
		}
		caseTable = PersistenceHelper.service().save(caseTable);
		formResult.setData(caseTable);

		return formResult;
	}

}

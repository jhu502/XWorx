package plm.dynamic.form;

import com.flame.annotations.*;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.xui.WidgetType;

@UIMeshGrid(grids = {
		@UIGrid(rows = { //
				@UIRow(cells = { //
						@UICell(label = "CaseTable导入", widget = {}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*编号：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "casetableNumber", name = "number", traits = "required:true", style = "width:250px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*名称：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "casetableName", name = "name", traits = "editable:true,required:true", style = "width:350px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "描述：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "casetableDescription", name = "description", traits = "editable:true,multiline:true", style = "width:350px;height:60px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "*上传文件：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.FileBox, id = "casetableUpload", name = "uploadFile", traits = "prompt:'Choose casetable xlsx...',editable:false,required:true", style = "width:400px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "模板下载：", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.HyperLink, id = "casetableTemplate", name = "templateHref", url = "templates/plm/part/XCaseTable_Template.xlsx", text = "CaseTable Import Template")//
						}), //
				}), //
		})
})
public class CaseTableImportForm extends AbstractMeshComponentBuilder {
}

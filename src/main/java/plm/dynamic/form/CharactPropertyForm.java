package plm.dynamic.form;

import com.flame.annotations.*;
import com.flame.xui.XCommandBean;
import com.flame.xui.widget.ComboBox;
import com.flame.xui.widget.RadioBox;
import com.flame.xui.widget.TextBox;
import com.flame.xui.WidgetType;
import com.flame.xui.XUIMeshGrid;
import com.flame.xui.builder.AbstractMeshComponentBuilder;
import com.flame.orm.XObject;
import com.flame.type.XBaseType;
import plm.dynamic.InputType;
import plm.dynamic.OptionMode;
import plm.dynamic.XCharacteristic;

@UIMeshGrid(grids = {
		@UIGrid(rows = { //
				@UIRow(cells = { //
						@UICell(widget = { //
								@UIWidget(type = WidgetType.Hidden, id = "characterOid", name = "oid"), //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_number", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "characterNumber", name = "number", traits = "required:true", style = "width:250px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_name", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "characterName", name = "name", traits = "editable:true,required:true", style = "width:350px;height:24px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_description", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "characterDescription", name = "description", traits = "editable:true,multiline:true", style = "width:500px;height:60px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_characterType", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.ComboBox, id = "characterBaseType", name = "baseType", traits = "panelHeight:'auto',editable:false,required:true", style = "width:200px;height:25px", events = {@UIEvent(name = "onchange", value = "showChoiceDefPanel();")})//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_characterInputType", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.RadioBox, id = "characterInputType", name = "inputType", style = "margin-left:8px")//
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_characterOptionMode", required = true, style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.RadioBox, id = "characterOptionMode", name = "optionMode", style = "margin-left:8px", events = {@UIEvent(name = "onclick", value = "showChoiceDefPanel(this);")}) //
						}), //
				}), //
				@UIRow(cells = { //
						@UICell(label = "label_characterFieldMapping", style = "width:80px", widget = { //
								@UIWidget(type = WidgetType.TextBox, id = "characterFieldMapping", name = "fieldMapping", traits = "editable:true", style = "width:200px;height:24px"), //
								@UIWidget(type = WidgetType.LinkButton, id = "btnFieldMapping", style = "width:30px;height:24px;") //
						}), //
				}), //
		})
})
public class CharactPropertyForm extends AbstractMeshComponentBuilder {
	@Override
	public XUIMeshGrid buildComponentConfig(XCommandBean commandBean) {
		XUIMeshGrid formConfig = super.buildComponentConfig(commandBean);
		XObject primary = commandBean.getPrimaryObj();

		if (primary instanceof XCharacteristic) {
			this.editForm((XCharacteristic) primary, formConfig);
		} else {
			this.createForm(formConfig);
		}

		return formConfig;
	}

	private void editForm(XCharacteristic charact, XUIMeshGrid formConfig) {
		TextBox numberBox = (TextBox) formConfig.getXUIWidget("characterNumber");
		numberBox.addTrait("editable", false);
		numberBox.setValue(charact.getNumber());
		TextBox nameBox = (TextBox) formConfig.getXUIWidget("characterName");
		nameBox.setValue(charact.getName());
		TextBox descriptionBox = (TextBox) formConfig.getXUIWidget("characterDescription");
		descriptionBox.setValue(charact.getDescription());
		ComboBox baseTypeBox = (ComboBox) formConfig.getXUIWidget("characterBaseType");
		baseTypeBox.setValue(charact.getBaseType().getName());
		baseTypeBox.addTrait("disabled", true);
		for (XBaseType baseType : XBaseType.characterTypes()) {
			baseTypeBox.addOption(baseType.getName(), baseType.getDisplay());
		}
		RadioBox inputTypeBox = (RadioBox) formConfig.getXUIWidget("characterInputType");
		inputTypeBox.setValue(charact.getInputType().getName());
		for (InputType inputType : InputType.values()) {
			inputTypeBox.addRadio(inputType.getName(), inputType.getDisplay());
		}
		RadioBox optionModeBox = (RadioBox) formConfig.getXUIWidget("characterOptionMode");
		optionModeBox.setValue(charact.getOptionMode().getName());
		for (OptionMode optionMode : OptionMode.values()) {
			optionModeBox.addRadio(optionMode.getName(), optionMode.getDisplay());
		}
		TextBox fieldMappingBox = (TextBox) formConfig.getXUIWidget("characterFieldMapping");
		if (!isBlank(charact.getFieldMapping())) {
			fieldMappingBox.setValue(charact.getFieldMapping());
		}
	}

	private void createForm(XUIMeshGrid formConfig) {
		ComboBox baseTypeBox = (ComboBox) formConfig.getXUIWidget("characterBaseType");
		for (XBaseType baseType : XBaseType.characterTypes()) {
			baseTypeBox.addOption(baseType.getName(), baseType.getDisplay());
		}
		RadioBox inputTypeBox = (RadioBox) formConfig.getXUIWidget("characterInputType");
		for (InputType inputType : InputType.values()) {
			inputTypeBox.addRadio(inputType.getName(), inputType.getDisplay());
		}
		RadioBox optionModeBox = (RadioBox) formConfig.getXUIWidget("characterOptionMode");
		optionModeBox.addTrait("editable", false);
		for (OptionMode optionMode : OptionMode.values()) {
			optionModeBox.addRadio(optionMode.getName(), optionMode.getDisplay());
		}
	}

}

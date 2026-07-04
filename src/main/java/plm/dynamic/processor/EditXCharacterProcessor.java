package plm.dynamic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import plm.dynamic.InputType;
import plm.dynamic.OptionMode;
import plm.dynamic.XCharacteristic;
import plm.dynamic.bean.XChoice;
import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.JsonUtils;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

public class EditXCharacterProcessor extends DefaultFormProcessor {

	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String _name = commandBean.getTextParameter("name");
		String _description = commandBean.getTextParameter("description");
		String _inputType = commandBean.getTextParameter("inputType");
		String _optionMode = commandBean.getTextParameter("optionMode");
		String _charactChoice = commandBean.getTextParameter("charactChoiceJSON");
		String _fieldMapping = commandBean.getTextParameter("fieldMapping");
		String _rangeChoices = commandBean.getTextParameter("rangeChoices");
		String _dynamicMapping = commandBean.getTextParameter("dynamicMapping");
		_fieldMapping = _fieldMapping == null ? "" : _fieldMapping;

		XUIRowId uiRowId = commandBean.getRowId();
		XObject xobject = (XObject) PersistenceHelper.getPersistable(uiRowId.getObjectId());
		if (xobject instanceof XCharacteristic) {
			XCharacteristic xcharact = (XCharacteristic) xobject;
			xcharact.setName(_name);
			xcharact.setDescription(_description);
			InputType inputType = InputType.valueOf(_inputType);
			xcharact.setInputType(inputType);
			OptionMode optionMode = OptionMode.valueOf(_optionMode);
			xcharact.setOptionMode(optionMode);
			xcharact.getChoices().clear();
			if (OptionMode.NONE.equals(optionMode)) {
				xcharact.getChoices().clear();
			} else if (OptionMode.RANGE.equals(optionMode)) {
				if (_rangeChoices == null || FlameUtils.isBlank(_rangeChoices))
					throw new XException("请填写值范围.");
				
				xcharact.getChoices().clear();
				XChoice choice = XChoice.newXChoice(_rangeChoices, "Range");
				choice.setType('R');
				xcharact.addChoice(choice);
			} else if (OptionMode.LIST.equals(optionMode)) {
				if (FlameUtils.isBlank(_charactChoice))
					throw new XException("请添加参数可选值.");

				xcharact.getChoices().clear();
				JsonNode jsonNode = JsonUtils.convertJsonNode(_charactChoice);
				if (jsonNode instanceof ArrayNode) {
					ArrayNode arrayNode = (ArrayNode) jsonNode;
					for (JsonNode node : arrayNode) {
						XChoice choice = JsonUtils.convertT(node, XChoice.class);
						if (choice != null && !FlameUtils.isBlank(choice.getValue())) {
							choice.setType('V');
							xcharact.addChoice(choice);
						}
					}
				}
			} else if (OptionMode.DLIST.equals(optionMode)) {
				if (FlameUtils.isBlank(_charactChoice))
					throw new XException("请添加属性映射.");

				xcharact.setDynamicMapping(_dynamicMapping);
				xcharact.getChoices().clear();
				JsonNode jsonNode = JsonUtils.convertJsonNode(_charactChoice);
				if (jsonNode instanceof ArrayNode) {
					ArrayNode arrayNode = (ArrayNode) jsonNode;
					for (JsonNode node : arrayNode) {
						XChoice choice = JsonUtils.convertT(node, XChoice.class);
						if (choice != null && !FlameUtils.isBlank(choice.getValue())) {
							choice.setType('D');
							xcharact.addChoice(choice);
						}
					}
				}
			} else {
			}

			xcharact = PersistenceHelper.service().save(xcharact);
			formResult.setData(xcharact);
		}

		return formResult;
	}
}

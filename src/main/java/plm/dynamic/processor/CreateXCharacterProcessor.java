package plm.dynamic.processor;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import plm.dynamic.InputType;
import plm.dynamic.OptionMode;
import plm.dynamic.XCharacteristic;
import plm.dynamic.bean.XChoice;
import plm.dynamic.service.DynamicServiceHelper;
import plm.part.XPart;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.type.XBaseType;
import com.flame.util.JsonUtils;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

public class CreateXCharacterProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String _number = commandBean.getTextParameter("number");
		String _name = commandBean.getTextParameter("name");
		String _description = commandBean.getTextParameter("description");
		String _baseType = commandBean.getTextParameter("baseType");
		String _inputType = commandBean.getTextParameter("inputType");
		String _optionMode = commandBean.getTextParameter("optionMode");
		String _charactChoice = commandBean.getTextParameter("charactChoiceJSON");
		String _fieldMapping = commandBean.getTextParameter("fieldMapping");
		String _rangeChoices = commandBean.getTextParameter("rangeChoices");//dynamicMapping
		String _dynamicMapping = commandBean.getTextParameter("dynamicMapping");
		_fieldMapping = _fieldMapping == null ? "" : _fieldMapping;
		XObject context = commandBean.getPrimaryObj();

		if (context instanceof XPart) {
			XPart part = (XPart) context;
			List<XCharacteristic> charList = DynamicServiceHelper.repository().getRelatedCharacteristic(part, _name);
			if (charList != null && !charList.isEmpty()) {
				throw new XException("特征：" + _name + " 已经存在！");
			}
			XCharacteristic xcharact = XCharacteristic.newCharacteristic(part);
			xcharact.setNumber(_number.toUpperCase());
			xcharact.setName(_name);
			xcharact.setDescription(_description);
			XBaseType baseType = XBaseType.valueOf(_baseType);
			xcharact.setBaseType(baseType);
			InputType inputType = InputType.valueOf(_inputType);
			xcharact.setInputType(inputType);
			OptionMode optionMode = OptionMode.valueOf(_optionMode);
			xcharact.setOptionMode(optionMode);
			xcharact.setFieldMapping(_fieldMapping);
			if (OptionMode.NONE.equals(optionMode)) {
				xcharact.getChoices().clear();
			} else if (OptionMode.RANGE.equals(optionMode)) {
				if (FlameUtils.isBlank(_charactChoice))
					throw new XException("请添加参数可选值.");

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
				if (FlameUtils.isBlank(_dynamicMapping))
					throw new XException("请指定映射属性.");

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

			xcharact.setSortNo(DynamicServiceHelper.getNextCharactSortNo(part));
			xcharact = PersistenceHelper.service().save(xcharact);
			formResult.setData(xcharact);
		}

		return formResult;
	}
}

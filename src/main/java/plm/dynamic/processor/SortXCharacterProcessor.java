package plm.dynamic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import plm.dynamic.XCharacteristic;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.util.JsonUtils;

public class SortXCharacterProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		String sortOids = commandBean.getTextParameter("charactSortOids");

		JsonNode jsonNode = JsonUtils.convertJsonNode(sortOids);
		if (jsonNode instanceof ArrayNode) {
			int sortNo = 0;
			ArrayNode arrayNode = (ArrayNode) jsonNode;
			for (JsonNode node : arrayNode) {
				String oid = node.asText();
				XCharacteristic xcharact = (XCharacteristic) PersistenceHelper.getPersistable(oid);
				if (xcharact != null) {
					if (xcharact.getSortNo() != sortNo) {
						xcharact.setSortNo(sortNo);
						PersistenceHelper.service().save(xcharact);
					}
				}
				sortNo++;
			}
		}
		
		return formResult;
	}

}

package mes.equipt.processor;

import mes.equipt.XEquiptInstance;
import mes.equipt.XEquiptParameter;
import com.thing.worx.ThingworxHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.XException;

import java.util.List;

public class DeleteEquiptParameterProcessor extends DefaultFormProcessor {

	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		XObject primary = commandBean.getPrimaryObj();
		List<Object> rowObjs = commandBean.getRowObjects();
		if (rowObjs.isEmpty())
			throw new XException("请选择数据行!");
		for (Object rowObject : rowObjs) {

			if (primary instanceof XEquiptInstance && rowObject instanceof XEquiptParameter) {
				XEquiptInstance xInstance = (XEquiptInstance) primary;
				XEquiptParameter xParameter = (XEquiptParameter) rowObject;
				String thingName = xInstance.getNumber();
				String propertyName = xParameter.getNumber();
				PersistenceHelper.service().remove(xParameter);

				ThingworxHelper.thingworx().delPropertyDefinition(thingName, propertyName);
				formResult.setMessage("参数：" + xParameter.getNumber() + "(" + xParameter.getName() + ")成功删除，并同步Thingworx成功.");
			}
		}

		return formResult;
	}
}

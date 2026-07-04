package mes.equipt.processor;

import mes.equipt.XEquiptInstance;
import mes.equipt.XEquiptParameter;
import com.thing.worx.ThingworxHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.XObject;
import org.json.JSONObject;

import java.util.List;

public class SyncEquiptParameterProcessor extends DefaultFormProcessor {

	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		XObject primary = commandBean.getPrimaryObj();
		List<Object> rowObjs = commandBean.getRowObjects();
		for (Object rowObject : rowObjs) {
			if (primary instanceof XEquiptInstance && rowObject instanceof XEquiptParameter) {
				XEquiptInstance xInstance = (XEquiptInstance) primary;
				XEquiptParameter xParameter = (XEquiptParameter) rowObject;
				String thingName = xInstance.getNumber();
				String name = xParameter.getNumber();
				String description = xParameter.getDescription();
				String type = xParameter.getBaseType().name();
				String category = "";
				String dataShape = "";
				Boolean readOnly = false;
				Boolean persistent = true;
				Boolean logged = false;
				Boolean indexed = false;
				String dataChangeType = "VALUE";
				Double dataChangeThreshold = 0d;
				Boolean remote = false;
				String remotePropertyName = "";
				Integer timeout = 0;
				String pushType = "";
				Double pushThreshold = -1d;
				String defaultValue = "";
				JSONObject remoteBindingAspects = new JSONObject();
				ThingworxHelper.thingworx().addPropertyDefinition(thingName, name, description, type, category, dataShape, readOnly, persistent, logged, indexed,
						dataChangeType, dataChangeThreshold, remote, remotePropertyName, timeout, pushType, pushThreshold, defaultValue, remoteBindingAspects);
				formResult.setMessage("参数：" + xParameter.getNumber() + "(" + xParameter.getName() + ")同步Thingworx成功.");
			}
		}

		return formResult;
	}
}

package xw.object.processor;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XPersistable;
import com.flame.vc.IVersioned;
import com.flame.xui.XUIRowId;

public class DeleteObjectProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		XUIRowId[] rowIds = commandBean.getRowIds();
		for (XUIRowId xuiRowId : rowIds) {
			if (xuiRowId.getRowObject() instanceof XPersistable persist) {
				List<XPersistable> removeList = new ArrayList<>();
				if (persist instanceof IVersioned) {
					IVersioned<?> versioned = (IVersioned<?>) persist;
					removeList.add(versioned.getMaster());
				}
				removeList.add(persist);
				PersistenceHelper.service().remove(removeList);
			}
		}
		formResult.setMessage("对象已经被成功删除");

		return formResult;
	}
}

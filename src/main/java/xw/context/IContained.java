package xw.context;

import com.flame.orm.XPersistable;
import xw.context.entity.Container;

public interface IContained<T extends Container> extends XPersistable {
	T getContainer();
	
	void setContainer(T container);
}

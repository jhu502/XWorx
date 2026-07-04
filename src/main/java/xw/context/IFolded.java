package xw.context;

import com.flame.orm.XPersistable;

import xw.context.entity.XFolder;

public interface IFolded extends XPersistable {
	XFolder getFolder();
	
	void setFolder(XFolder folder);
}

package xw.context;

import xw.domain.IAdminDomain;
import xw.team.ITeamManaged;
import xw.team.entity.IRoleHolder;

public interface IContainer<T extends IRoleHolder> extends IAdminDomain, ITeamManaged<T> {
	public String getNumber();
	
	public String getName();
}

package xw.team.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import xw.auths.entity.RoleRB;
import xw.auths.entity.XUser;

@Entity
@Table(name = "XFlowTeam", uniqueConstraints = {})
public class XFlowTeam extends AbstractTeam {
    private static final long serialVersionUID = 1L;

    public XFlowTeam() {
    }

    @Override
    public List<XUser> members() {
        return new ArrayList<>();
    }

    @Override
    public Map<RoleRB, List<XUser>> roleMembers() {
        return new HashMap<>();
    }
}

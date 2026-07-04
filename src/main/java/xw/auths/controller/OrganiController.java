package xw.auths.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.util.JsonUtils;
import com.flame.util.XException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import xw.auths.XGroupHelper;
import xw.auths.entity.RoleRB;
import xw.auths.entity.XGroup;
import xw.auths.entity.XGroupUserLink;
import xw.auths.entity.XUser;
import xw.auths.service.XGroupManager;

@RestController
@RequestMapping(value = "/OrganiController", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganiController {
	@Resource
	private XGroupManager organiService;

	@Operation(summary = "更新组信息", parameters = { @Parameter(name = "group", required = true), @Parameter(name = "oid", required = true) })
	@PostMapping(value = "/updateXGroup", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@Transactional
	public XGroup updateXGroup(XGroup group, String oid) {
		if (oid == null || "".equals(oid.trim())) {
			throw new XException("请选中被更新的组!");
		}
		XGroup fgroup = (XGroup) PersistenceHelper.service().find(oid);
		if (fgroup == null) {
			throw new XException("请选中被更新的组!");
		}
		fgroup.setName(group.getName());
		fgroup.setEnglishName(group.getEnglishName());
		fgroup.setFullName(group.getFullName());
		fgroup.setGroupType(group.getGroupType());
		fgroup.setDescription(group.getDescription());
		fgroup.setFax(group.getFax());
		fgroup.setTel(group.getTel());
		fgroup.setEmail(group.getEmail());
		fgroup.setAddress(group.getAddress());
		fgroup.setPostalCode(group.getPostalCode());
		return PersistenceHelper.service().save(fgroup);
	}

	@Operation(summary = "添加用户", parameters = { @Parameter(name = "pid", required = true), @Parameter(name = "oid", required = true), @Parameter(name = "role", required = true) })
	@PostMapping(value = "/addUser", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@Transactional
	public XGroupUserLink addUser(String pid, String oid, String role) {
		if (pid == null || "".equals(pid.trim())) {
			throw new XException("请选中组节点!");
		}
		XGroup parent = (XGroup) PersistenceHelper.service().find(pid);
		if (parent == null) {
			throw new XException("请选中组节点!");
		}
		XUser child = PersistenceHelper.service().find(oid);
		XGroupUserLink gUserLink = XGroupUserLink.newGroupUserLink(parent, child);
		gUserLink.setRole(RoleRB.toRoleRB(role));

		return PersistenceHelper.service().save(gUserLink);
	}

	@Operation(summary = "搜索用户", parameters = { @Parameter(name = "name", required = true) })
	@RequestMapping(value = "/searchUserByName", method = RequestMethod.GET)
	public List<?> searchUserByName(String name) {
		if (name == null || "".equals(name.trim())) {
			throw new XException("请输入用户名");
		}

		return XGroupHelper.repository().findUserFuzzy(name);
	}

	@Operation(summary = "修改用户信息", parameters = { @Parameter(name = "user", required = true), @Parameter(name = "oid", required = true) })
	@PostMapping(value = "/modifyXUser", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@Transactional
	public XUser modifyXUser(XUser user, String oid) {
		if (oid == null || "".equals(oid.trim())) {
			throw new XException("请选中被更新的组!");
		}
		XUser xUser = PersistenceHelper.service().find(oid);
		if (xUser == null) {
			throw new XException("请选中被更新的组!");
		}
		xUser.setName(user.getName());
		xUser.setEnglishName(user.getEnglishName());
		xUser.setFullName(user.getFullName());
		xUser.setDescription(user.getDescription());
		xUser.setFax(user.getFax());
		xUser.setTel(user.getTel());
		xUser.setEmail(user.getEmail());
		xUser.setEnglishName(user.getEnglishName());
		xUser.setAddress(user.getAddress());
		return PersistenceHelper.service().save(xUser);
	}

	@Operation(summary = "查询下层组", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/listSubGroups", method = RequestMethod.GET)
	public Set<XGroup> listSubGroups(@RequestParam("oid") String oid) {
		Set<XGroup> result = new HashSet<XGroup>();
		if (oid == null || "".equals(oid.trim())) {
			return result;
		}
		List<?> list = XGroupHelper.repository().getGroupMember(ObjectReference.newObjectReference(oid));
		for (Object obj : list) {
			if (obj instanceof XGroup) {
				XGroup group = (XGroup) obj;
				result.add(group);
			}
		}
		return result;
	}

	@Operation(summary = "查询下层", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/listSubPrincipal", method = RequestMethod.GET)
	public List<?> listSubPrincipal(@RequestParam("oid") String oid) {
		return organiService.getMember(oid);
	}

	@Operation(summary = "查询组的用户", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/listChildUser", method = RequestMethod.GET)
	public Set<XUser> listChildUser(String oid) {
		Set<XUser> result = new HashSet<>();
		if (oid == null || "".equals(oid.trim())) {
			return result;
		}
		ObjectReference<XGroup> objRef = new ObjectReference<XGroup>(oid);
		List<?> list = PersistenceHelper.service().query("select a from XUser a, GroupUserLink b where a.id = b.right.id and b.left.id = :lid", new Object[][] { { "lid", objRef.getId() } });
		for (Object obj : list) {
			if (obj instanceof XUser) {
				XUser user = (XUser) obj;
				result.add(user);
			}
		}
		return result;
	}

	@Operation(summary = "查询父组", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/listParentGroup", method = RequestMethod.GET)
	public Set<XGroup> listParentGroup(String oid) {
		return organiService.listParentGroup(oid);
	}

	@Operation(summary = "删除Principal", parameters = { @Parameter(name = "pid", required = true), @Parameter(name = "oids", required = true) })
	@RequestMapping(value = "/deletePrincipals", method = RequestMethod.POST)
	public String deletePrincipals(String pid, String oids) {
		if (oids == null) {
			throw new XException("Parameter is null!");
		}
		List<?> list = JsonUtils.parseAsList(oids);

		organiService.removeGroup(pid, list);

		return "数据删除成功";
	}

	@Operation(summary = "查询用户", parameters = { @Parameter(name = "oid", required = true) })
	@RequestMapping(value = "/childrenCUser", method = RequestMethod.GET)
	public Set<Map<String, String>> childrenCUser(@RequestParam("oid") String oid) {
		Set<Map<String, String>> result = new HashSet<>();
		ObjectReference<XGroup> objRef = new ObjectReference<>(oid);
		List<?> list = PersistenceHelper.service().query("select a from XUser a, GroupUserLink b where a.ida2a2 = b.idb1 and b.ida1 = :ida1", new Object[][] { { "ida1", objRef.getId() } });
		for (Object obj : list) {
			if (obj instanceof XUser) {
				XUser user = (XUser) obj;
				Map<String, String> rowData = new HashMap<String, String>();
				rowData.put("oid", user.getOid());
				rowData.put("icon", user.getIcon());
				rowData.put("name", "<img src='" + user.getIcon() + "'/> " + user.getName());
				rowData.put("state", "closed");
				result.add(rowData);
			}
		}
		return result;
	}
}

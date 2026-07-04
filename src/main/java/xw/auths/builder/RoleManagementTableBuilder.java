package xw.auths.builder;

import com.flame.xui.XCommandBean;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UIDataGrid;
import com.flame.xui.builder.AbstractComponentBuilder;
import com.flame.xui.service.TableComponentRow;
import org.apache.commons.lang3.StringUtils;
import xw.auths.entity.RoleRB;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理表格构建器。
 *
 * <p>用于构建角色列表表格，显示所有已注册的角色信息。</p>
 *
 * @author XClaw Team
 */
@UIDataGrid(idField = "name", singleSelect = true, rowNumber = true, fit = true, //
        columns = { //
                @UIColumn(field = "name", width = "150", sortable = true), //
                @UIColumn(field = "display", width = "150", sortable = true), //
                @UIColumn(field = "en_US", width = "120"), //
                @UIColumn(field = "zh_CN", width = "120"), //
                @UIColumn(field = "description", width = "250"), //
                @UIColumn(field = "responsibility", width = "300"), //
                @UIColumn(field = "soul", width = "300"), //
                @UIColumn(field = "createdStamp", width = "130", align = "center", sortable = true), //
                @UIColumn(field = "modifiedStamp", width = "130", align = "center", sortable = true) //
        } //
)
public class RoleManagementTableBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(XCommandBean commandBean) {
        List<TableComponentRow> result = new ArrayList<>();

        String name = commandBean.getTextParameter("name");
        String display = commandBean.getTextParameter("display");

        RoleRB[] roles = RoleRB.getRoleRBSet();

        for (RoleRB role : roles) {
            if (StringUtils.isNotBlank(name) && !role.getName().toLowerCase().contains(name.toLowerCase())) {
                continue;
            }
            if (StringUtils.isNotBlank(display) && !role.getDisplay().toLowerCase().contains(display.toLowerCase())) {
                continue;
            }

            TableComponentRow tableRow = TableComponentRow.newInstance(role);
            tableRow.addAttribute("en_US", role.getEn_US());
            tableRow.addAttribute("zh_CN", role.getZh_CN());
            result.add(tableRow);
        }

        return result;
    }
}
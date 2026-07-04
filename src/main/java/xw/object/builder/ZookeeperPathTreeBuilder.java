package xw.object.builder;

import java.util.ArrayList;
import java.util.List;

import com.flame.xui.XCommandBean;
import com.flame.xui.XUIRowId;
import com.flame.config.basic.BasicConfiguration;
import com.flame.util.FlameUtils;
import com.flame.util.XException;
import com.flame.xui.ArrayComponent;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.xui.service.TreeComponentNode;
import com.flame.xui.widget.IconBox;
import com.flame.xui.widget.TextDisplay;

@UITreeGrid(idField = "rowId", treeField = "name", title = "Node", rowNumber = false, fit = true, //
        columns = { //
                @UIColumn(field = "rowId", hidden = true), //
                @UIColumn(field = "name", width = "400") //
        } //
)
public class ZookeeperPathTreeBuilder extends AbstractTreeComponentBuilder {
    private static final String ROOT = "/";
    private static final String SEP = "/";

    @Override
    public List<?> getRootNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();

        TreeComponentNode node = TreeComponentNode.newTreeComponentNode();
        node.setRowId(encodeRowId(ROOT));
        node.addAttribute("name", new ArrayComponent(new IconBox("images/zkeeper.png"), new TextDisplay("/")));
        result.add(node);

        try {
            List<String> children = BasicConfiguration.getFramework().getChildren().forPath(ROOT);
            for (String name : children) {
                TreeComponentNode subNode = TreeComponentNode.newTreeComponentNode();
                subNode.setRowId(encodeRowId(ROOT + name));
                subNode.addAttribute("name", new ArrayComponent(new IconBox("images/zkeeper-node.png"), new TextDisplay(name)));
                node.addChildren(subNode);
            }
        } catch (Exception e) {
            throw new XException(e);
        }

        return result;
    }

    @Override
    public List<?> getNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();
        XUIRowId uiRowId = commandBean.getRowId();
        if (uiRowId == null)
            return result;

        String rowId = decodeRowId(uiRowId.getValue());
        try {
            List<String> children = BasicConfiguration.getFramework().getChildren().forPath(rowId);
            for (String name : children) {
                TreeComponentNode subNode = TreeComponentNode.newTreeComponentNode();
                subNode.setRowId(encodeRowId(rowId + SEP + name));
                subNode.addAttribute("name", new ArrayComponent(new IconBox("images/zkeeper-node.png"), new TextDisplay(name)));
                result.add(subNode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private String encodeRowId(String path) {
        String encodePath = FlameUtils.getBase64Encode(path);
        return encodePath.replace("=", "");
    }

    private String decodeRowId(String path) {
        return FlameUtils.getBase64Decode(path);
    }
}

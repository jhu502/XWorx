package xw.context.builder;

import com.flame.xui.XCommandBean;
import com.flame.xui.builder.AbstractTreeComponentBuilder;
import com.flame.annotations.UIColumn;
import com.flame.annotations.UITreeGrid;

import java.util.ArrayList;
import java.util.List;

@UITreeGrid(idField = "oid", treeField = "name", rowNumber = false, fit = true, //
        columns = { //
                @UIColumn(field = "rowId", hidden = true), //
                @UIColumn(field = "oid", hidden = true), //
                @UIColumn(field = "name", width = "150px", align = "left") //
        } //
)
public class LibraryHierarchyTreeBuilder extends AbstractTreeComponentBuilder {
    @Override
    public List<? extends Object> getRootNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();

        return result;
    }

    @Override
    public List<? extends Object> getNode(XCommandBean commandBean) {
        List<Object> result = new ArrayList<>();

        return result;
    }
}

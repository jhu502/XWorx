package plm.dynamic.bean;

import java.util.ArrayList;
import java.util.List;

import com.flame.orm.XJsonType;

public class XCaseRows extends XJsonType<XCaseRows> {
    private static final long serialVersionUID = 1L;
    private List<List<Object>> data = new ArrayList<>();

    public List<List<Object>> getData() {
        return data;
    }

    public void setData(List<List<Object>> data) {
        this.data = data;
    }

    public void addRow(List<Object> row) {
        this.data.add(row);
    }
}

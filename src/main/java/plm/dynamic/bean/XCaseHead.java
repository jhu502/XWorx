package plm.dynamic.bean;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.orm.JsonObjectConverter;
import com.flame.orm.XJsonType;

public class XCaseHead extends XJsonType<XCaseHead> {
    private static final long serialVersionUID = 1L;
    private List<String> data = new ArrayList<>();

    public List<String> getData() {
        return this.data;
    }

    public void setData(List<String> columns) {
        this.data = columns;
    }

    public void addColumn(String column) {
        this.data.add(column);
    }

    @JsonIgnore
    public int length() {
        return this.data.size();
    }

    public String toString() {
        return this.data.toString();
    }
    
    public static void main(String[] args) {
        String header = "{\"xclass\":\"plm.dynamic.bean.XCaseHead\",\"columns\": [\"INLET_POWER\", \"INVERTER_STO_COMPONENT\", \"OTHER_INTERFACE\"]}";
        System.out.println(header);
        JsonObjectConverter converter = new JsonObjectConverter();
        Object object = converter.convertToEntityAttribute(header);
        System.out.println(object.getClass().getName());
    }
}

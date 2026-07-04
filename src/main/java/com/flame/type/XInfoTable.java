package com.flame.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class XInfoTable implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, XFieldDefinition> _dataShapes = new LinkedHashMap<>();
    private List<XValueCollection> _rows = new ArrayList<>();

    @JsonIgnore
    public Map<String, XFieldDefinition> getDataShapes() {
        return this._dataShapes;
    }

    public void addField(XFieldDefinition field) {
        this._dataShapes.put(field.getName(), field);
    }

    @JsonIgnore
    public int getColumnCount() {
        return this._dataShapes.size();
    }

    public List<XValueCollection> getRows() {
        return this._rows;
    }

    public void addRow(XValueCollection xvalue) {
        this._rows.add(xvalue);
    }

    public int getTotal() {
        return this._rows.size();
    }

    @JsonIgnore
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        JSONObject dataShape = new JSONObject();
        jsonObject.put("dataShape", dataShape);
        JSONObject definitions = new JSONObject();
        dataShape.put("fieldDefinitions", definitions);
        for (Entry<String, XFieldDefinition> entry : _dataShapes.entrySet()) {
            XFieldDefinition fieldDef = entry.getValue();
            definitions.put(entry.getKey(), fieldDef.toJSONObject());
        }
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("rows", jsonArray);
        for (XValueCollection xvalue : this._rows) {
            jsonArray.put(xvalue.toJSONObject());
        }

        return jsonObject;
    }

    public String toString() {
        return this.toJSONObject().toString();
    }

}

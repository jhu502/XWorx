package com.flame.type;

import org.json.JSONObject;

public class XFieldDefinition {
    private String _name;
    private String _description;
    private int _ordinal;
    private XBaseType _baseType;

    public XFieldDefinition(String name, String description, XBaseType baseType) {
        this._name = name;
        this._description = description;
        this._baseType = baseType;
    }

    public XBaseType getBaseType() {
        return _baseType;
    }

    public void setBaseType(XBaseType baseType) {
        this._baseType = baseType;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public int getOrdinal() {
        return _ordinal;
    }

    public void setOrdinal(int ordinal) {
        this._ordinal = ordinal;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.getName());
        jsonObject.put("description", this.getDescription());
        jsonObject.put("ordinal", this.getOrdinal());
        jsonObject.put("baseType", this.getBaseType().name());

        return jsonObject;
    }

    public String toString() {
        return this._name + ":" + this._baseType.name();
    }
}

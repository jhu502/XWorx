package com.flame.type;

import java.util.HashMap;

import com.flame.util.XException;
import org.json.JSONObject;

public class XValueCollection extends HashMap<String, IPrimitiveType<?>> {
    private static final long serialVersionUID = 1L;

    @Override
    public IPrimitiveType<?> put(String key, IPrimitiveType<?> primotive) {
        return super.put(key, primotive);
    }

    public JSONObject toJSONObject() throws XException {
        JSONObject jsonObject = new JSONObject();
        for (Entry<String, IPrimitiveType<?>> entry : this.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }

        return jsonObject;
    }
}

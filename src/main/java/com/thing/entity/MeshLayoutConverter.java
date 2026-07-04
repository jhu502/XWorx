package com.thing.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.flame.orm.JsonObjectConverter;
import com.flame.util.JsonUtils;

import jakarta.persistence.AttributeConverter;

/**
 * {@link com.thing.entity.MeshLayout} 专用的 JSONB 转换器。
 * <p>
 * 与 {@link JsonObjectConverter} 不同，本转换器直接指定目标类型为
 * {@code MeshLayout}，不依赖 JSON 中的 {@code xclass} 字段，
 * 因此旧数据或手动插入的数据也能正确反序列化。
 * </p>
 */
public class MeshLayoutConverter implements AttributeConverter<MeshLayout, String> {

    @Override
    public String convertToDatabaseColumn(MeshLayout object) {
        if (object == null)
            return null;

        return JsonUtils.toJson(object);
    }

    @Override
    public MeshLayout convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isBlank())
            return null;

        JsonNode node = JsonUtils.parseNode(dbData);
        MeshLayout meshLayout = JsonUtils.toObject(node, MeshLayout.class);
        meshLayout.postProcess();
        return meshLayout;
    }
}

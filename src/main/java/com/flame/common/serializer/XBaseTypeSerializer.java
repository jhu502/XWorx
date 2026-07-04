package com.flame.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.type.XBaseType;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 基础类型序列化器 —— 将 {@link XBaseType} 实例序列化为其显示文本。
 *
 * <h3>与 {@link EnumeratedTypeSerializer} 的区别</h3>
 * <p>{@code XBaseType} 是更底层的基础类型抽象（非枚举），其 {@code getDisplay()} 方法不带 Locale 参数，
 * 输出的是系统默认的显示名称，不随用户语言环境变化。适用于系统级的类型标识（如数据类型、状态码等）。</p>
 *
 * <h3>注册方式</h3>
 * <p>通过 {@link JsonComponent @JsonComponent} 注解自动注册到 Jackson ObjectMapper。</p>
 *
 * @author Hujin
 * @see XBaseType
 * @see XBaseType
 */
@JsonComponent
public class XBaseTypeSerializer extends JsonSerializer<XBaseType> {
    /**
     * 将 XBaseType 实例序列化为其显示文本字符串。
     *
     * @param object       XBaseType 实例（可为null）
     * @param generator    JSON 生成器
     * @param serializers  序列化器提供者（未使用）
     */
    @Override
    public void serialize(XBaseType object, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        if (object == null) {
            generator.writeObject(null);
            return;
        }
        generator.writeString(object.getDisplay());
    }
}

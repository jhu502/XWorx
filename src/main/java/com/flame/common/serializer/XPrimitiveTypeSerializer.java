package com.flame.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.type.IPrimitiveType;
import com.flame.type.primitive.XDatetimePrimitive;
import com.flame.type.primitive.XObjectPrimitive;
import com.flame.type.primitive.XTimespanPrimitive;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 原始类型序列化器 —— 将 {@link IPrimitiveType} 子类按各自的值类型进行序列化。
 *
 * <h3>类型分发</h3>
 * <p>不同类型的原始值有不同的序列化策略：
 * <ul>
 *   <li>{@link XObjectPrimitive} —— 直接输出内部值对象（可能为任意JSON兼容类型）</li>
 *   <li>{@link XTimespanPrimitive} —— 直接输出时间段值</li>
 *   <li>{@link XDatetimePrimitive} —— 将日期时间值转为 {@code toString()} 字符串输出<br>
 *       （注意：与其他两种不同，此处显式调用 toString() 确保输出为ISO格式字符串）</li>
 *   <li>其他 {@link IPrimitiveType} 子类 —— 直接输出 {@code getValue()} 返回值</li>
 * </ul></p>
 *
 * <h3>注册方式</h3>
 * <p>通过 {@link JsonComponent @JsonComponent} 注解自动注册。</p>
 *
 * @param <T> 原始类型，必须继承 {@link IPrimitiveType}
 * @author Hujin
 * @see IPrimitiveType
 */
@JsonComponent
public class XPrimitiveTypeSerializer<T extends IPrimitiveType<?>> extends JsonSerializer<T> {
    /**
     * 根据原始值的具体子类型，采用对应的序列化策略。
     *
     * @param object       原始类型实例（可为null）
     * @param generator    JSON 生成器
     * @param serializers  序列化器提供者（未使用）
     */
    @Override
    public void serialize(T object, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        if (object == null) {
            generator.writeObject(null);
            return;
        }

        if (object instanceof XObjectPrimitive) {
            XObjectPrimitive _primitive = (XObjectPrimitive) object;
            generator.writeObject(_primitive.getValue());
        } else if (object instanceof XTimespanPrimitive) {
            XTimespanPrimitive _primitive = (XTimespanPrimitive) object;
            generator.writeObject(_primitive.getValue());
        } else if (object instanceof XDatetimePrimitive) {
            XDatetimePrimitive _primitive = (XDatetimePrimitive) object;
            generator.writeObject(_primitive.getValue().toString());
        } else {
            generator.writeObject(object.getValue());
        }
    }
}

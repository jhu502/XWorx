package com.flame.common.serializer;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.auths.SessionHelper;
import com.flame.meta.EnumeratedType;

/**
 * 枚举类型序列化器 —— 将 {@link EnumeratedType} 实例序列化为其国际化显示文本。
 *
 * <h3>序列化行为</h3>
 * <p>不输出枚举的 {@code name()} 或 {@code ordinal()}，而是调用 {@code object.getDisplay(SessionHelper.getLocale())}
 * 根据当前会话的 {@link java.util.Locale} 输出本地化的显示名称。例如：
 * <ul>
 *   <li>中文环境：{@code "管理员"}</li>
 *   <li>英文环境：{@code "Administrator"}</li>
 * </ul>
 * 这样前端直接拿到可读文本，无需额外的国际化处理。</p>
 *
 * <h3>注册方式</h3>
 * <p>通过 {@link JsonComponent @JsonComponent} 注解由 Spring Boot 自动发现并注册到 Jackson 的 {@code ObjectMapper} 中，
 *   无需手动配置。框架内所有返回 {@code EnumeratedType} 或其子类的 JSON 端点都会自动使用此序列化器。</p>
 *
 * @param <T> 枚举类型，必须实现 {@link EnumeratedType} 接口
 * @author Hujin
 * @see EnumeratedType
 * @see SessionHelper#getLocale()
 */
@JsonComponent
public class EnumeratedTypeSerializer<T extends EnumeratedType<?>> extends AbstractSerializer<T> {
    /**
     * 将枚举值序列化为其国际化显示文本。
     *
     * @param object       枚举实例（可为null）
     * @param generator    JSON 生成器
     * @param serializers  序列化器提供者（未使用）
     */
    @Override
    public void serialize(T object, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        if (object == null) {
            generator.writeObject(null);
            return;
        }

        generator.writeString(object.getDisplay(SessionHelper.getLocale()));
    }
}

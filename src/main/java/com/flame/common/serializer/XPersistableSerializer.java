package com.flame.common.serializer;

import java.io.IOException;
import java.lang.reflect.Method;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.orm.XPersistable;

/**
 * 持久化对象序列化器 —— 将 {@link XPersistable} 实例序列化为 JSON 对象，同时防止外键循环引用。
 *
 * <h3>核心问题：外键循环引用</h3>
 * <p>XPersistable 对象是数据库实体，之间存在双向外键关联（如 {@code A → B → A}）。
 * 如果 Jackson 默认递归序列化所有属性，会陷入无限循环最终抛出 {@code StackOverflowError}。
 * 本序列化器通过以下策略解决此问题：</p>
 *
 * <h3>序列化策略（委托给 {@link AbstractSerializer#serialize(Object, Method, JsonGenerator)}）</h3>
 * <ol>
 *   <li><b>XPersistable 属性 → 只输出 OID</b><br>
 *       遍历对象的所有 getter 方法时，若返回值是 XPersistable 类型，不递归序列化整个对象，
 *       而只输出其 {@code getOid()} 返回值。前端可根据 OID 自行按需加载关联数据。</li>
 *   <li><b>@JsonAnyGetter 注解的方法 → 展平 Map</b><br>
 *       Map 中的键值对被直接写入父 JSON 对象，不创建嵌套对象。</li>
 *   <li><b>@JsonUnwrapped 注解的方法 → 展平对象</b><br>
 *       对象的属性被展开到父 JSON 对象中。</li>
 *   <li><b>其他属性 → 正常序列化</b></li>
 * </ol>
 *
 * <h3>触发条件</h3>
 * <p>只有当 Spring MVC 控制器方法上标注了 {@code produces = MediaType.APPLICATION_JSON_VALUE}
 * 时，Jackson 才会调用此序列化器。普通的 toString() 或日志输出不受影响。</p>
 *
 * <h3>注册方式</h3>
 * <p>通过 {@link JsonComponent @JsonComponent} 注解由 Spring Boot 自动注册。</p>
 *
 * @param <T> 持久化对象类型
 * @author Hujin
 * @see AbstractSerializer
 */
@JsonComponent
public class XPersistableSerializer<T extends XPersistable> extends AbstractSerializer<T> {
    /**
     * 序列化持久化对象的所有 getter 方法返回值，写入 {@code {}} 包裹的 JSON 对象。
     *
     * <p>核心逻辑委托给父类的 {@link AbstractSerializer#serialize(Object, Method, JsonGenerator)} 方法，
     * 该方法内部根据返回值类型和注解自动决策序列化策略（OID/展平/正常输出）。</p>
     *
     * @param object       持久化对象实例（可为null）
     * @param generator    JSON 生成器
     * @param provider     序列化器提供者（未直接使用，由父类方法处理）
     */
    @Override
    public void serialize(T object, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (object == null) {
            generator.writeObject(null);
            return;
        }

        generator.writeStartObject();
        for (Method method : object.getClass().getMethods()) {
            this.serialize(object, method, generator);
        }
        generator.writeEndObject();
    }

}

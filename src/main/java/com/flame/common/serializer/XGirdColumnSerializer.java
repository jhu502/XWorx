package com.flame.common.serializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.xui.widget.GridColumn;
import com.flame.xui.XUIWidget;
import com.flame.util.XException;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 网格列配置序列化器 —— 将 {@link GridColumn}（表格/树形表格的列定义）序列化为 JSON 对象。
 *
 * <h3>与 XUIWidgetSerializer 的协作</h3>
 * <p>UI控件序列化器（{@link XUIWidgetSerializer}）默认将 Widget 渲染为 HTML 字符串。
 * 但当 Widget 被嵌入在 DataGrid/TreeGrid/PropertyGrid 的列配置中时，前端需要的是 Widget 的属性 JSON
 * （而非 HTML），以便用 JS 读取并构建列配置。本序列化器专门处理此场景：</p>
 *
 * <h3>序列化流程</h3>
 * <ol>
 *   <li>遍历列对象的所有 getter 方法（排除 {@code @JsonIgnore}、非 getter 方法）</li>
 *   <li>对于普通属性值 —— 正常写入 JSON 字段</li>
 *   <li>对于 {@link XUIWidget} 类型的属性值 —— 递归遍历 Widget 的 getter 方法，将其属性展开写入嵌套 JSON 对象<br>
 *       （而非调用 {@code renderHTML()} 输出 HTML 字符串）</li>
 *   <li>特殊处理：{@code formatter} 字段若为空则跳过（避免输出空的格式化函数定义）</li>
 * </ol>
 *
 * <h3>注册方式</h3>
 * <p>通过 {@link JsonComponent @JsonComponent} 注解自动注册。</p>
 *
 * @param <T> 列配置类型，必须继承 {@link GridColumn}
 * @author Hujin
 * @see GridColumn
 * @see XUIWidgetSerializer
 */
@JsonComponent
public class XGirdColumnSerializer<T extends GridColumn> extends AbstractSerializer<T> {
    /**
     * 序列化网格列配置为 JSON 对象，对嵌套的 XUIWidget 属性展开其 JSON 结构。
     *
     * <h4>XUIWidget 展开逻辑</h4>
     * <p>当遇到 {@code XUIWidget} 类型的属性值时，不调用其序列化器（会输出HTML），
     * 而是手动遍历 Widget 的 getter 方法。
     * 这样前端可以拿到完整的 Widget 属性 JSON 用于构建列配置。</p>
     *
     * @param object     网格列配置对象（可为null）
     * @param generator  JSON 生成器
     * @param provider   序列化器提供者（未使用）
     */
    @Override
    public void serialize(T object, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (object == null) {
            generator.writeObject(null);
            return;
        }

        generator.writeStartObject();
        for (Method method : object.getClass().getMethods()) {
            if (!this.isTargetMethod(method))
                continue;
            if (method.getAnnotation(JsonIgnore.class) != null)
                continue;
            try {
                String field = this.getFieldName(method.getName());
                Object value = method.invoke(object);
                if (value == null)
                    continue;
                // formatter字段若为空则不输出，避免前端拿到无效的格式化函数
                if ("formatter".equals(field) && isBlank(value))
                    continue;

                if (value instanceof XUIWidget) {
                    // XUIWidget在列配置中需要展开为JSON对象（而非HTML字符串）
                    XUIWidget widget = (XUIWidget) value;
                    generator.writeFieldName(field);
                    generator.writeStartObject();
                    for (Method _method : widget.getClass().getMethods()) {
                        this.serialize(widget, _method, generator);
                    }
                    generator.writeEndObject();
                } else {
                    generator.writeObjectField(field, value);
                }
            } catch (XException e) {
                throw e;
            } catch (Exception e) {
                throw new XException(e);
            }
        }
        generator.writeEndObject();
    }
}

package com.flame.common.serializer;

import java.io.IOException;
import java.lang.reflect.Method;

import com.flame.xui.XUIMeshGrid;
import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.util.XException;

/**
 * 网格表单序列化器 —— 将 {@link XUIMeshGrid}（网格表单配置）序列化为 JSON 对象。
 *
 * <h3>核心问题：widgetMap 的双重序列化需求</h3>
 * <p>XMeshGrid 的 {@code widgetMap} 属性中存储了所有 UI 控件的映射（控件ID → Widget实例）。
 * 在序列化时存在矛盾需求：
 * <ul>
 *   <li>正常情况：Widget 应序列化为 HTML 字符串（通过 {@link XUIWidgetSerializer}）</li>
 *   <li>widgetMap 场景：Widget 需序列化为 JSON 对象，因为前端 JS 需要读取控件属性来动态构建表单</li>
 * </ul></p>
 *
 * <h3>解决方案：ThreadLocal 模式切换</h3>
 * <p>在序列化 {@code widgetMap} 之前，临时将 {@link AbstractSerializer#Widget2HTML} 设为 {@code false}，
 * 使 Widget 序列化器输出 JSON 对象而非 HTML 字符串。序列化完成后在 {@code finally} 块中恢复。
 * 这一机制确保了线程安全（ThreadLocal）且不影响其他字段的序列化。</p>
 *
 * <h3>序列化流程</h3>
 * <ol>
 *   <li>遍历 XMeshGrid 的所有 getter 方法</li>
 *   <li>普通属性：直接写入 JSON 字段</li>
 *   <li>{@code widgetMap} 属性：临时切换 Widget2HTML=false，使内嵌 Widget 输出 JSON 对象</li>
 * </ol>
 *
 * @author Hujin
 * @see AbstractSerializer#Widget2HTML
 * @see XUIWidgetSerializer
 * @see XUIMeshGrid
 */
@JsonComponent
public class XMeshGridSerializer extends AbstractSerializer<XUIMeshGrid> {
    /**
     * 序列化 XMeshGrid 配置为 JSON 对象，对 widgetMap 进行特殊处理。
     *
     * <h4>widgetMap 特殊处理</h4>
     * <p>在输出 widgetMap 字段前设置 {@code Widget2HTML = false}，使得其中所有 XUIWidget
     * 以完整 JSON 对象的形式序列化（而非 HTML 字符串）。前端通过此 JSON 获取控件的类型、
     * 配置、校验规则等元数据，用于动态渲染表单。</p>
     *
     * @param meshGrid   XMeshGrid 配置实例（可为null）
     * @param generator  JSON 生成器
     * @param provider   序列化器提供者（未使用）
     */
    @Override
    public void serialize(XUIMeshGrid meshGrid, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (meshGrid == null) {
            generator.writeObject(null);
            return;
        }

        generator.writeStartObject();
        for (Method method : meshGrid.getClass().getMethods()) {
            if (!this.isTargetMethod(method))
                continue;
            if (method.getAnnotation(JsonIgnore.class) != null)
                continue;
            try {
                String field = this.getFieldName(method.getName());
                Object value = method.invoke(meshGrid);
                if ("widgetMap".equals(field)) {
                    /**
                     * XMeshGrid的widgetMap中的XUIWidget需要以json的方式返回客户端，
                     * 以方便客户端通过javascript去读取XUIWidget的属性信息。
                     * 临时将Widget2HTML设为false，序列化完成后在finally中恢复。
                     */
                    try {
                        Widget2HTML.set(Boolean.FALSE);
                        generator.writeObjectField(field, value);
                    } finally {
                        Widget2HTML.remove();
                    }
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

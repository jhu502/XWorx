package com.flame.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.flame.xui.IWidget;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * UI 控件序列化器 —— 将 {@link IWidget} 实例序列化为 HTML 字符串或 JSON 对象。
 *
 * <h3>双模式序列化</h3>
 * <p>Widget 有两种序列化模式，通过父类的 ThreadLocal 开关 {@link AbstractSerializer#Widget2HTML} 控制：
 * <ul>
 *   <li><b>HTML 模式</b>（{@code Widget2HTML == null || Widget2HTML == true}，默认）<br>
 *       调用 {@code widget.renderHTML()} 输出 HTML 标签字符串。<br>
 *       例如：{@code <a href='javascript:void(0)'><img src='images/details.gif'/></a>}</li>
 *   <li><b>JSON 模式</b>（{@code Widget2HTML == Boolean.FALSE}）<br>
 *       遍历 Widget 的所有 getter 方法，输出完整的属性 JSON 对象。<br>
 *       用于 XMeshGrid 的 widgetMap 等场景，前端 JS 需要读取 Widget 的属性信息。</li>
 * </ul></p>
 *
 * <h3>EasyUI 组件自动降级</h3>
 * <p>当 Widget 被嵌入到 DataGrid/TreeGrid/PropertyGrid 的数据行中时，EasyUI 组件无法在表格单元格内正常渲染。
 * 此时 {@link XGridRowSerializer} 会通过 ThreadLocal 传递行上下文，本序列化器检测到后自动调用
 * {@code widget.setEasyGui(false)} 将 Widget 降级为普通 HTML 渲染。</p>
 *
 * <h3>关于 @JsonComponent</h3>
 * <p>{@link JsonComponent @JsonComponent} 是 Spring Boot 的核心注解。使用此注解后，
 * Spring Boot 自动将序列化器注册到 Jackson 的 {@code ObjectMapper} 中，
 * 无需手动调用 {@code objectMapper.registerModule()}。</p>
 *
 * @param <T> UI 控件类型，必须实现 {@link IWidget} 接口
 * @author Hujin
 * @see AbstractSerializer#Widget2HTML
 * @see XGridRowSerializer#ROW_COMPONENT
 */
@JsonComponent
public class XUIWidgetSerializer<T extends IWidget> extends AbstractSerializer<T> {
    /**
     * 根据 Widget2HTML 开关决定序列化为 HTML 字符串还是 JSON 对象。
     *
     * <h4>执行流程</h4>
     * <ol>
     *   <li>null 检查 —— 输出空字符串（注意：使用 {@code writeNumber("")} 而非 writeNull）</li>
     *   <li>检测行上下文 —— 若存在 XGridRowSerializer 传递的 RowComponent，自动关闭 EasyUI 模式</li>
     *   <li>判断渲染模式：
     *     <ul>
     *       <li>JSON 模式（Widget2HTML == FALSE）→ 输出完整属性 JSON 对象</li>
     *       <li>HTML 模式（默认）→ 调用 {@code renderHTML()} 输出 HTML 字符串</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * <p>注意：{@code Widget2HTML.get()} 必须与 {@code Boolean.FALSE} 进行 {@code ==} 比较，
     * 而不能用 {@code .equals()}，因为在未设置时 get() 返回 null，== 比较不会抛出 NullPointerException。</p>
     *
     * @param widget    UI 控件实例（可为null）
     * @param generator JSON 生成器
     * @param provider  序列化器提供者（未使用）
     */
    @Override
    public void serialize(T widget, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (widget == null) {
            generator.writeNumber("");
            return;
        }

        /**
         * 标识当前Widget是否是Easyui组件。
         * Widget被嵌入到DataGrid/TreeGrid/PropertyGrid的数据行时，easyui组件无法被渲染，只适合作为普通的html来处理。
         */
        if (widget.isEasyUI()) {
            if (XGridRowSerializer.getRowComponent() != null) {
                widget.setEasyUI(false);
            }
        }
        /**
         * XMeshGrid组件既要返回XUIWidget的html，又需要返回XUIWidget的json：
         * - FALSE: 以json的方式返回XUIWidget组件的所有属性
         * - TRUE/null: 以html的方式返回XUIWidget组件（调用renderHTML()）
         * 说明: 必须是与Boolean.FALSE进行 == 比较，不然在未设置时会NullPointerException
         */
        if (Widget2HTML.get() == Boolean.FALSE) {
            generator.writeStartObject();
            for (Method method : widget.getClass().getMethods()) {
                this.serialize(widget, method, generator);
            }
            generator.writeEndObject();
        } else {
            generator.writeString((widget).renderHTML());
        }
    }
}

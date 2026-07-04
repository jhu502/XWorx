package com.flame.xui.widget;

import com.flame.xui.HREFactory;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * 超链接组件，渲染为 {@code <a>} 标签。
 *
 * <p>支持三种构造方式：通过注解、无参默认、带 {@code anchor} 标记。
 * URL 为空时自动使用 {@code javascript:void(0)} 占位。
 * 支持内嵌子组件（如 {@link IconBox}）渲染为链接内容。</p>
 */
public class HyperLink extends XUIWidget {

    /** URL 查询参数 */
    private Map<String, Object> params = new HashMap<>();
    /** 是否为锚点链接 */
    private boolean anchor = false;

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public HyperLink(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /** 无参默认构造，WidgetType 为 {@link WidgetType#HyperLink}。 */
    public HyperLink() {
        super(WidgetType.HyperLink);
        this.setEasyUI(false);
    }

    /**
     * 构造并可选设置为锚点链接。
     * @param anchor 为 {@code true} 时调用 {@link #setAnchor()}
     */
    public HyperLink(boolean anchor) {
        super(WidgetType.HyperLink);
        if (anchor) {
            this.setAnchor();
        }
        this.setEasyUI(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "a";
    }

    /**
     * 添加 URL 查询参数。
     * @param name  参数名
     * @param value 参数值
     */
    public void addParam(String name, Object value) {
        this.params.put(name, value);
    }

    /** 标记为锚点链接，影响 {@link HREFactory#getHREF} 的 URL 生成策略。 */
    public void setAnchor() {
        this.anchor = true;
    }

    /**
     * 设置 {@code href} 属性：URL 为空时用 {@code javascript:void(0)} 占位，
     * 否则通过 {@link HREFactory#getHREF} 拼接 contextPath。
     */
    protected void appendDomAttributes() {
        if (FlameUtils.isBlank(this.getUrl())) {
            this.appendEl("href", "javascript:void(0)");
        } else {
            this.appendEl("href", HREFactory.getHREF(this.getUrl(), this.params, this.anchor));
        }
        this.appendEl("style", this.getStyle());
    }

    /** 渲染 innerHTML：先 text，再递归渲染内嵌子组件。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }

        if (this.getInnerObject() instanceof IWidget) {
            IWidget widget = (IWidget) this.getInnerObject();
            this.append(widget.renderHTML());
        } else {
            if (this.getInnerObject() != null) {
                this.append(this.getInnerObject().toString());
            }
        }
    }
}

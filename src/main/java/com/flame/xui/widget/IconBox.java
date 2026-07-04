package com.flame.xui.widget;

import com.flame.xui.HREFactory;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

/**
 * 图标组件，渲染为 {@code <img>} 标签。
 *
 * <p>URL 为空时自动使用默认图片 {@code images/body/fails.png}。
 * 自动追加垂直居中和右边距样式。</p>
 *
 * <p>常用作 {@link ArrayComponent} 的子元素，与 {@link TextDisplay} 组合
 * 渲染树节点或列表项的图标+文本。</p>
 */
public class IconBox extends XUIWidget {

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public IconBox(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
    }

    /**
     * 通过图片路径构造。
     * @param img 图片路径，如 {@code "images/role.gif"}
     */
    public IconBox(String img) {
        super(WidgetType.IconBox);
        this.setUrl(img);
        this.setEasyUI(false);
    }

    /**
     * 通过图片路径和自定义样式构造。
     * @param img   图片路径
     * @param style 追加的 CSS 样式
     */
    public IconBox(String img, String style) {
        super(WidgetType.IconBox);
        this.setUrl(img);
        this.setStyle(style);
        this.setEasyUI(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "img";
    }

    /**
     * 设置 {@code src} 和 {@code style} DOM 属性。
     * URL 为空时回退到默认图片；自动追加垂直居中和右边距样式。
     */
    @Override
    protected void appendDomAttributes() {
        if (FlameUtils.isBlank(this.getUrl())) {
            this.setUrl("images/body/fails.png");
        }

        this.appendEl("src", HREFactory.getHREF(this.getUrl()));
        if (FlameUtils.isBlank(this.getStyle())) {
            this.appendEl("style", "margin-right:2px;vertical-align:middle;padding-right:1px;");
        } else {
            this.appendEl("style", this.getStyle() + "margin-right:2px;vertical-align:middle;padding-right:1px;");
        }
    }

    /** 若设置了 text，输出为 innerHTML。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
    }
}

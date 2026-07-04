package com.flame.xui.widget;

import com.flame.xui.HREFactory;
import com.flame.common.serializer.XGridRowSerializer;
import com.flame.orm.XPersistable;
import com.flame.util.FlameUtils;
import com.flame.xui.XUIWidget;
import com.flame.xui.IWidget;
import com.flame.xui.WidgetType;
import com.flame.annotations.UIWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * 弹窗图标链接组件，渲染为 {@code <a href="javascript:void(0)"><img src="images/details.gif"/></a>}。
 *
 * <p>点击默认触发 {@code flame.openDialogPage(event, title, url, style)}
 * 打开模态弹窗页面。与 {@link DetailIcon} 的区别在于使用
 * {@code openDialogPage} 而非 {@code openDetailsPage}。</p>
 *
 * <p>支持通过 {@link #addEvent addEvent(ON_CLICK, ...)} 覆盖默认点击行为。</p>
 *
 * @see DetailIcon
 */
public class DialogIcon extends XUIWidget {

    private static final String DETAILS_GIF = "images/details.gif";

    /** URL 查询参数 */
    private Map<String, Object> params = new HashMap<>();
    /** 是否为锚点链接 */
    private boolean anchor = false;

    /**
     * 通过 {@link UIWidget} 注解构造。
     * @param widget UIWidget 注解实例
     */
    public DialogIcon(UIWidget widget) {
        super(widget);
        this.setEasyUI(false);
        this.setInnerObject(new IconBox(DETAILS_GIF));
    }

    /**
     * 通过实体对象构造。
     * 若未手动设置 onclick，则默认渲染为 {@code flame.openDialogPage(event, ...)}。
     *
     * @param value 实体对象（{@link XPersistable}）或 OID 字符串
     */
    public DialogIcon(Object value) {
        super(WidgetType.HyperLink);
        this.setValue(value);
        this.setEasyUI(false);
        this.setInnerObject(new IconBox(DETAILS_GIF));
    }

    /** {@inheritDoc} */
    @Override
    public String getTag() {
        return "a";
    }

    /** Grid 行内使用时自动追加 {@code event.stopPropagation()}。 */
    @Override
    protected void appendEventElement() {
        if (XGridRowSerializer.getRowComponent() != null && !XUIWidget.isWidgetEmbedded()) {
            String value = this.getEventMap().get(ON_CLICK);
            if (FlameUtils.isBlank(value)) {
                value = "event.stopPropagation();";
            } else {
                value = value + ";event.stopPropagation();";
            }
            this.addEvent(ON_CLICK, value);
        }
        super.appendEventElement();
    }

    /**
     * 设置 {@code href} 和 {@code onclick} DOM 属性。
     * 若未手动设置 onclick，则自动生成 {@code flame.openDialogPage(event, title, url, style)}。
     */
    @Override
    protected void appendDomAttributes() {
        if (FlameUtils.isBlank(this.getUrl())) {
            this.appendEl("href", "javascript:void(0)");
        } else {
            this.appendEl("href", HREFactory.getHREF(this.getUrl(), this.params, this.anchor));
        }

        if (!this.getEventMap().containsKey(ON_CLICK)) {
            StringBuilder onclick = new StringBuilder("flame.openDialogPage(event, '");
            onclick.append(this.getText() == null ? "" : this.getText()).append("','");
            if (FlameUtils.isBlank(this.getUrl())) {
                if (this.getValue() instanceof XPersistable) {
                    onclick.append(HREFactory.hashInfoPage((XPersistable) this.getValue())).append("','");
                } else {
                    onclick.append(HREFactory.hashInfoPage((XPersistable) this.getValue())).append("','");
                }
            } else {
                onclick.append(this.getUrl()).append("','");
            }
            if (FlameUtils.isNotBlank(this.getStyle())) {
                onclick.append(this.getStyle()).append("');");
            } else {
                onclick.append("width:800px;height:600px;');");
            }
            this.addEvent(ON_CLICK, onclick.toString());
        }
        this.appendEl("style", this.getStyle());
    }

    /** 渲染 innerHTML：text → 图标组件。 */
    @Override
    protected void genInnerHTML() {
        if (FlameUtils.isNotBlank(this.getText())) {
            this.append(this.getText());
        }
        if (this.getInnerObject() instanceof IWidget) {
            IWidget iWidget = (IWidget) this.getInnerObject();
            this.append(iWidget.renderHTML());
        } else {
            if (this.getInnerObject() != null) {
                this.append(this.getInnerObject().toString());
            }
        }
    }
}

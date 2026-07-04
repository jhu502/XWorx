package com.flame.xui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.common.serializer.XGridRowSerializer;
import com.flame.localize.AbstractEnumerated;
import com.flame.localize.LocalizationHelper;
import com.flame.util.FlameUtils;
import com.flame.util.PropertyUtil;
import com.flame.util.XException;
import com.flame.annotations.UIEvent;
import com.flame.annotations.UIWidget;
import com.flame.vc.Iterated;
import com.flame.xui.service.RowComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Widget组件的基类,实现了Widget的基础功能;
 */
public abstract class XUIWidget extends AbstractComponent implements IWidget {
    protected static final Logger logger = LoggerFactory.getLogger(XUIWidget.class);
    public static final ThreadLocal<Boolean> IS_WIDGET_EMBEDDED = new ThreadLocal<>();
    private static final String XUI = "xui-";
    private static final String EASYUI = "easyui-";
    private static char QUOTE = '\'';
    private static char COLON = ':';
    private transient ICell<? extends IRow<?>> cell; //ICell与table->td对应,指向当前Widget组件所嵌入的td;
    private String text = "";
    private String style = ""; //该字段不能够放在AbstractComponent中，会导致Grid渲染问题
    private String url = "";
    private Object value = "";
    private Object inner = null; //Widget组件的内部文本,或者是子组件
    private boolean readOnly = false;
    private boolean required = false;
    private Map<String, Object> traitMap = new HashMap<>(); //用来生成easyui的data-options属性
    private Map<String, String> eventMap = new HashMap<>();
    public static final String ON_CLICK = "onclick";
    public static final String ON_CHANGE = "onchange";
    protected StringBuilder htmlBuilder = new StringBuilder();

    protected XUIWidget(WidgetType widgetType) {
        this.setWidgetType(widgetType);
    }

    protected XUIWidget(UIWidget widget) {
        this.setWidgetType(widget.type());
        this.setId(widget.id());
        this.setName(widget.name());
        this.setStyle(widget.style());
        this.setText(widget.text());
        this.setUrl(widget.url());
        this.setTraits(widget.traits());
        if (widget.events() != null) {
            for (UIEvent event : widget.events()) {
                this.addEvent(event.name(), event.value());
            }
        }
    }

    public abstract String getTag();

    public static XUIWidget getWidget(UIWidget uiWidget) {
        if (uiWidget == null)
            return null;

        try {
            Constructor<?> constructor = uiWidget.type().getWidget().getConstructor(UIWidget.class);
            return (XUIWidget) constructor.newInstance(uiWidget);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new XException(e);
        }
    }

    protected void setCell(ICell<? extends IRow<?>> cell) {
        this.cell = cell;
    }

    @JsonIgnore
    public ICell<? extends IRow<?>> getCell() {
        return this.cell;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Map<String, String> getEventMap() {
        return eventMap;
    }

    public void addEvent(String name, String value) {
        if (name == null)
            return;

        this.eventMap.put(name, value);
    }

    public Map<String, Object> getTraitMap() {
        return traitMap;
    }

    /**
     * 将字符串的data-options转换成key-value对,并put到traitMap中;
     *
     * @param traits
     */
    public void setTraits(String traits) {
        boolean bool = true;
        int i = 0, j = 0;
        for (char c : traits.toCharArray()) {
            if (c == QUOTE) {
                bool = !bool;
            }
            if (c == ',') {
                if (bool) {
                    String trait = traits.substring(j, i);
                    int k = trait.indexOf(COLON);
                    String key = trait.substring(0, k);
                    String value = trait.substring(k + 1);
                    this._addTrait(key, value);
                    j = i + 1;
                }
            }
            i = i + 1;
        }
        if (i > j) {
            String trait = traits.substring(j, i);
            int k = trait.indexOf(COLON);
            String key = trait.substring(0, k);
            String value = trait.substring(k + 1);
            this._addTrait(key, value);
        }
    }

    private void _addTrait(String key, String value) {
        if ("true".equals(value))
            this.addTrait(key, true);
        else if ("false".equals(value))
            this.addTrait(key, false);
        else if (FlameUtils.isInteger(value))
            this.addTrait(key, Integer.parseInt(value));
        else if (FlameUtils.isNumeric(value))
            this.addTrait(key, Double.parseDouble(value));
        else
            this.addTrait(key, FlameUtils.trim(value, QUOTE));
    }

    public void addTrait(String key, Object value) {
        this.traitMap.put(key, value);
    }

    public Object getInnerObject() {
        return inner;
    }

    public void setInnerObject(Object inner) {
        this.inner = inner;
    }

    @Override
    public void inflate(Object object) {
        if (object == null)
            return;

        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), this.getName());
        if (descriptor == null) {
            if (object instanceof Iterated) {
                try {
                    Method getMasterMethod = object.getClass().getMethod("getMaster");
                    Object master = getMasterMethod.invoke(object);
                    if (master != null) {
                        descriptor = BeanUtils.getPropertyDescriptor(master.getClass(), this.getName());
                        if (descriptor != null) {
                            object = master;
                        }
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        if (descriptor == null)
            return;

        Class<?> propType = descriptor.getPropertyType();
        Method readMethod = descriptor.getReadMethod();
        if (readMethod == null)
            return;

        try {
            Object widValue = readMethod.invoke(object, new Object[0]);
            if (widValue == null)
                return;

            if (boolean.class.equals(propType) || Boolean.class.equals(propType)) {
                if (WidgetMode.Display.equals(this.getWidgetMode())) {
                    String locValue = LocalizationHelper.get(widValue.toString());
                    this.setValue(FlameUtils.isBlank(locValue) ? widValue : locValue);
                } else {
                    this.setValue(widValue);
                }
            } else if (AbstractEnumerated.class.isAssignableFrom(propType)) {
                this.setValue(((AbstractEnumerated<?>) widValue).getName());
            } else if (propType.isEnum()) {
                try {
                    Method method = propType.getMethod("name", new Class[0]);
                    this.setValue(method.invoke(widValue, new Object[0]));
                } catch (NoSuchMethodException e) {
                    throw new XException(e);
                }
            } else {
                this.setValue(widValue);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new XException(e);
        }
    }

    public XUIWidget append(String string) {
        this.htmlBuilder.append(string);
        return this;
    }

    public XUIWidget append(Object object) {
        if (object == null) {
            this.htmlBuilder.append("null");
        } else {
            this.htmlBuilder.append(object.toString());
        }
        return this;
    }

    public String getDomClass() {
        StringBuilder domClass = new StringBuilder();
        domClass.append(XUI + this.getWidgetType().getName());
        if (this.isEasyUI()) {
            domClass.append(" ").append(EASYUI + this.getWidgetType().getName());
        }

        if (this.getClasses().isEmpty()) {
            return domClass.toString();
        } else {
            return domClass.append(" ").append(super.getDomClass()).toString();
        }
    }

    public String renderHTML() {
        this.htmlBuilder = new StringBuilder();
        this.append("<").append(this.getTag()).append(" ");
        if (FlameUtils.isNotBlank(this.getId())) {
            this.appendEl("id", this.getId());
        } else {
            if (FlameUtils.isNotBlank(this.getName())) {
                this.appendEl("id", this.getName() + "-" + FlameUtils.getRandomConst());
            }
        }
        if (FlameUtils.isNotBlank(this.getName())) {
            /**
             * 解决Widget被嵌入到GridRow中时, name重名的问题, name = name + "$" + oid;
             */
            RowComponent gridRow = XGridRowSerializer.getRowComponent();
            if (gridRow == null) {
                this.appendEl("name", this.getName());
            } else {
                Object oid = PropertyUtil.getProperty(gridRow.getObject(), "oid");
                if (oid == null) {
                    oid = PropertyUtil.getProperty(gridRow.getAttributes(), "oid");
                }
                if (oid instanceof String) {
                    this.appendEl("name", this.getName() + "$" + oid);
                } else {
                    this.appendEl("name", this.getName());
                }
            }
        }

        if (this.isRequired()) {
            this.addDomClass("required");
        }

        this.appendEl("class", this.getDomClass());

        if (isEasyUI()) {
            if (WidgetMode.Display.equals(this.getWidgetMode())) {
                this.addTrait("disabled", true);
                this.addTrait("editable", false);
            }
            if (this.isReadOnly()) {
                this.addTrait("readonly", true);
                this.addTrait("editable", false);
            }
            if (this.isRequired()) {
                this.addTrait("required", true);
            }
        } else {
            if (WidgetMode.Display.equals(this.getWidgetMode())) {
                this.appendEl("disabled", true);
            }
            if (this.isReadOnly()) {
                this.appendEl("readonly", true);
            }
        }
        if (isEasyUI()) {
            this.genDataOptions();
        }
        this.appendDomAttributes();
        this.appendEventElement();
        this.append(">");

        try {
            IS_WIDGET_EMBEDDED.set(true);
            this.genInnerHTML();
        } finally {
            IS_WIDGET_EMBEDDED.remove();
        }

        this.append("</").append(this.getTag()).append(">");
        return this.htmlBuilder.toString();
    }

    protected String genTraitString() {
        StringBuilder optionBuilder = new StringBuilder();

        boolean bool = false;
        for (Entry<String, Object> entry : this.getTraitMap().entrySet()) {
            String key = entry.getKey();
            Object $val = entry.getValue();
            String value = "";
            if ($val == null)
                value = "''";
            else if ($val instanceof String)
                value = "'" + $val + "'";
            else
                value = $val.toString();

            if (bool)
                optionBuilder.append(",").append(key).append(":").append(value);
            else
                optionBuilder.append(key).append(":").append(value);

            bool = true;
        }

        return optionBuilder.toString();
    }

    protected void appendEl(String name, Object value) {
        if (FlameUtils.isBlank(name))
            return;
        if (value == null)
            return;

        if (value instanceof String) {
            if (FlameUtils.isBlank((String) value))
                return;
            this.append(name).append("=\"").append(value).append("\" ");
        } else {
            this.append(name).append("=").append(value.toString()).append(" ");
        }
    }

    protected abstract void appendDomAttributes();

    /**
     * 将EventMap中定义的事件,生成html元素的事件属性
     */
    protected void appendEventElement() {
        for (Entry<String, String> event : this.getEventMap().entrySet()) {
            this.appendEl(event.getKey(), event.getValue());
        }
    }

    protected void genInnerHTML() {
    }

    protected void genDataOptions() {
        String dataOptions = this.genTraitString();
        if (FlameUtils.isNotBlank(dataOptions)) {
            this.appendEl("data-options", dataOptions);
        }
    }

    @JsonIgnore
    public static boolean isWidgetEmbedded() {
        Boolean bool = IS_WIDGET_EMBEDDED.get();
        if (bool == null) {
            return false;
        } else {
            return bool;
        }
    }
}

package com.flame.common.serializer;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.flame.orm.XPersistable;
import com.flame.util.XException;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 抽象序列化器基类 —— 为 Flame 框架中所有自定义 Jackson 序列化器提供通用序列化逻辑。
 *
 * <h3>核心设计目标</h3>
 * <ol>
 *   <li><b>打破外键循环引用</b> —— 对 {@link XPersistable} 类型的属性只输出其 OID，而非递归序列化整个对象，
 *       从而避免 Jackson 在序列化双向关联实体时陷入无限递归导致 {@code StackOverflowError}</li>
 *   <li><b>支持 Jackson 展平注解</b> —— 正确处理 {@link JsonUnwrapped @JsonUnwrapped}（对象属性展平）
 *       和 {@link JsonAnyGetter @JsonAnyGetter}（Map 键值展平）</li>
 *   <li><b>提供方法过滤机制</b> —— 通过 {@link #isTargetMethod(Method)} 筛选需要序列化的 getter 方法，
 *       排除 {@code getClass()}、有参方法、void 返回方法等</li>
 * </ol>
 *
 * <h3>Widget 渲染模式控制</h3>
 * <p>{@link #Widget2HTML} 是一个 ThreadLocal 开关，控制 {@link com.flame.xui.IWidget} 的序列化方式：
 * <ul>
 *   <li>{@code true}（默认） —— 调用 {@code renderHTML()} 输出 HTML 字符串</li>
 *   <li>{@code false} —— 输出 Widget 属性的完整 JSON 对象（用于 XMeshGrid 的 widgetMap 等场景）</li>
 * </ul>
 * 使用 ThreadLocal 确保线程安全，避免并发请求间的状态污染。</p>
 *
 * <h3>方法过滤规则 {@link #isTargetMethod(Method)}</h3>
 * <p>只有满足以下条件的方法才会被序列化：
 * <ul>
 *   <li>方法名以 {@code get} 或 {@code is} 开头（标准 JavaBean getter 命名）</li>
 *   <li>无参数</li>
 *   <li>返回值类型不是 {@code void}</li>
 *   <li>不是 {@code getClass()} 方法</li>
 * </ul></p>
 *
 * <h3>字段名转换 {@link #getFieldName(String)}</h3>
 * <p>将 getter 方法名转换为 JSON 字段名：
 * <ul>
 *   <li>{@code getDisplay()} → {@code "display"}（去掉 "get" 前缀，首字母小写）</li>
 *   <li>{@code isEnabled()} → {@code "enabled"}（去掉 "is" 前缀，首字母小写）</li>
 * </ul>
 * 首字母小写通过 ASCII 码 +32 实现（大写字母 A-Z 的码点为 65-90，小写 a-z 为 97-122）。</p>
 *
 * @param <T> 被序列化的目标类型
 * @author Hujin
 * @see XPersistableSerializer
 * @see XGridRowSerializer
 * @see XUIWidgetSerializer
 */
public abstract class AbstractSerializer<T> extends JsonSerializer<T> {
    /**
     * Widget 渲染模式开关（ThreadLocal 保证线程安全）。
     *
     * <p>{@code true}（默认/未设置）：Widget 序列化为 HTML 字符串；<br>
     * {@code false}：Widget 序列化为完整 JSON 对象。</p>
     *
     * <p>典型使用场景：XMeshGrid 的 widgetMap 需要以 JSON 形式返回 Widget 属性供前端 JS 读取，
     * 此时会临时设为 {@code false}，序列化完成后在 finally 块中 remove 恢复默认行为。</p>
     */
    protected static ThreadLocal<Boolean> Widget2HTML = new ThreadLocal<>();

    /**
     * 序列化对象的单个方法返回值。
     *
     * <h4>处理逻辑（按优先级）</h4>
     * <ol>
     *   <li>方法带有 {@link JsonIgnore @JsonIgnore} → 跳过</li>
     *   <li>返回值是 {@link XPersistable} 类型 → 只输出 OID 字符串（打破循环引用）</li>
     *   <li>方法带有 {@link JsonAnyGetter @JsonAnyGetter} → 将 Map 的键值对展平写入父 JSON 对象</li>
     *   <li>方法带有 {@link JsonUnwrapped @JsonUnwrapped} → 递归调用 {@link #serialize(Object, JsonGenerator)} 展平对象属性</li>
     *   <li>其他情况 → 以字段名:值的形式正常输出</li>
     * </ol>
     *
     * @param object    被序列化的源对象
     * @param method    要序列化的 getter 方法
     * @param generator Jackson JSON 生成器
     * @throws XException 反射调用异常时抛出
     */
    public void serialize(Object object, Method method, JsonGenerator generator) {
        if (!this.isTargetMethod(method))
            return;

        if (method.getAnnotation(JsonIgnore.class) != null)
            return;

        try {
            String field = this.getFieldName(method.getName());
            Class<?> clazz = method.getReturnType();
            if (XPersistable.class.isAssignableFrom(clazz)) {
                // 持久化对象只输出OID，防止外键循环引用导致StackOverflow
                XPersistable persist = (XPersistable) method.invoke(object);
                if (persist == null) {
                    generator.writeStringField(field, null);
                } else {
                    generator.writeStringField(field, persist.getOid());
                }
            } else if (method.getAnnotation(JsonAnyGetter.class) != null) {
                // @JsonAnyGetter：将Map的键值对展平到父JSON中
                Map<?, ?> map = (Map<?, ?>) method.invoke(object);
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    generator.writeObjectField(key.toString(), entry.getValue());
                }
            } else if (method.getAnnotation(JsonUnwrapped.class) != null) {
                // @JsonUnwrapped：递归展平对象属性到父JSON中
                Object value = method.invoke(object);
                serialize(value, generator);
            } else {
                Object value = method.invoke(object);
                generator.writeObjectField(field, value);
            }
        } catch (XException e) {
            throw e;
        } catch (Exception e) {
            throw new XException(e);
        }
    }

    /**
     * 将对象的所有 getter 方法返回值展平写入当前 JSON 对象中（不为该对象创建新的 JSON 嵌套层级）。
     *
     * <p>与 Jackson 默认的对象序列化不同，此方法不会写入 {@code {}} 包裹，而是直接将对象的属性
     * 作为当前 JSON 对象的同级字段写入。这模拟了 {@link JsonUnwrapped @JsonUnwrapped} 的行为，
     * 但以编程方式实现，适用于无法在编译期标注注解的场景。</p>
     *
     * @param object    被展平序列化的源对象
     * @param generator Jackson JSON 生成器
     */
    public void serialize(Object object, JsonGenerator generator) {
        for (Method method : object.getClass().getMethods()) {
            if (!this.isTargetMethod(method))
                continue;

            if (method.getAnnotation(JsonIgnore.class) != null)
                continue;

            try {
                Object value = method.invoke(object);
                String field = this.getFieldName(method.getName());
                generator.writeObjectField(field, value);
            } catch (XException e) {
                throw e;
            } catch (Exception e) {
                throw new XException(e);
            }
        }
    }

    /**
     * 判断当前方法是否是需要进行序列化的 getter 方法。
     *
     * <h4>排除条件</h4>
     * <ul>
     *   <li>{@code getClass()} —— Object 的内置方法，不应序列化</li>
     *   <li>有参数的方法 —— 不是 getter</li>
     *   <li>返回值类型为 {@code void} —— 不是 getter</li>
     * </ul>
     *
     * <h4>包含条件</h4>
     * <ul>
     *   <li>方法名以 {@code get} 开头（如 {@code getName()}、{@code getDisplay()}）</li>
     *   <li>方法名以 {@code is} 开头（如 {@code isEnabled()}、{@code isActive()}）</li>
     * </ul>
     *
     * @param method 待检查的方法
     * @return true 如果该方法应被序列化
     */
    public boolean isTargetMethod(Method method) {
        if ("getClass".equals(method.getName()))
            return false;

        if (method.getParameterCount() > 0)
            return false;

        if (void.class.equals(method.getReturnType()))
            return false;

        if (method.getName().startsWith("get"))
            return true;

        if (method.getName().startsWith("is"))
            return true;

        return false;
    }

    /**
     * 将 getter 方法名转换为 JSON 字段名（去掉 get/is 前缀，首字母小写）。
     *
     * <h4>转换示例</h4>
     * <ul>
     *   <li>{@code getDisplay} → {@code display}</li>
     *   <li>{@code getName} → {@code name}</li>
     *   <li>{@code isEnabled} → {@code enabled}</li>
     * </ul>
     *
     * <p>首字母小写通过 ASCII 码值 +32 实现（'D'=68 → 'd'=100），
     * 假定方法名在去掉前缀后的首字符是大写字母（符合 JavaBean 命名规范）。</p>
     *
     * @param methodName getter 方法名（如 "getName"、"isActive"）
     * @return JSON 字段名（如 "name"、"active"）
     */
    public String getFieldName(String methodName) {
        if (methodName.startsWith("get")) {
            return lower(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return lower(methodName.substring(2));
        }

        return methodName;
    }

    /**
     * 将字符串首字母从大写转为小写（ASCII 码 +32）。
     *
     * <p>仅处理首字符，不处理后续字符。假定输入字符串的首字符是大写英文字母。</p>
     *
     * @param string 首字母大写的字符串
     * @return 首字母小写的字符串
     */
    public String lower(String string) {
        char[] chars = string.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 判断对象是否为"空白"（null 或空白字符串）。
     *
     * @param value 待检查的对象
     * @return true 如果为 null、空字符串或纯空白字符串
     */
    public boolean isBlank(Object value) {
        if (value == null)
            return true;

        if (value instanceof String) {
            String o = (String) value;
            if (o.isEmpty() || o.trim().isEmpty())
                return true;
        }

        return false;
    }
}

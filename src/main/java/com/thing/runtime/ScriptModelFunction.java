package com.thing.runtime;

import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.thing.common.IThingManaged;
import com.flame.type.XBaseType;
import org.graalvm.polyglot.Value;

/**
 * ThingModel的继承通过Javascript对象的__proto__的指向来实现，ThingModel的实例化成Thing通过Javascript函数的实例化实现；
 * a. 每个ThingModel类型都有一个“js原型对象”和一个“js函数”，“js函数”的prototype指向对应的“js原型对象”，“js原型对象”的__proto__属性指向父ThingModel的“js原型对象”来实现
 * ThingModel继承；
 * b. ScriptClassModel、ScriptPrototypeModel是与“js函数”、“js原型对象”对应的java数据结构，在ThingModel中定义的property、service都是被加载进ScriptPrototypeModel的
 * 数据结构中；
 * c. 通过ScriptClassModel去操作property、service都是委托给ScriptPrototypeModel去处理，ScriptClassModel的唯一用途只是为了通过ThingModel去实例化Thing，调用Thing的
 * property、service就是调用ScriptPrototypeModel中的prototype中对应的property、service；
 *
 * @author hujin
 */
public class ScriptModelFunction implements IScriptModel {
    ScriptModelPrototype prototype;
    transient Value function;

    protected void setModelPrototype(ScriptModelPrototype prototype) {
        this.prototype = prototype;
        this.function.putMember("prototype", prototype.getScriptObject());
    }

    public Value getNewInstance(IThingManaged<?> proxy) {
        return function.newInstance(proxy);
    }

    @Override
    public ScriptFunction getScriptFunction(String serviceName) throws UnknownIdentifierException {
        return this.prototype.getScriptFunction(serviceName);
    }

    @Override
    public XBaseType getPropertyType(String propName) throws UnknownIdentifierException {
        return this.prototype.getPropertyType(propName);
    }

    @Override
    public String getDefaultValue(String propName) throws UnknownIdentifierException {
        return this.prototype.getDefaultValue(propName);
    }

    @Override
    public boolean isNullable(String propName) throws UnknownIdentifierException {
        return this.prototype.isNullable(propName);
    }

    @Override
    public boolean isPersistent(String propName) throws UnknownIdentifierException {
        return this.prototype.isPersistent(propName);
    }

    @Override
    public boolean isReadOnly(String propName) throws UnknownIdentifierException {
        return this.prototype.isReadOnly(propName);
    }

    @Override
    public boolean isLogged(String propName) throws UnknownIdentifierException {
        return this.prototype.isLogged(propName);
    }

    @Override
    public boolean hasFunction(String funcName) {
        return this.prototype.hasFunction(funcName);
    }

    @Override
    public boolean hasProperty(String propName) {
        return this.prototype.hasProperty(propName);
    }
}
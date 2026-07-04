package com.thing.runtime;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Value;

import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IServiceDefinition;
import com.flame.thing.XServiceType;
import com.flame.type.XBaseType;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.object.DynamicObject;
import com.thing.ThingPerformHelper;

/**
 * ThingModel中自定义的js service、property在保存时会被加载进ScriptModelPrototype中： a. js
 * service还会被加载进javascript prototype中； b. property不会被加载进javascript
 * prototype中，运行时会动态的加载进IThingEntity实例中；
 *
 * @author hujin
 */
public class ScriptModelPrototype {
    private ScriptModelPrototype parent;
    private transient Value scriptObject;
    private String name;
    private Map<String, ScriptProperty> properties = new HashMap<>();
    private Map<String, ScriptFunction> functions = new HashMap<>();

    public Value getScriptObject() {
        return this.scriptObject;
    }

    public void setScriptObject(Value scriptObj) {
        this.scriptObject = scriptObj;
    }

    protected void setParentPrototype(ScriptModelPrototype parent) {
        this.parent = parent;
        this.scriptObject.putMember("__proto__", parent.getScriptObject());
    }

    public void etlServiceDefinition(IServiceDefinition serviceDef) {
        ScriptFunction funcModel = new ScriptFunction(serviceDef);
        if (XServiceType.Javascript.equals(funcModel.serviceType)) {
            /**
             * Javascript Service需要往js prototype中加载function
             */

            if (this.scriptObject.hasMember(funcModel.methodName)) {
                this.scriptObject.removeMember(funcModel.methodName);
                this.functions.remove(funcModel.methodName);
            }
            DynamicObject dynamicObject = ThingPerformHelper.service().genDynamicObject(serviceDef);
            this.scriptObject.putMember(funcModel.methodName, dynamicObject);
            Value protoFunc = this.scriptObject.getMember(funcModel.methodName);
            funcModel.setProtoFunction(protoFunc);
            this.functions.put(funcModel.methodName, funcModel);
        } else if (XServiceType.Remote.equals(funcModel.serviceType)) {
            /**
             * Remote Service不需要往js prototype中添加function
             */

            if (this.scriptObject.hasMember(funcModel.methodName)) {
                this.scriptObject.removeMember(funcModel.methodName);
                this.functions.remove(funcModel.methodName);
            }
            this.functions.put(funcModel.methodName, funcModel);
        }
    }

    public void etlPropertyDefinition(IPropertyDefinition propertyDef) {
        ScriptProperty propModel = new ScriptProperty(propertyDef);

        if (this.properties.containsKey(propModel.propName)) {
            this.properties.remove(propModel.propName);
        }

        this.properties.put(propModel.propName, propModel);
    }

    public ScriptFunction getScriptFunction(String serviceName) throws UnknownIdentifierException {
        ScriptFunction funcModel = this.functions.get(serviceName);

        if (funcModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(serviceName);
            } else {
                return this.parent.getScriptFunction(serviceName);
            }
        } else {
            return funcModel;
        }
    }

    public XServiceType getServiceType(String methodName) throws UnknownIdentifierException {
        ScriptFunction funcModel = this.functions.get(methodName);

        if (funcModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(methodName);
            } else {
                return this.parent.getServiceType(methodName);
            }
        } else {
            return funcModel.serviceType;
        }
    }

    public XBaseType[] getArguments(String methodName) throws UnknownIdentifierException {
        ScriptFunction funcModel = this.functions.get(methodName);

        if (funcModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(methodName);
            } else {
                return this.parent.getArguments(methodName);
            }
        } else {
            return funcModel.arguTypes;
        }
    }

    public XBaseType getResultType(String methodName) throws UnknownIdentifierException {
        ScriptFunction funcModel = this.functions.get(methodName);

        if (funcModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(methodName);
            } else {
                return this.parent.getResultType(methodName);
            }
        } else {
            return funcModel.resultType;
        }
    }

    public XBaseType getPropertyType(String propName) throws UnknownIdentifierException {
        ScriptProperty propModel = this.properties.get(propName);

        if (propModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(propName);
            } else {
                return this.parent.getPropertyType(propName);
            }
        } else {
            return propModel.propertyType;
        }
    }

    public String getDefaultValue(String propName) throws UnknownIdentifierException {
        ScriptProperty propModel = this.properties.get(propName);

        if (propModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(propName);
            } else {
                return this.parent.getDefaultValue(propName);
            }
        } else {
            return propModel.defaultValue;
        }
    }

    public boolean isNullable(String propName) throws UnknownIdentifierException {
        ScriptProperty propModel = this.properties.get(propName);

        if (propModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(propName);
            } else {
                return this.parent.isNullable(propName);
            }
        } else {
            return propModel.nullable;
        }
    }

    public boolean isPersistent(String propName) throws UnknownIdentifierException {
        ScriptProperty propModel = this.properties.get(propName);

        if (propModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(propName);
            } else {
                return this.parent.isPersistent(propName);
            }
        } else {
            return propModel.persistent;
        }
    }

    public boolean isReadOnly(String propName) throws UnknownIdentifierException {
        ScriptProperty propModel = this.properties.get(propName);

        if (propModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(propName);
            } else {
                return this.parent.isReadOnly(propName);
            }
        } else {
            return propModel.readOnly;
        }
    }

    public boolean isLogged(String propName) throws UnknownIdentifierException {
        ScriptProperty propModel = this.properties.get(propName);

        if (propModel == null) {
            if (this.parent == null) {
                throw UnknownIdentifierException.create(propName);
            } else {
                return this.parent.isLogged(propName);
            }
        } else {
            return propModel.logged;
        }
    }

    public boolean hasFunction(String funcName) {
        if (this.functions.containsKey(funcName)) {
            return true;
        } else {
            if (this.parent == null) {
                return false;
            } else {
                return this.parent.hasFunction(funcName);
            }
        }
    }

    public boolean hasProperty(String propName) {
        if (this.properties.containsKey(propName)) {
            return true;
        } else {
            if (this.parent == null) {
                return false;
            } else {
                return this.parent.hasProperty(propName);
            }
        }
    }
}

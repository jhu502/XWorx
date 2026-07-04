package com.thing.runtime;

import org.graalvm.polyglot.Value;

import com.flame.thing.Argument;
import com.flame.thing.IServiceDefinition;
import com.flame.thing.XServiceType;
import com.flame.type.XBaseType;

public class ScriptFunction {
    XServiceType serviceType;
    String methodName;
    XBaseType[] arguTypes;
    XBaseType resultType;
    transient Value protoFunc;

    ScriptFunction(IServiceDefinition serviceDef) {
        this.serviceType = serviceDef.getServiceType();
        this.methodName = serviceDef.getName();

        Argument[] paramList = serviceDef.getArguments().toArray(new Argument[0]);
        this.arguTypes = new XBaseType[paramList.length];
        for (int i = 0; i < paramList.length; i++) {
            this.arguTypes[i] = paramList[i].getBaseType();
        }
        this.resultType = serviceDef.getResultType();
    }

    public XServiceType getServiceType() {
        return this.serviceType;
    }

    public XBaseType[] getArguments() {
        return this.arguTypes;
    }

    public XBaseType getResultType() {
        return this.resultType;
    }

    public void setProtoFunction(Value func) {
        this.protoFunc = func;
    }

    public Value getProtoFunction() {
        return this.protoFunc;
    }
}

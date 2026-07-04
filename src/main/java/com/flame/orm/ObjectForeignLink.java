package com.flame.orm;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class ObjectForeignLink<T extends XPersistable> extends XObject {
    private static final long serialVersionUID = 1L;
    @Basic
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "source_id")), @AttributeOverride(name = "className", column = @Column(name = "source_classname"))})
    protected ObjectReference<T> sourceRef = null;

    public ObjectReference<T> getSourceRef() {
        return sourceRef;
    }

    public T getSouceObject() {
        return this.getSourceRef().getObject();
    }

    public void setSourceRef(ObjectReference<T> sourceRef) {
        this.sourceRef = sourceRef;
    }

    public void setSourceObject(T target) {
        this.sourceRef = new ObjectReference<T>(target);
    }

}

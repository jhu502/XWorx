package com.flame.orm;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class ObjectToObjectLink<L extends XPersistable, R extends XPersistable> extends XObject {
    private static final long serialVersionUID = 1L;
    @Basic
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "left_id")), @AttributeOverride(name = "className", column = @Column(name = "left_classname"))})
    protected ObjectReference<L> left = null;
    @Basic
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "right_id")), @AttributeOverride(name = "className", column = @Column(name = "right_classname"))})
    protected ObjectReference<R> right = null;

    public ObjectReference<L> getLeft() {
        return left;
    }

    public L getLeftObject() {
        return this.getLeft().getObject();
    }

    public void setLeft(ObjectReference<L> left) {
        this.left = left;
    }

    public void setLeftObject(L left) {
        this.left = new ObjectReference<L>(left);
    }

    public ObjectReference<R> getRight() {
        return this.right;
    }

    public R getRightObject() {
        return this.getRight().getObject();
    }

    public void setRight(ObjectReference<R> right) {
        this.right = right;
    }

    public void setRightObject(R right) {
        this.right = new ObjectReference<R>(right);
    }

}

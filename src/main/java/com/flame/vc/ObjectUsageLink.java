package com.flame.vc;

import com.flame.orm.ObjectToObjectLink;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class ObjectUsageLink<L extends IVersioned<?>, R extends IMastered> extends ObjectToObjectLink<L, R> {
	private static final long serialVersionUID = 1L;

	public IVersioned<?> getUsedBy() {
        return this.getLeftObject();
    }

    public void setUsedBy(L versioned) {
        this.setLeftObject(versioned);
    }

    public IMastered getUses() {
        return (IMastered) this.getRightObject();
    }

    public void setUses(R mastered) {
        this.setRightObject(mastered);
    }
}

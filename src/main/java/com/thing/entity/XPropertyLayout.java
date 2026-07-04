package com.thing.entity;

import org.hibernate.annotations.ColumnTransformer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.localize.AbstractLocalization;
import com.flame.orm.XConstant;
import com.flame.thing.IPropertyLayout;
import com.flame.thing.IPropertyProvider;
import com.flame.thing.LayoutType;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "XPropertyLayout", uniqueConstraints = {})
public class XPropertyLayout extends AbstractLocalization implements IPropertyLayout {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "layoutType", nullable = false)
    @Enumerated(EnumType.STRING)
    private LayoutType layoutType;
    @ColumnTransformer(write = "?::jsonb")
    @Convert(converter = MeshLayoutConverter.class)
    @Column(name = "layout", columnDefinition = XConstant.JSONB)
    private MeshLayout layout;
    @ManyToOne(targetEntity = XThingModel.class)
    @JoinColumn(name = "providerId")
    private IPropertyProvider propertyProvider; //指向属性的所属对象, 例如: XThingModel

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MeshLayout getLayout() {
        if (this.layout != null) {
            this.layout.setProvider(this.getPropertyProvider());
        }
        return this.layout;
    }

    public void setLayout(MeshLayout layout) {
        this.layout = layout;
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    @JsonIgnore
    public IPropertyProvider getPropertyProvider() {
        return propertyProvider;
    }

    public void setPropertyProvider(IPropertyProvider propertyProvider) {
        this.propertyProvider = propertyProvider;
    }
}

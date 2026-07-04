package com.thing.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnTransformer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.orm.XConstant;
import com.flame.orm.JsonArrayConverter;
import com.flame.orm.XObject;
import com.flame.thing.Argument;
import com.flame.thing.IServiceDefinition;
import com.flame.thing.IServiceProvider;
import com.flame.thing.XServiceType;
import com.flame.type.XBaseType;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author ph
 * @version 1.0
 * @created 29-10月-2019 22:20:05
 */
@Entity
@Table(name = "XServiceDefinition", uniqueConstraints = {})
public class XServiceDefinition extends XObject implements IServiceDefinition {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "name")
	private String name;
	@Basic
	@Column(name = "code", length = 4000)
	private String code;
	@Basic
	@Column(name = "serviceType", length = 50)
	@Enumerated(EnumType.STRING)
	private XServiceType serviceType;
	@Basic
	@Column(name = "description")
	private String description;
	@Basic
	@Column(name = "resultType")
	@Enumerated(EnumType.STRING)
	private XBaseType resultType;
	@ColumnTransformer(write = "?::jsonb")
    @Convert(converter = JsonArrayConverter.class)
	@Column(name = "arguments", columnDefinition = XConstant.JSONB)
	private List<Argument> arguments = new ArrayList<>();
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "providerId")
	private IServiceProvider serviceProvider;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public XServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(XServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	/**
	 * 操作集合时，只能望其集合对象中添加，不能替换集合对象，不然会报all-delete-orphan的错误
	 * 
	 * @param arguments
	 */
	public void setArguments(List<Argument> arguments) {
		this.arguments.clear();
		this.arguments.addAll(arguments);
	}

	public void addArgument(Argument argument) {
		if (argument != null)
			this.arguments.add(argument);
	}

	public XBaseType getResultType() {
		return this.resultType;
	}

	public void setResultType(XBaseType resultType) {
		this.resultType = resultType;
	}

	@JsonIgnore
	public IServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(IServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
}
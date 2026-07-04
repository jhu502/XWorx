package plm.dynamic;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnTransformer;

import com.flame.annotations.XDefinition;
import com.flame.orm.ItemEntity;
import com.flame.orm.XConstant;
import com.flame.orm.JsonArrayConverter;
import com.flame.type.XBaseType;
import com.flame.util.JsonUtils;
import com.thing.common.DefaultThing;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import plm.dynamic.bean.XChoice;
import plm.part.XPart;

@Entity
@Table(name = "XCharacteristic", uniqueConstraints = {})
@XDefinition(name = "XCharacteristic", config = DefaultThing.class, icon = "images/characteristic.png", description = "XCharacteristic", display = "Characteristic", zh_CN = "特征")
public class XCharacteristic extends ItemEntity {
	private static final long serialVersionUID = 1L;
	@Enumerated(EnumType.STRING)
	@Column(name = "basetype", length = 50)
	private XBaseType baseType;
	@Enumerated(EnumType.STRING)
	@Column(name = "optionmode", length = 50)
	private OptionMode optionMode;
	@Enumerated(EnumType.STRING)
	@Column(name = "inputtype", length = 50)
	private InputType inputType;
	@Basic
	@Column(name = "fieldmapping")
	private String fieldMapping = "";
	@Basic
	@Column(name = "dynamicmapping")
	private String dynamicMapping = "";
	@Basic
	@Column(name = "multivalue")
	private boolean multivalue = false;
	@Basic
	@Column(name = "need_quantity")
	private boolean needQuantity = false;
	@Basic
	@Column(name = "quantity")
	private double quantity = -1;
	@Basic
	@Column(name = "statement_r")
	private String requiredStatement = "";
	@Basic
	@Column(name = "statement_d")
	private String displayStatement = "";
	@Basic
	@Column(name = "statement_q")
	private String quantityStatement = "";
	@Basic
	@Column(name = "sortno")
	private int sortNo = 0;
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonArrayConverter.class)
	@Column(name = "choices", columnDefinition = XConstant.JSONB)
	private List<XChoice> choices = new ArrayList<>();
	@ManyToOne(targetEntity = XPart.class)
	@JoinColumn(name = "characted_xid", foreignKey = @ForeignKey(name = "XCHARACTED_ID_FK"))
	private ICharacted characted;

	public static XCharacteristic newCharacteristic(ICharacted characted) {
		XCharacteristic xcharact = new XCharacteristic();
		xcharact.setCharacted(characted);

		return xcharact;
	}

	public XBaseType getBaseType() {
		return this.baseType;
	}

	public void setBaseType(XBaseType baseType) {
		this.baseType = baseType;
	}

	public OptionMode getOptionMode() {
		return this.optionMode;
	}

	public void setOptionMode(OptionMode optionmodel) {
		this.optionMode = optionmodel;
	}

	public InputType getInputType() {
		return this.inputType;
	}

	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}

	public String getFieldMapping() {
		return fieldMapping;
	}

	public void setFieldMapping(String fieldMapping) {
		this.fieldMapping = fieldMapping;
	}

	public String getRequiredStatement() {
		return this.requiredStatement;
	}

	public void setRequiredStatement(String requiredStatement) {
		this.requiredStatement = requiredStatement;
	}

	public String getDisplayStatement() {
		return this.displayStatement;
	}

	public void setDisplayStatement(String displayStatement) {
		this.displayStatement = displayStatement;
	}

	public String getDynamicMapping() {
		return dynamicMapping;
	}

	public void setDynamicMapping(String dynamicMapping) {
		this.dynamicMapping = dynamicMapping;
	}

	public boolean isMultivalue() {
		return this.multivalue;
	}

	public void setMultivalue(boolean multivalue) {
		this.multivalue = multivalue;
	}

	public boolean isNeedQuantity() {
		return this.needQuantity;
	}

	public void setNeedQuantity(boolean bool) {
		this.needQuantity = bool;
	}

	public double getQuantity() {
		return this.quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getQuantityStatement() {
		return this.quantityStatement;
	}

	public void setQuantityStatement(String quantityStatement) {
		this.quantityStatement = quantityStatement;
	}

	public int getSortNo() {
		return sortNo;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public List<XChoice> getChoices() {
		return this.choices;
	}

	public void addChoice(XChoice choice) {
		this.choices.add(choice);
	}

	public void setChoices(List<XChoice> choices) {
		this.choices = choices;
	}

	public String getChoiceJSON() {
		return JsonUtils.toJsonString(this.choices);
	}

	public ICharacted getCharacted() {
		return this.characted;
	}

	public void setCharacted(ICharacted characted) {
		this.characted = characted;
	}

}

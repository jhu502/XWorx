package plm.part;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import javax.vecmath.Matrix3d;

import plm.three.EulerAngles;
import plm.three.RotationMatrix;
import com.flame.orm.XObject;

@Entity
@Table(name = "XPartUsesOccurrence", uniqueConstraints = {})
public class XPartUsesOccurrence extends XObject {
	private static final long serialVersionUID = 1L;
	private transient EulerAngles eulerAngles;
	@ManyToOne(targetEntity = XPartUsageLink.class)
	@JoinColumn(name = "usage_xid", foreignKey = @ForeignKey(name = "XPARTUSAGELINK_ID_FK"))
	protected XPartUsageLink usageLink;
	@Basic
	@Column(name = "componentId")
	private String componentId = "";
	@Basic
	@Column(name = "r1_0")
	private Double rotation1_0;
	@Basic
	@Column(name = "r1_1")
	private Double rotation1_1;
	@Basic
	@Column(name = "r1_2")
	private Double rotation1_2;
	@Basic
	@Column(name = "r2_0")
	private Double rotation2_0;
	@Basic
	@Column(name = "r2_1")
	private Double rotation2_1;
	@Basic
	@Column(name = "r2_2")
	private Double rotation2_2;
	@Basic
	@Column(name = "r3_0")
	private Double rotation3_0;
	@Basic
	@Column(name = "r3_1")
	private Double rotation3_1;
	@Basic
	@Column(name = "r3_2")
	private Double rotation3_2;
	@Basic
	@Column(name = "t1")
	private Double translation0;
	@Basic
	@Column(name = "t2")
	private Double translation1;
	@Basic
	@Column(name = "t3")
	private Double translation2;

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public Double getRotation1_0() {
		return rotation1_0;
	}

	public void setRotation1_0(Double rotation1_0) {
		this.rotation1_0 = rotation1_0;
	}

	public Double getRotation1_1() {
		return rotation1_1;
	}

	public void setRotation1_1(Double rotation1_1) {
		this.rotation1_1 = rotation1_1;
	}

	public Double getRotation1_2() {
		return rotation1_2;
	}

	public void setRotation1_2(Double rotation1_2) {
		this.rotation1_2 = rotation1_2;
	}

	public Double getRotation2_0() {
		return rotation2_0;
	}

	public void setRotation2_0(Double rotation2_0) {
		this.rotation2_0 = rotation2_0;
	}

	public Double getRotation2_1() {
		return rotation2_1;
	}

	public void setRotation2_1(Double rotation2_1) {
		this.rotation2_1 = rotation2_1;
	}

	public Double getRotation2_2() {
		return rotation2_2;
	}

	public void setRotation2_2(Double rotation2_2) {
		this.rotation2_2 = rotation2_2;
	}

	public Double getRotation3_0() {
		return rotation3_0;
	}

	public void setRotation3_0(Double rotation3_0) {
		this.rotation3_0 = rotation3_0;
	}

	public Double getRotation3_1() {
		return rotation3_1;
	}

	public void setRotation3_1(Double rotation3_1) {
		this.rotation3_1 = rotation3_1;
	}

	public Double getRotation3_2() {
		return rotation3_2;
	}

	public void setRotation3_2(Double rotation3_2) {
		this.rotation3_2 = rotation3_2;
	}

	public void setRotation(Double[] rotation) {
		this.rotation1_0 = rotation[0];
		this.rotation1_1 = rotation[1];
		this.rotation1_2 = rotation[2];
		this.rotation2_0 = rotation[3];
		this.rotation2_1 = rotation[4];
		this.rotation2_2 = rotation[5];
		this.rotation3_0 = rotation[6];
		this.rotation3_1 = rotation[7];
		this.rotation3_2 = rotation[8];
	}

	public Double getTranslation0() {
		return translation0;
	}

	public void setTranslation0(Double translation0) {
		this.translation0 = translation0;
	}

	public Double getTranslation1() {
		return translation1;
	}

	public void setTranslation1(Double translation1) {
		this.translation1 = translation1;
	}

	public Double getTranslation2() {
		return translation2;
	}

	public void setTranslation2(Double translation2) {
		this.translation2 = translation2;
	}

	public Double[] getTranslation() {
		return new Double[] { this.translation0, this.translation1, this.translation2 };
	}

	public void setTranslation(Double[] translation) {
		this.translation0 = translation[0];
		this.translation1 = translation[1];
		this.translation2 = translation[2];
	}

	public Double[] getRotation() {
		return new Double[] { this.rotation1_0, this.rotation1_1, this.rotation1_2, this.rotation2_0, this.rotation2_1, this.rotation2_2, this.rotation3_0, this.rotation3_1, this.rotation3_2 };
	}

	public Matrix3d getMatrix3d() {
		return new Matrix3d(this.rotation1_0, this.rotation1_1, this.rotation1_2, this.rotation2_0, this.rotation2_1, this.rotation2_2, this.rotation3_0, this.rotation3_1, this.rotation3_2);
	}

	public EulerAngles getEulerAngles() {
		if (this.eulerAngles == null) {
			this.eulerAngles = (new RotationMatrix(this.getMatrix3d())).toEulerAngles();
		}

		return this.eulerAngles;
	}
}

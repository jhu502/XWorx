package plm.dynamic;

import java.util.List;

import org.hibernate.annotations.ColumnTransformer;

import com.flame.orm.ItemEntity;
import com.flame.orm.XConstant;
import com.flame.orm.JsonObjectConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import plm.dynamic.bean.XCaseHead;
import plm.dynamic.bean.XCaseRows;
import plm.part.XPart;

@Entity
@Table(name = "XCaseTable", uniqueConstraints = {})
public class XCaseTable extends ItemEntity {
	private static final long serialVersionUID = 1L;
	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 50)
	private CaseType type = CaseType.FLATTABLE;
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonObjectConverter.class)
	@Column(name = "head", columnDefinition = XConstant.JSONB)
	private XCaseHead head = new XCaseHead();
	@ColumnTransformer(write = "?::jsonb")
	@Convert(converter = JsonObjectConverter.class)
	@Column(name = "rows", columnDefinition = XConstant.JSONB)
	private XCaseRows rows = new XCaseRows();
	@ManyToOne(targetEntity = XPart.class)
	@JoinColumn(name = "partId", foreignKey = @ForeignKey(name = "PART_CASETABLE_FK"))
	private XPart part;

	public static XCaseTable newXCaseTable(XPart xpart) {
		XCaseTable caseTable = new XCaseTable();
		caseTable.setPart(xpart);

		return caseTable;
	}

	public CaseType getType() {
		return type;
	}

	public void setType(CaseType caseType) {
		this.type = caseType;
	}

	public XCaseHead getHead() {
		return head;
	}

	public void setHead(XCaseHead caseHead) {
		this.head = caseHead;
	}

	public XCaseRows getRows() {
        return rows;
    }

    public void setRows(XCaseRows rows) {
        this.rows = rows;
    }

    public void addCaseRow(List<Object> row) {
		this.rows.addRow(row);
	}

	public XPart getPart() {
		return this.part;
	}

	public void setPart(XPart part) {
		this.part = part;
	}

}

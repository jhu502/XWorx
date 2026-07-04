package plm.doc;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.vc.Master;

@Entity
@Table(name = "XDocumentMaster", uniqueConstraints = {})
public class XDocumentMaster extends Master {
	private static final long serialVersionUID = 1L;
}

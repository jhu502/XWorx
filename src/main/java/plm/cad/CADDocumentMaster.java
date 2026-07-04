package plm.cad;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.vc.Master;

@Entity
@Table(name = "CADDocumentMaster", uniqueConstraints = {})
public class CADDocumentMaster extends Master {
	private static final long serialVersionUID = 1L;
}

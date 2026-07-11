package xw.doc;

import com.flame.orm.ObjectReference;
import com.flame.vc.ObjectUsageLink;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "XDocumentUsageLink", uniqueConstraints = {})
public class XDocumentUsageLink extends ObjectUsageLink<XDocument, XDocumentMaster> {
	private static final long serialVersionUID = 1L;

	public static XDocumentUsageLink newInstance(XDocument document, XDocumentMaster master) {
		XDocumentUsageLink usageLink = new XDocumentUsageLink();
		usageLink.setUsedBy(document);
		usageLink.setUses(master);
		return usageLink;
	}
}

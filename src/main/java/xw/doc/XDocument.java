package xw.doc;

import com.flame.annotations.XDefinition;
import com.thing.common.DefaultThing;
import xw.vc.VersionControlled;

import jakarta.persistence.*;

@Entity
@Table(name = "XDocument", uniqueConstraints = {})
@XDefinition(name = "XDocument", config = DefaultThing.class, icon = "images/doc.gif", description = "Document", display = "Document", en_US = "Document", zh_CN = "文档")
public class XDocument extends VersionControlled<XDocumentMaster> {
    private static final long serialVersionUID = 1L;
    @ManyToOne(targetEntity = XDocumentMaster.class)
    @JoinColumn(name = "masterId", foreignKey = @ForeignKey(name = "MASTER_ID_FK"))
    protected XDocumentMaster master;

    private XDocument(XDocumentMaster master) {
        this.setMaster(master);
    }

    public static XDocument newInstance(String number, String name) {
        XDocumentMaster master = new XDocumentMaster();
        XDocument document = new XDocument(master);
        document.setNumber(number);
        document.setName(name);

        return document;
    }

    public XDocument() {
    }

    @Override
    public XDocumentMaster getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(XDocumentMaster master) {
        this.master = master;
    }
}

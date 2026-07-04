package xw.action.entity;

import xw.auths.entity.XUser;
import com.flame.orm.XObject;

import jakarta.persistence.*;

@Entity
@Table(name = "XFavoriteFolder", uniqueConstraints = {})
public class XFavoriteFolder extends XObject {
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "name")
    private String name = "";
    @ManyToOne
    @JoinColumn(name = "upperFolder", foreignKey = @ForeignKey(name = "UPPER_FOLDER_FK"))
    private XFavoriteFolder upperFolder;
    @ManyToOne
    @JoinColumn(name = "creatorId", foreignKey = @ForeignKey(name = "CREATOR_ID_FK"))
    private XUser creator;

    public static XFavoriteFolder newFavoriteFolder() {
        XFavoriteFolder favorFolder = new XFavoriteFolder();

        return favorFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public XFavoriteFolder getUpperFolder() {
        return upperFolder;
    }

    public void setUpperFolder(XFavoriteFolder upperFolder) {
        this.upperFolder = upperFolder;
    }

    public XUser getCreator() {
        return creator;
    }

    public void setCreator(XUser creator) {
        this.creator = creator;
    }
}

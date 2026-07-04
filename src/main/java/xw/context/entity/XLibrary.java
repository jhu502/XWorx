package xw.context.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.flame.annotations.XDefinition;
import com.thing.common.DefaultThing;

import xw.context.IContained;
import xw.team.entity.XContainerTeam;

@Entity
@Table(name = "XLibrary", uniqueConstraints = {})
@XDefinition(name = "XLibrary", config = DefaultThing.class, icon = "images/library.png", description = "XLibrary", display = "Library", en_US = "Library", zh_CN = "库上下文")
public class XLibrary extends Container implements IContained<XOrganization> {
	private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.EAGER) // EAGER：立即加载(也可设为LAZY懒加载)
    @JoinColumn(name = "libraryType", referencedColumnName = "name", nullable = false)
    private LibraryTypeRB libraryType;
    @ManyToOne(targetEntity = XOrganization.class)
    @JoinColumn(name = "containerId", nullable = false, foreignKey = @ForeignKey(name = "CONTAINER_ID_FK"))
    private XOrganization container;

    public static XLibrary newLibrary(String number, String name, String description, XOrganization xorg) {
        XLibrary library = new XLibrary();
        library.setNumber(number);
        library.setName(name);
        library.setDescription(description);
        library.setContainer(xorg);
        XContainerTeam team = XContainerTeam.newInstance();
        library.setTeam(team);

        return library;
    }

    public XOrganization getContainer() {
        return container;
    }

    public void setContainer(XOrganization container) {
        this.container = container;
    }

    public LibraryTypeRB getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(LibraryTypeRB libraryType) {
        this.libraryType = libraryType;
    }
}

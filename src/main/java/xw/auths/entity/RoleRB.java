package xw.auths.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.config.JPAConfiguration;
import com.flame.localize.AbstractEnumerated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * RoleRB - 角色枚举实体。
 *
 */
@Entity
@Table(name = "RoleRB", uniqueConstraints = {})
public class RoleRB extends AbstractEnumerated<RoleRB> {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType = RoleType.ROLE;

    @Column(name = "responsibility", length = 500)
    private String responsibility;

    /**
     * 角色类型枚举 - 区分不同类型的角色
     */
    public enum RoleType {
        AGENT("Agent", "智能代理角色"),
        ROLE("Role", "业务功能角色"),
        SERVICE("Service", "服务角色"),
        SYSTEM("System", "系统角色");

        private final String name;
        private final String description;

        RoleType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    @JsonIgnore
    public static RoleRB[] getRoleRBSet() {
        List<RoleRB> roleRBList = JPAConfiguration.toRBTypeList(RoleRB.class);
        return roleRBList.toArray(new RoleRB[0]);
    }

    @JsonIgnore
    public static RoleRB[] getRoleRBSet(RoleType roleType) {
        List<RoleRB> roleRBList = JPAConfiguration.toRBTypeList(RoleRB.class, "roleType", roleType);
        return roleRBList.toArray(new RoleRB[0]);
    }

    public static RoleRB toRoleRB(String name) {
        return JPAConfiguration.toRBType(RoleRB.class, name);
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
    
    public boolean isRoleType(RoleType roleType) {
        return this.roleType == roleType;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }
}
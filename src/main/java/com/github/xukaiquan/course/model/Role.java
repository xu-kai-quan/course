package com.github.xukaiquan.course.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role",schema = "public")
public class Role extends BaseEntity{
    private String name;

    @Column(name = "name",nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Set<Permission> permissions;

    @OneToMany
    @JoinTable(
            name = "permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}

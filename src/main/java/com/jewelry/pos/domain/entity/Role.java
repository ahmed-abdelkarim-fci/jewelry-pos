package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "roles") // 'role' is often a reserved word
public class Role extends Auditable {

    @Id @Tsid @Column(length = 26)
    private String id;

    @Column(unique = true, nullable = false)
    private String name; // e.g., "ROLE_ADMIN", "ROLE_CLERK"

    @ManyToMany(fetch = FetchType.EAGER) // Eager fetch for Security
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }
}
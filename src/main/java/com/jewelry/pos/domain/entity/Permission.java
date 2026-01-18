package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "permission")
public class Permission extends Auditable {

    @Id @Tsid @Column(length = 26)
    private String id;

    @Column(unique = true, nullable = false)
    private String name; // e.g., "PRODUCT_CREATE", "SALE_VOID"

    private String description;

    public Permission(String name) {
        this.name = name;
    }
}
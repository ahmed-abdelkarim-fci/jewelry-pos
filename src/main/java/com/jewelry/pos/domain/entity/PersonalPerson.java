package com.jewelry.pos.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "personal_person")
@Getter @Setter
public class PersonalPerson extends Auditable {

    @Id @Tsid
    @Column(length = 26)
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 50)
    private String phoneNumber;

    @Column(length = 500)
    private String address;

    @Column(length = 1000)
    private String notes;
}

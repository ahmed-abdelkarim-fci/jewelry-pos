package com.jewelry.pos.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "system_setting")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting {

    @Id
    @Column(name = "setting_key", length = 50)
    private String key;

    @Column(name = "setting_value")
    private String value;
}
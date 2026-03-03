package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.PersonalPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalPersonRepository extends JpaRepository<PersonalPerson, String> {
    Optional<PersonalPerson> findByName(String name);
}

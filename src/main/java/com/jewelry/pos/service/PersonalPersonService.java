package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.PersonalPerson;
import com.jewelry.pos.domain.repository.PersonalPersonRepository;
import com.jewelry.pos.web.dto.PersonalPersonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalPersonService {

    private final PersonalPersonRepository personalPersonRepository;

    @Transactional
    public PersonalPerson createPerson(PersonalPersonDTO dto) {
        PersonalPerson person = new PersonalPerson();
        person.setName(dto.name());
        person.setPhoneNumber(dto.phoneNumber());
        person.setAddress(dto.address());
        person.setNotes(dto.notes());
        return personalPersonRepository.save(person);
    }

    @Transactional
    public PersonalPerson updatePerson(String id, PersonalPersonDTO dto) {
        PersonalPerson person = personalPersonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));
        person.setName(dto.name());
        person.setPhoneNumber(dto.phoneNumber());
        person.setAddress(dto.address());
        person.setNotes(dto.notes());
        return personalPersonRepository.save(person);
    }

    @Transactional(readOnly = true)
    public List<PersonalPerson> getAllPersons() {
        return personalPersonRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PersonalPerson getPersonById(String id) {
        return personalPersonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));
    }

    @Transactional
    public void deletePerson(String id) {
        personalPersonRepository.deleteById(id);
    }
}

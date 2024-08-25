package com.moinul.LostAndFound.repository;

import com.moinul.LostAndFound.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    public Person findByFullName(String fullName);
}

package com.moinul.LostAndFound.repository;

import com.moinul.LostAndFound.model.Person;
import com.moinul.LostAndFound.model.PersonImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PersonImageRepository extends JpaRepository<PersonImage, Long> {

}

package com.veselov.libraryproject.LibraryProjectBoot.repositories;

import com.veselov.libraryproject.LibraryProjectBoot.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleRepository  extends JpaRepository<Person,Integer> {

}

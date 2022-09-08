package com.veselov.libraryproject.LibraryProjectBoot.services;

import com.veselov.libraryproject.LibraryProjectBoot.model.Book;
import com.veselov.libraryproject.LibraryProjectBoot.model.Person;
import com.veselov.libraryproject.LibraryProjectBoot.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll(){
        return peopleRepository.findAll();
    }
    public Person findById(int id){
        return peopleRepository.findById(id).orElse(null);
    }
    @Transactional
    public void save(Person person){
        peopleRepository.save(person);
    }
    @Transactional
    public void update(int id, Person updatedPerson){
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }
    @Transactional
    public void delete(int id){
        peopleRepository.deleteById(id);
    }

    public List<Book> getBooksByPersonId(int id){
        Optional<Person> person = peopleRepository.findById(id);
        if(person.isPresent()){
            Hibernate.initialize(person.get().getBooks());
            List<Book> books = person.get().getBooks();
            books.forEach(this::checkOverdue);
            return books;
        }
        else{
            return Collections.emptyList();
        }
    }

    private void checkOverdue(Book book){
        Date currentDate = new Date();
        Date bookDate = book.getAssignAt();
        long diff = (currentDate.getTime()-bookDate.getTime())/1000/60/60/24;
        book.setOverdue(diff > 10);
    }

}

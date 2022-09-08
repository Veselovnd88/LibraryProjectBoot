package com.veselov.libraryproject.LibraryProjectBoot.services;

import com.veselov.libraryproject.LibraryProjectBoot.model.Book;
import com.veselov.libraryproject.LibraryProjectBoot.model.Person;
import com.veselov.libraryproject.LibraryProjectBoot.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

        private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(){
        return booksRepository.findAll();
    }

    public List<Book> findAll(Pageable pageable){
        return booksRepository.findAll(pageable).getContent();
    }
    public List<Book> findAll(Sort sort){
        return booksRepository.findAll(sort);
    }
    public int getTotalPages(Pageable pageable){
        return booksRepository.findAll(pageable).getTotalPages();
    }


    public Book findByNameStartsWith(String part){
        return booksRepository.findByNameStartsWith(part);
    }


    public Book findById(int id){
        return booksRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Book book){
        booksRepository.save(book);
    }
    @Transactional
    public void update(int id, Book updatedBook){
            updatedBook.setId(id);
            booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id){
        booksRepository.deleteById(id);
    }

    @Transactional
    public void assignPerson(Person person, int id){
       Optional<Book> receivedBook = booksRepository.findById(id);
       if(receivedBook.isPresent()){
        Book book =  receivedBook.get();
           book.setAssignAt(new Date());
           book.setOwner(person);
       }

    }
    /*Проверка просрочки книги*/

    @Transactional
    public void releasePerson(int id){
        Book book =  booksRepository.findById(id).orElse(null);
        book.setOwner(null);
        book.setAssignAt(null);

    }

}

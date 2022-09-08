package com.veselov.libraryproject.LibraryProjectBoot.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    @NotEmpty(message = "Поле названия не может быть пустым")
    @Size(min = 0, message = "Длина должна быть больше 0")
    private String name;
    @Column(name = "author")
    @NotEmpty(message = "Поле автора не может быть пустым")
    @Size(min = 0, message = "Длина должна быть больше 0")
    private String author;
    @Column(name = "year")
    @Min(value = 1, message = "Год должен быть больше 0")
    private int year;

    @ManyToOne
    @JoinColumn(name = "person_id",referencedColumnName = "person_id")
    private Person owner;

    @Column(name = "assign_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignAt;
    @Transient
    private boolean isOverdue;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Book(){

    }

    public Book(int id, String name, String author, Person owner) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public Date getAssignAt() {
        return assignAt;
    }

    public void setAssignAt(Date assignAt) {
        this.assignAt = assignAt;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }

}

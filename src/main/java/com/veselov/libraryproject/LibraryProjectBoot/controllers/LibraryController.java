package com.veselov.libraryproject.LibraryProjectBoot.controllers;

import com.veselov.libraryproject.LibraryProjectBoot.model.Person;
import com.veselov.libraryproject.LibraryProjectBoot.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/people")
public class LibraryController {
    private final PeopleService peopleService;
    @Autowired
    public LibraryController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping
    public String index(Model model){
        model.addAttribute("people", peopleService.findAll());
        return "/people/index";
    }


    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model
            model){//получает из запроса ID
        Person person = peopleService.findById(id);
        model.addAttribute(person);
        model.addAttribute("books",peopleService.getBooksByPersonId(person.getId()));
        return "people/show";
    }

    @GetMapping("/new")
    //при отправке ГЕТ на этот адрес он отдаем форму нью
    public String newPerson(@ModelAttribute("person") Person person){
        /*передавая через модель аттрибьют персон - спринг сам создает
        * нам экземпляр класса, куда мы потом через пост будет писать данные*/
        return "people/new";
    }
    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "people/new";
        }
        peopleService.save(person);
             return "redirect:/people";
    }

    @GetMapping("/{id}/edit")//отдаем форму для редактирования
    public String edit(@PathVariable("id") int id, Model model){
        model.addAttribute("person", peopleService.findById(id));
        return "people/edit";
    }

    @PatchMapping("/{id}")//сюда отправляет Patch(POST)
    public String update(@PathVariable("id") int id, @ModelAttribute("person")
    @Valid Person person, BindingResult bindingResult ){
        if(bindingResult.hasErrors()){
            return "people/edit";
        }
        peopleService.update(id,person);
        //  обновляем данные - передаем айди и новые данные
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    //делит маппинг - указываем с какой страницы будет отправлен этот запрос
    public String delete(@PathVariable("id") int id){
        //пас варибл ловит из строки id и передает в метод
        peopleService.delete(id);
        return "redirect:/people";
    }

}

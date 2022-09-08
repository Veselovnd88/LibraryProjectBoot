package com.veselov.libraryproject.LibraryProjectBoot.controllers;

import com.veselov.libraryproject.LibraryProjectBoot.model.Book;
import com.veselov.libraryproject.LibraryProjectBoot.model.Person;
import com.veselov.libraryproject.LibraryProjectBoot.services.BooksService;
import com.veselov.libraryproject.LibraryProjectBoot.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {
    private final PeopleService peopleService;
    private final BooksService booksService;

    @Autowired
    public BookController (PeopleService peopleService, BooksService booksService) {
        this.peopleService = peopleService;
        this.booksService = booksService;

    }

    /*Получение всех книг из бд;
    * books - отдает все книги полученные из бд;
    * Пагинация - при передаче параметров в адресной строке - выводит как просят;
    * Если ничего не передается, то выводим все книги;
    * Сортировка - передается отдельным ключом в адресной строке - работает как с пагинацией, так и без*/
    @GetMapping
    public String index(Model model,@RequestParam(value = "page",required = false) Integer page,
                        @RequestParam(value = "books_per_page",required = false) Integer books_per_page,
                        @RequestParam(value = "sort_by_year",required = false) Boolean sort_by_year){
        String returnPage = "/books/index";
        if(page==null||books_per_page==null){
            if(sort_by_year==null|| !sort_by_year){
                model.addAttribute("books", booksService.findAll());
            }
            else {
            model.addAttribute("books", booksService.findAll(Sort.by("year")));
            }
            return returnPage;
        }
        Pageable pageable;
        if(sort_by_year==null||!sort_by_year){
            pageable = PageRequest.of(page,books_per_page);}
        else{
            pageable =PageRequest.of(page,books_per_page,Sort.by("year"));}
        model.addAttribute("books", booksService.findAll(pageable));
        return returnPage;
    }
    /*Вывод информации по конкретной книге с указанием ее владельца:
    * pathVariable - идентификатор книги, которая отображается в строке,
    * Получаем владельца книги - передаем его в модель, если null - то таймлиф предложит выбрать,если нет, то покажет
    * "book" - сам объект книги, для отображения ее полей на странице
    * "persons" - список людей, который будет выведен в выпадающем списке
    * "person" новый объект Person, который будет служить dto для передачи выбранного Id владельца */
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model
            model){//получает из запроса ID
        Person person=booksService.findById(id).getOwner();
        model.addAttribute("owner",person);
        model.addAttribute("book",booksService.findById(id));
        model.addAttribute("persons",peopleService.findAll());
        Person personToAssign = new Person();
        model.addAttribute("person",personToAssign);
        return "books/show";
    }
    /*Назначение владельца книги:
    * person - передаем в модельАтрибьют объект владельца которому присвоили Id в методе патч
    * Забираем книгу по заданному Id, устанавливаем ей нового владельца,
    * и обновляем нашу книгу через сервис*/
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, Model
          model,@ModelAttribute("person") Person person){
          booksService.assignPerson(person,id);
          return "redirect:/books/"+id;
    }
    /*Удаление владельца книги:
    * При отправке метода Patch по указанному адресу передается PathVariable - id книги;
    * Сервис устанавливает владельца книги на null*/
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id, Model
            model){
        booksService.releasePerson(id);
        return "redirect:/books/"+id;
    }

    /*Создание книги:
    * При отправке ГЕТ запрос отдает форму для создания новой книги;
    * Передается объект book через ModelAttribute для создания объекта и его заполнения*/
    @GetMapping("/new")
    public String newPerson(@ModelAttribute("book") Book book){
        return "books/new";
    }
    /*Создание книги:
    * При отправки ПОСТ метода на общий адрес создает заполненный данным объект book;
    * Данные из метода пост попадают в объект book;
    * Происходит валидация, при ошибках возвращает ту же страницу;
    * Сервис обращается к бд сохраняя объект*/
    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "books/new";
        }
        booksService.save(book);
        return "redirect:/books";
    }
    /*Редактирование книги:
    * При отправки Гет метода по адресу с передачей в адресной строке PathVariable,
    * отдает форму для редактирования объекта;
    * Сервис ищет в бд объект книги с заданным Id и передает ее в модель для обработки таймлифом*/
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model){
        model.addAttribute("book", booksService.findById(id));
        return "books/edit";
    }
    /*Обновление книги
    * При отправке метода Пэтч по данному адресу контроллер отправляет в этот метод;
    * Принимается PathVariable через адресную строку и передается объект book в модели;
    * Также происходит валидация объекта книги, при появлении ошибок возвращает ту же страницу;
    * Происходит обновление книги через сервис и метод update, куда мы передаем id книги которую нужно обновить
    * и книгу данные которой нужно установить*/
    @PatchMapping("/{id}")//сюда отправляет Patch(POST)
    public String update(@PathVariable("id") int id, @ModelAttribute("book")
    @Valid Book book,BindingResult bindingResult ){
        if(bindingResult.hasErrors()){
            return "books/edit";
        }
        booksService.update(id,book);
        return "redirect:/books";
    }
    /*Удаление книги по Id:
    * При отправке метода DELETE по данному адресу контроллер отправляет в этот метод;
    * Параметр приходит из адресной строки в PathVariable;
    * */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        booksService.delete(id);
        return "redirect:/books";
    }
    /*Поиск книги по первым буквам:
    * При запросе без параметров - просто отдает форму с полем для поиска;
    * С формы с input-text приходит requestParam через параметры;
    * Мы проверяем если запрос query null или пустая строка, то передаем в таймлиф checkParam=null;
    * В таймлифе идет проверка - если нулл - то показывает просто форму;
    * Если есть запрос - то сервис ищет в бд подходящие варианты и отдает нам объект книги;
    * Дальше таймлиф сам проверяет - если книга есть - то пишет ее автора, или что автора нет,
    * и если книги нет (объект=нулл) то сообщает что книга не найдена*/
    @GetMapping("/search")
    public String search(Model model,@RequestParam(value = "query",required = false) String query){
       String returnPage = "/books/search";
        Book book=null;
        if (query != null) {
            if(query.equals("")){
                query=null;
            }else{
            book=booksService.findByNameStartsWith(query);}
        }
        model.addAttribute("check",query);
        model.addAttribute("book",book);
        return returnPage;
    }

}

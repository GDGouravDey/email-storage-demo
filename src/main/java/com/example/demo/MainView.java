package com.example.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
    private final PersonRepository repository;
    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final EmailField email = new EmailField("Email");
    private final Binder<Person> binder = new Binder<>(Person.class);
    private final Grid<Person> grid = new Grid<>(Person.class);
    public MainView(PersonRepository repository) {
        this.repository = repository;
        grid.setColumns("firstName","lastName","email");
        add(getForm(),grid);
        refreshGrid();
    }

    private Component getForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        var addButton = new Button("Add");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(firstName,lastName,email,addButton);
        binder.bindInstanceFields(this);
        grid.addComponentColumn(this::createDeleteButton);
        addButton.addClickListener(click -> {
            try{
                var person = new Person();
                binder.writeBean(person);
                if (isNotBlank(person.getFirstName()) && isNotBlank(person.getLastName()) && isNotBlank(person.getEmail())) {
                    repository.save(person);
                    binder.readBean(new Person());
                    refreshGrid();
                }
                else {
                    Notification.show("Please enter data to all the given fields.");
                }
            } catch (ValidationException e) {
                //
            }
        });
        return layout;
    }
    private boolean isNotBlank(String str) {
        return str!=null && !str.trim().isEmpty();
    }
    private void refreshGrid() {
        grid.setItems(repository.findAll());
    }
    private Button createDeleteButton(Person person) {
        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            repository.delete(person);
            refreshGrid();
        });
        return deleteButton;
    }
}

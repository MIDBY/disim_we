package it.univaq.example.webmarket.data.model.impl;

import it.univaq.example.webmarket.data.model.Author;
import it.univaq.framework.data.DataItemImpl;

public class AuthorImpl extends DataItemImpl<Integer> implements Author {

    private String name;
    private String surname;

    public AuthorImpl() {
        super();
        name = "";
        surname = "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setSurname(String surname) {
        this.surname = surname;
    }

}
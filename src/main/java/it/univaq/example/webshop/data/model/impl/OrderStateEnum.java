package it.univaq.example.webshop.data.model.impl;

public enum OrderStateEnum {
    EMPTY, ACCETTATO, RESPINTONONCONFORME, RESPINTONONFUNZIONANTE;

    @Override
    public String toString() {
        return this == EMPTY ? "" : this.name();
    }
}

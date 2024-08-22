package it.univaq.example.webshop.data.model.impl;

public enum OrderStateEnum {
    ACCETTATO, RESPINTONONCONFORME, RESPINTONONFUNZIONANTE, EMPTY;

    @Override
    public String toString() {
        return this == EMPTY ? "" : this.name();
    }
}

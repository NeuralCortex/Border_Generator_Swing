package com.fx.swing.pojo;

public class TableHeaderPOJO {

    private final String name;
    private final Class klasse;

    public TableHeaderPOJO(String name, Class klasse) {
        this.name = name;
        this.klasse = klasse;
    }

    public String getName() {
        return name;
    }

    public Class getKlasse() {
        return klasse;
    }
}

package com.example.smartpot;

import java.util.Arrays;

public class Dictionary {

    private String name;
    private String image;
    private String[] attrs;
    private String[] values;

    public Dictionary() {
    }

    public Dictionary(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Dictionary(String[] attrs, String[] values) {
        this.attrs = attrs;
        this.values = values;
    }

    public Dictionary(String name, String image, String[] attrs, String[] values) {
        this.name = name;
        this.image = image;
        this.attrs = attrs;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getAttrs() {
        return attrs;
    }

    public void setAttrs(String[] attrs) {
        this.attrs = attrs;
    }

    public String[] getValues() {
        return values;
    }
    public void setValues(String[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", attrs=" + Arrays.toString(attrs) +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}

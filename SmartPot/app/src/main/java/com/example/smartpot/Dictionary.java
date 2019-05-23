package com.example.smartpot;

public class Dictionary {

    private String name;
    private String image;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public Dictionary(String name, String image, String content) {
        this.name = name;
        this.image = image;
        this.content = content;
    }
}

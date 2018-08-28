package com.cxp.personalmanage.pojo;

public class Student {
    private Integer id;
    private String student_id;
    private String name;
    private String class_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", student_id='" + student_id + '\'' +
                ", name='" + name + '\'' +
                ", class_id='" + class_id + '\'' +
                '}';
    }
}

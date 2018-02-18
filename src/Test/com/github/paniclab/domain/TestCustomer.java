package com.github.paniclab.domain;

import java.util.Objects;

public class TestCustomer implements Entity<Long> {

    private Long id;
    private String name;
    private int age;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public TestCustomer getThis() {
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.hashCode() != this.hashCode()) return false;
        if (obj == this) return true;

        if (!(obj instanceof TestCustomer)) return false;

        TestCustomer other = TestCustomer.class.cast(obj);
        if (!(Objects.equals(this.getId(), other.getId()))) return false;
        if (this.getAge() != other.getAge()) return false;
        if (!(Objects.equals(this.getName(), other.getName()))) return false;
        return true;
    }
}

package com.github.paniclab.domain;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void equals_twoEmptyInstances_returnTrue() {

        TestCustomer one = new TestCustomer();
        TestCustomer another = new TestCustomer();

        assertTrue(Objects.equals(one, another));
    }

    @Test
    void equals_twoNotEmptyTransientInstancesEqualsByValue_returnTrue() {
        TestCustomer one = new TestCustomer();
        one.setName("Vlad");
        one.setAge(27);

        TestCustomer another = new TestCustomer();
        another.setName("Vlad");
        another.setAge(27);

        assertTrue(Objects.equals(one, another));
    }

    @Test
    void equals_TwoNotEmptyTransientInstancesNotEqualsByValue_returnFalse() {
        TestCustomer one = new TestCustomer();
        one.setName("Vlad");
        one.setAge(27);

        TestCustomer another = new TestCustomer();
        another.setName("Vlad");
        another.setAge(28);

        assertFalse(Objects.equals(one, another));
    }
}
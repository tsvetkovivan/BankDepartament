package com.tsvetkov.bd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void setAge() {
        Person person = new Person();

        try {
            assertTrue(person.setAge(18));
            assertTrue(person.setAge(20));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void setFirstName() {
        Person person = new Person();

        try {
            assertTrue(person.setFirstName("ivan"));
//            assertFalse(person.setFirstName("i9van"));
//            assertFalse(person.setFirstName(""));
//            assertFalse(person.setFirstName("i9van "));
//            assertFalse(person.setFirstName("i.van"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void setSecondName() {
        Person person = new Person();

        try {
            assertTrue(person.setSecondName("ivan"));
//            assertFalse(person.setSecondName("i9van"));
//            assertFalse(person.setSecondName(""));
//            assertFalse(person.setSecondName("i9van "));
//            assertFalse(person.setSecondName("i.van"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
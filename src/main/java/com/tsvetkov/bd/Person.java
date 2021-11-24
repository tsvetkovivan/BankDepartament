package com.tsvetkov.bd;


import com.tsvetkov.util.ErrorFormException;

import javax.persistence.*;
import java.sql.Date;
import java.util.Locale;

@Entity
@Table(name = "kurs.persons")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name="discriminator",
        discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue(value="P")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "person_age")
    private int           age;

    @Column(name = "person_first_name")
    private String        firstName;

    @Column(name = "person_second_name")
    private String        secondName;

    @Column(name = "person_date_birth")
    private java.sql.Date dateBirth = new Date(2121321321);;



    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public java.sql.Date getDateBirth() {
        return dateBirth;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean setAge(int age) throws ErrorFormException {
        if (age < 18) {
            throw new ErrorFormException("Нельзя добавить несовершеннолетнего человека.");
        }
        this.age = age;
        return true;
    }

    public boolean setFirstName(String firstName) throws ErrorFormException {
        boolean allLetters = firstName.chars().allMatch(Character::isLetter);

        if (!allLetters || firstName.isEmpty() || firstName.contains(" ")) {
            throw new ErrorFormException("Некорректные символы в имени.");
        } else {
            this.firstName = firstName.toLowerCase();
        }
        return true;
    }

    public boolean setSecondName(String secondName) throws ErrorFormException {
        boolean allLetters = secondName.chars().allMatch(Character::isLetter);

        if (!allLetters || secondName.isEmpty() || secondName.contains(" ")) {
            throw new ErrorFormException("Некорректные символы в фамилии.");
            //return false;
        } else {
            this.secondName = secondName.toLowerCase();
        }
        return true;
    }

    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }

    @Override
    public String toString() {
        return secondName + " " + firstName;
    }
}

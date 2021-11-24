package com.tsvetkov.bd;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kurs.persons")
@DiscriminatorValue("M")
public class Manager extends Person {
    @Column(name = "manager_count_deals")
    private int countDeals;

    @Column(name = "manager_count_loans")
    private int countLoans;

    public int getCountDeals() {
        return countDeals;
    }

    public void setCountDeals(int countDeals) {
        this.countDeals = countDeals;
    }

    public int getCountLoans() {
        return countLoans;
    }

    public void setCountLoans(int countLoans) {
        this.countLoans = countLoans;
    }

    public boolean addNewDeal(Deal deal) {
        return true;
    }

    public List<Deal> getDeals() {
        return new ArrayList<>();
    }

    public boolean removeDeal(Deal de) {
        return true;
    }

    public boolean addNewClient(Client client) {
        return true;
    }

    public int getCountOfClients() {
        return 0;
    }

    public boolean signNewDeal(Client client) {
        return true;
    }

    public boolean removeClient(Client client) {
        return true;
    }

    public List<Client> getClients() {
        return new ArrayList<>();
    }

    public Client getClient(Client person) {
        return new Client();
    }
}


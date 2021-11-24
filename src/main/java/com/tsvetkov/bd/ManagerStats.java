package com.tsvetkov.bd;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "kurs.stats")
public class ManagerStats {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "manager_id")
    private  int manager_id;

    @Column(name = "manager_deal_count")
    private int manager_deal_count;

    @Column(name = "manager_loan_count")
    private int manager_loan_count;

    @Column(name = "date")
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getManager_id() {
        return manager_id;
    }

    public void setManager_id(int manager_id) {
        this.manager_id = manager_id;
    }

    public int getManager_deal_count() {
        return manager_deal_count;
    }

    public void setManager_deal_count(int manager_deal_count) {
        this.manager_deal_count = manager_deal_count;
    }

    public int getManager_loan_count() {
        return manager_loan_count;
    }

    public void setManager_loan_count(int manager_loan_count) {
        this.manager_loan_count = manager_loan_count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}

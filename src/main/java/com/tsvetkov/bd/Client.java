package com.tsvetkov.bd;

import javax.persistence.*;

@Entity
@Table(name = "kurs.persons")
@DiscriminatorValue("C")
public class Client extends Person {
    @Column(name = "client_his_manager_id")
    private int his_manager_id;

    @Column(name = "client_his_count_deals")
    private int his_count_deals;

    @Column(name = "client_his_summary_debt")
    private int his_summary_debt;

    public int getHis_count_deals() {
        return his_count_deals;
    }

    public void setHis_count_deals(int his_count_deals) {
        this.his_count_deals = his_count_deals;
    }

    public int getHis_summary_debt() {
        return his_summary_debt;
    }

    public void setHis_summary_debt(int his_summary_debt) {
        this.his_summary_debt = his_summary_debt;
    }

    public int getHis_manager_id() {
        return his_manager_id;
    }

    public void setHis_manager_id(int his_manager_id) {
        this.his_manager_id = his_manager_id;
    }
}

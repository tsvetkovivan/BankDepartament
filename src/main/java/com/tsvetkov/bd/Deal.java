package com.tsvetkov.bd;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "kurs.deals")
public class Deal {
    @Id
    @Column(name = "id_deal_key")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_deal_key;

    @Column(name = "id_manager")
    private int id_manager;

    @Column(name = "id_client")
    private int id_client;

    @Column(name = "date_sign_deal")
    private java.sql.Date   dateOfSighDeal = new Date(2121321321);

    @Column(name = "date_payment")
    private java.sql.Date   dateOfPayment = new Date(2121321321);

    @Column(name = "total_amount_loan")
    private int             TotalAmountOfLoan;

    @Column(name = "current_debt")
    private int             currentDebt;

    @Column(name = "redeemed")
    private boolean         redeemed;

    @Column(name = "paid_this_section")
    private boolean         paidThisSection;

    public Deal () {

    }

    public int getId_deal_key() {
        return id_deal_key;
    }

    public int getId_manager() {
        return id_manager;
    }

    public int getId_client() {
        return id_client;
    }

    public java.sql.Date getDateOfSighDeal() {
        return dateOfSighDeal;
    }

    public int getTotalAmountOfLoan() {
        return TotalAmountOfLoan;
    }

    public int getCurrentDebt() {
        return currentDebt;
    }

    public java.sql.Date getDateOfPayment() {
        return dateOfPayment;
    }

    public boolean getRedeemed() {
        return redeemed;
    }

    public boolean getPaidThisSection() {
        return paidThisSection;
    }

    public void setId_deal_key(int id_deal_key) {
        this.id_deal_key = id_deal_key;
    }

    public void setId_manager(int id_manager) {
        this.id_manager = id_manager;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public void setDateOfSighDeal(Date dateOfSighDeal) {
        this.dateOfSighDeal = dateOfSighDeal;
    }

    public void setDateOfPayment(Date dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public void setTotalAmountOfLoan(int totalAmountOfLoan) {
        TotalAmountOfLoan = totalAmountOfLoan;
    }

    public void setCurrentDebt(int currentDebt) {
        this.currentDebt = currentDebt;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public void setPaidThisSection(boolean paidThisSection) {
        this.paidThisSection = paidThisSection;
    }

    @Override
    public String toString() {
        return "Договор №" + (1000 + id_deal_key);
    }
}
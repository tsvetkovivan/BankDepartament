package com.tsvetkov.ui;

import com.tsvetkov.bd.Client;
import com.tsvetkov.bd.Deal;
import com.tsvetkov.bd.Manager;
import com.tsvetkov.bd.ManagerStats;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class TabDeals extends JPanel {
    private uiApp app;
    private EntityManager entityManager;
    private DefaultListModel<Deal> modelList;
    private JList jlist;
    private int lastSortIndManager = 0;
    private int lastSortIndClient = 0;
    private boolean lastDebtorsDealState = false;

    public TabDeals (uiApp app, EntityManager entityManager) {
        this.app = app;
        this.entityManager = entityManager;

        entityManager.getTransaction().begin();
        app.deals = entityManager.createQuery("from Deal").getResultList();
        entityManager.getTransaction().commit();

        modelList = new DefaultListModel<>();
        jlist = new JList(modelList);
        jlist.setCellRenderer(new ListItemRenderer("docs.jpg"));
        for (Deal d : app.deals) {
            modelList.addElement(d);
        }
        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Rectangle r = jlist.getCellBounds(0, jlist.getLastVisibleIndex());
                    if (r != null && r.contains(e.getPoint())) {
                        onClickDeal((Deal) jlist.getSelectedValue());
                    }
                }
            }
        });

        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(jlist);
        add(scroll, BorderLayout.CENTER);

        JButton addNewDealButton = new JButton();
        addNewDealButton.setText("Добавить новый договор");
        addNewDealButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (app.managers.size() == 0 || app.clients.size() == 0) {
                    JOptionPane.showMessageDialog(null,"Сначала добавьте менеджера и клиента");
                    return;
                }

                JPanel panel = new JPanel();
                panel.setLayout(null);

                JFrame frame = new JFrame();

                JLabel clientmanager = new JLabel("Обслуживающий менеджер");
                clientmanager.setBounds(25, 25, 200, 20);

                String[] arr = new String[app.managers.size()];
                for (int i = 0; i < app.managers.size(); i++) {
                    arr[i] = app.managers.get(i).toString();
                }
                JComboBox box = new JComboBox(arr);
                box.setBounds(25, 45, 220, 20);

                JLabel client = new JLabel("Клиент");
                client.setBounds(25, 75, 200, 20);

                String[] arr1 = new String[app.clients.size()];
                for (int i = 0; i < app.clients.size(); i++) {
                    arr1[i] = app.clients.get(i).toString();
                }
                JComboBox box1 = new JComboBox(arr1);
                box1.setBounds(25, 95, 220, 20);

                JLabel dateLabel1 = new JLabel("Дата заключения договора");
                UtilDateModel modell = new UtilDateModel();
                Properties p = new Properties();
                p.put("text.today", "Today");
                p.put("text.month", "Month");
                p.put("text.year", "Year");
                JDatePanelImpl datePanel = new JDatePanelImpl(modell, p);
                JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

                dateLabel1.setBounds(25, 125, 220, 20);
                datePicker.setBounds(25, 145, 220, 30);

                JLabel loanLabel = new JLabel("Сумма кредита");
                loanLabel.setBounds(25, 185, 200, 20);

                NumberFormat format = NumberFormat.getInstance();
                NumberFormatter formatter = new NumberFormatter(format);
                formatter.setValueClass(Integer.class);
                formatter.setMinimum(0);
                formatter.setMaximum(Integer.MAX_VALUE);
                formatter.setAllowsInvalid(false);
                JFormattedTextField loanText = new JFormattedTextField(formatter);
                loanText.setBounds(25, 205, 220, 20);

                JLabel paidLabel = new JLabel("День ежемесячной выплаты");
                paidLabel.setBounds(25, 235, 200, 20);

                UtilDateModel modelll = new UtilDateModel();
                Properties p1 = new Properties();
                p1.put("text.today", "Today");
                JDatePanelImpl datePanel1 = new JDatePanelImpl(modelll, p1);
                JDatePickerImpl datePicker1 = new JDatePickerImpl(datePanel1, new MouthDateLabelFormatter());
                datePicker1.setBounds(25, 255, 220, 30);


                JButton ok = new JButton("Добавить");
                ok.setBounds(100, 305, 100, 20);
                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Deal deal = new Deal();

                        //получение даты заключения договора
                        long dateSignDealMillis = -1;
                        Date d = new Date();
                        String string_date = datePicker.getJFormattedTextField().getText();
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            d = f.parse(string_date);
                            dateSignDealMillis = d.getTime();
                        } catch (ParseException exp) {
                            exp.printStackTrace();
                        }

                        if (
                                app.managers.size() > 0 &&
                                app.clients.size() > 0 &&
                                dateSignDealMillis != -1
                        ) {
                            //получение индексов менеджера и клиента
                            int ind = box.getSelectedIndex();
                            deal.setId_manager(app.managers.get(ind).getId());
                            ind = box1.getSelectedIndex();
                            deal.setId_client(app.clients.get(ind).getId());

                            //получение суммы договора
                            String loan_string = loanText.getText();
                            String loan_st = "";
                            for (int i = 0 ; i < loan_string.length(); i++) {
                                if (Character.isDigit(loan_string.charAt(i))) {
                                    loan_st += loan_string.charAt(i);
                                }
                            }
                            int loan = Integer.parseInt(loan_st);

                            //установка суммы
                            deal.setTotalAmountOfLoan(loan);
                            //установка долга
                            deal.setCurrentDebt(loan);
                            //установка поля "погашено"
                            deal.setRedeemed(false);
                            //установка поля "проплачено в этом месяце"
                            deal.setPaidThisSection(true);
                            //установка даты заключения договора
                            deal.setDateOfSighDeal(new java.sql.Date(dateSignDealMillis));

                            //получение даты платежа (выбранный день для сл месяца)
                            String datePayment = datePicker1.getJFormattedTextField().getText();
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datePayment));
                            c.add(Calendar.MONTH, 1);
                            Date datePaym = c.getTime();
                            //установка дня платежа
                            deal.setDateOfPayment(new java.sql.Date(datePaym.getTime()));

                            //обновление полей "колиечество сделок" и "сумма выданных кредитов"
                            //для выбранного менеджера
                            ind = box.getSelectedIndex();
                            app.managers.get(ind).setCountDeals(app.managers.get(ind).getCountDeals() + 1);
                            app.managers.get(ind).setCountLoans(app.managers.get(ind).getCountLoans() + loan);

                            //обновление статистики для менеджера
                            ManagerStats stats = new ManagerStats();
                            stats.setManager_id(app.managers.get(ind).getId());
                            stats.setManager_deal_count(app.managers.get(ind).getCountDeals());
                            stats.setManager_loan_count(app.managers.get(ind).getCountLoans());
                            //stats.setDate(new java.sql.Date(System.currentTimeMillis()));
                            stats.setDate(new java.sql.Date(dateSignDealMillis));

                            //обновление статистики менеджера за день
                            entityManager.getTransaction().begin();
                            try {
                                Query query = entityManager
                                        .createQuery("FROM ManagerStats where manager_id="
                                                + stats.getManager_id() + " order by id DESC");
                                query.setMaxResults(1);
                                ManagerStats last = (ManagerStats) query.getSingleResult();

                                if (last.getDate().toString().equals(stats.getDate().toString())) {
                                    last.setManager_deal_count(app.managers.get(ind).getCountDeals());
                                    last.setManager_loan_count(app.managers.get(ind).getCountLoans());
                                    entityManager.persist(last);
                                } else {
                                    entityManager.persist(stats);
                                }
                            } catch (NoResultException exp1) {
                                entityManager.persist(stats);
                            }

                            //обновление полей "количество сделок" и "общий долг в банке" для клента
                            ind = box1.getSelectedIndex();
                            app.clients.get(ind).setHis_count_deals(app.clients.get(ind).getHis_count_deals() + 1);
                            app.clients.get(ind).setHis_summary_debt(app.clients.get(ind).getHis_summary_debt() + loan);

                            ind = box.getSelectedIndex();

                            //entityManager.getTransaction().begin();
                            app.deals.add(deal);
                            modelList.addElement(deal);
                            entityManager.persist(deal);
                            entityManager.persist(app.managers.get(ind));
                            entityManager.getTransaction().commit();
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Ошибка в данных");
                        }
                    }
                });

                panel.add(dateLabel1);
                panel.add(datePicker);
                panel.add(clientmanager);
                panel.add(box);
                panel.add(client);
                panel.add(box1);
                panel.add(loanLabel);
                panel.add(loanText);
                panel.add(paidLabel);
                panel.add(datePicker1);
                panel.add(ok);

                frame.setPreferredSize(new Dimension(300, 450));
                frame.getContentPane().add(panel);
                frame.setVisible(true);
                frame.setResizable(false);
                frame.setTitle("Добавить новый договор");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocation(700, 300);
                frame.pack();
            }
        });

        JButton sortButton = new JButton("Сортировать");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame();
                frame.setPreferredSize(new Dimension(480, 360));
                frame.setVisible(true);
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocation(700, 300);
                frame.setTitle("Параметры сортировки");
                frame.pack();

                JPanel panel = new JPanel();
                frame.getContentPane().add(panel);
                panel.setLayout(null);

                JLabel showDealsForOneManager = new JLabel("Показать договоры выбранного менеджера");
                showDealsForOneManager.setBounds(10, 10, 300, 20);
                panel.add(showDealsForOneManager);

                String[] managersArray = new String[app.managers.size() + 1];
                managersArray[0] = "Для всех";
                for (int i = 0; i < app.managers.size(); i++) {
                    managersArray[i + 1] = app.managers.get(i).toString();
                }
                JComboBox dealsManagerBox = new JComboBox(managersArray);
                dealsManagerBox.setBounds(10, 30, 220, 20);
                panel.add(dealsManagerBox);
                dealsManagerBox.setSelectedIndex(lastSortIndManager);
                dealsManagerBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lastSortIndManager = dealsManagerBox.getSelectedIndex();
                    }
                });


                JLabel showDealsForOneClient = new JLabel("Показать договоры выбранного клиента");
                showDealsForOneClient.setBounds(10, 60, 300, 20);
                panel.add(showDealsForOneClient);

                String[] clientsArray = new String[app.clients.size() + 1];
                clientsArray[0] = "Для всех";
                for (int i = 0; i < app.clients.size(); i++) {
                    clientsArray[i + 1] = app.clients.get(i).toString();
                }
                JComboBox dealsClientBox = new JComboBox(clientsArray);
                dealsClientBox.setBounds(10, 80, 220, 20);
                panel.add(dealsClientBox);
                dealsClientBox.setSelectedIndex(lastSortIndClient);
                dealsClientBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lastSortIndClient = dealsClientBox.getSelectedIndex();
                    }
                });

                JCheckBox debtorsDeal = new JCheckBox("Показывать просроченные по оплате договоры");
                debtorsDeal.setBounds(10, 110, 400, 20);
                panel.add(debtorsDeal);
                debtorsDeal.setSelected(lastDebtorsDealState);
                debtorsDeal.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        lastDebtorsDealState = debtorsDeal.isSelected();
                    }
                });

                JButton applySort = new JButton("Применить параметры");
                applySort.setBounds(140, 280, 200, 20);
                panel.add(applySort);
                applySort.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        modelList.clear();

                        DefaultListModel<Deal> temp_list = new DefaultListModel<>();

                        if (lastDebtorsDealState) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            String date =  "'" + format.format(new Date(System.currentTimeMillis())) + "'";
                            String queryStr = "from Deal where ( date_payment < " + date + " and redeemed = false )";

                            List<Deal> debtorsDeals = entityManager
                                    .createQuery(queryStr)
                                    .getResultList();

                            for (Deal d : debtorsDeals) {
                                temp_list.addElement(d);
                            }
                        } else {
                            for (Deal d : app.deals) {
                                temp_list.addElement(d);
                            }
                        }


                        if (lastSortIndManager != 0) {
                            Manager chosenManager = app.managers.get(lastSortIndManager - 1);
                            for (int i = 0; i < temp_list.size(); i++) {
                                if (temp_list.get(i).getId_manager() != chosenManager.getId()) {
                                    temp_list.remove(i);
                                    i--;
                                }
                            }
                        }


                        if (lastSortIndClient != 0) {
                            Client chosenClient = app.clients.get(lastSortIndClient - 1);
                            for (int i = 0; i < temp_list.size(); i++) {
                                if (temp_list.get(i).getId_client() != chosenClient.getId()) {
                                    temp_list.remove(i);
                                    i--;
                                }
                            }
                        }

                        for (int i = 0; i < temp_list.size(); i++) {
                            modelList.addElement(temp_list.get(i));
                        }

                        frame.dispose();
                    }
                });
            }
        });


        JPanel comboPanel = new JPanel();
        comboPanel.add(sortButton);
        comboPanel.add(addNewDealButton);
        add(comboPanel, BorderLayout.SOUTH);

        uiApp.logger.info("tab deals start");
    }


    private void onClickDeal(Deal deal) {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(480, 460));
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation(700, 300);
        frame.setTitle(deal.toString());

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel clientManager = new JLabel("Обслуживающий менеджер");
        clientManager.setBounds(10, 10, 250, 20);
        panel.add(clientManager);

        int selectedInd = 0;
        String[] managersArray = new String[app.managers.size()];
        for (int i = 0; i < app.managers.size(); i++) {
            managersArray[i] = app.managers.get(i).toString();
            if (app.managers.get(i).getId() == deal.getId_manager()) {
                selectedInd = i;
            }
        }
        JComboBox clientManagerBox = new JComboBox(managersArray);
        clientManagerBox.setBounds(10, 30, 220, 20);
        clientManagerBox.setSelectedIndex(selectedInd);
        panel.add(clientManagerBox);
        clientManagerBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ind = clientManagerBox.getSelectedIndex();
                Manager manager = app.managers.get(ind);
                deal.setId_manager(manager.getId());
                entityManager.getTransaction().begin();
                entityManager.persist(deal);
                entityManager.getTransaction().commit();
            }
        });

        JButton remove = new JButton("Удалить договор");
        remove.setBounds(140, 380, 200, 20);
        panel.add(remove);
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = null;
                for (Client c : app.clients) {
                    if (deal.getId_client() == c.getId()) {
                        client = c;
                        break;
                    }
                }

                client.setHis_summary_debt(client.getHis_summary_debt() - deal.getCurrentDebt());

                modelList.remove(modelList.indexOf(deal));

                app.deals.remove(deal);

                entityManager.getTransaction().begin();
                entityManager.persist(client);
                entityManager.remove(deal);
                entityManager.getTransaction().commit();

                frame.dispose();
            }
        });



        Client client = null;
        for (Client c : app.clients) {
            if (deal.getId_client() == c.getId()) {
                client = c;
                break;
            }
        }

        JLabel clientLabel = new JLabel("Клиент: " + client.toString());
        clientLabel.setBounds(10, 70, 300, 20);
        panel.add(clientLabel);

        JLabel dateSign = new JLabel("Дата подписания договора: " + deal.getDateOfSighDeal().toString());
        dateSign.setBounds(10, 100, 300 , 20);
        panel.add(dateSign);

        JLabel totalLoan = new JLabel("Сумма в договоре: " + deal.getTotalAmountOfLoan());
        totalLoan.setBounds(10, 130, 300, 20);
        panel.add(totalLoan);

        JLabel curDebt = new JLabel("Текущий долг: " + deal.getCurrentDebt());
        curDebt.setBounds(10, 160, 300, 20);
        panel.add(curDebt);

        JLabel nextDatePay = new JLabel("Дата платежа по плану: " + deal.getDateOfPayment().toString());
        nextDatePay.setBounds(10, 190, 300, 20);
        panel.add(nextDatePay);

        JLabel redeemedLabel = new JLabel("Погашен: " + deal.getRedeemed());
        redeemedLabel.setBounds(10, 220, 300, 20);
        panel.add(redeemedLabel);

        JLabel newPay = new JLabel("Внести платеж: ");
        newPay.setBounds(10, 250, 100, 20);
        panel.add(newPay);

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        JFormattedTextField payText = new JFormattedTextField(formatter);
        payText.setBounds(110, 250, 150, 20);
        panel.add(payText);

        JButton payEnter = new JButton("Внести");
        payEnter.setBounds(270, 250, 100, 20);
        panel.add(payEnter);
        payEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deal.getRedeemed()) {
                    return;
                }

                String pay_string = payText.getText();
                String pay_st = "";
                for (int i = 0 ; i < pay_string.length(); i++) {
                    if (Character.isDigit(pay_string.charAt(i))) {
                        pay_st += pay_string.charAt(i);
                    }
                }
                int pay = Integer.parseInt(pay_st);

                java.sql.Date curDate = deal.getDateOfPayment();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(curDate);
                calendar.add(Calendar.MONTH, 1);
                deal.setDateOfPayment(new java.sql.Date(calendar.getTime().getTime()));
                nextDatePay.setText("Дата платежа по плану: " + deal.getDateOfPayment().toString());


                int curDebt_ = deal.getCurrentDebt();
                int newDebt = curDebt_ - pay;
                if (newDebt <= 0) {
                    newDebt = 0;
                    deal.setRedeemed(true);
                    nextDatePay.setText("Дата платежа по плану: Погашен");
                    redeemedLabel.setText("Погашен: " + deal.getRedeemed());
                }

                deal.setCurrentDebt(newDebt);
                curDebt.setText("Текущий долг: " + deal.getCurrentDebt());

                Client client_ = null;
                for (Client c : app.clients) {
                    if (deal.getId_client() == c.getId()) {
                        client_ = c;
                        break;
                    }
                }

                int sumDebt = client_.getHis_summary_debt();
                int tempDebt = sumDebt - pay;
                if (tempDebt <= 0) {
                    tempDebt = 0;
                }
                client_.setHis_summary_debt(tempDebt);


                entityManager.getTransaction().begin();
                entityManager.persist(deal);
                entityManager.persist(client_);
                entityManager.getTransaction().commit();
            }
        });

        frame.pack();
    }

}

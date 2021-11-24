package com.tsvetkov.ui;

import com.tsvetkov.bd.Client;
import com.tsvetkov.bd.Deal;
import com.tsvetkov.bd.Manager;
import com.tsvetkov.bd.ManagerStats;
import com.tsvetkov.util.ErrorFormException;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;


public class TabManagers extends JPanel {

    private uiApp app;
    private EntityManager entityManager;
    private DefaultListModel<Manager> model;

    public TabManagers (uiApp app, EntityManager entityManager) {
        this.app = app;
        this.entityManager = entityManager;


        entityManager.getTransaction().begin();
        app.managers = entityManager.createQuery("from Manager").getResultList();
        entityManager.getTransaction().commit();
        model = new DefaultListModel<>();
        JList list = new JList(model);
        list.setCellRenderer(new ListItemRenderer("manager.jpg"));
        for (Manager m : app.managers) {
            model.addElement(m);
        }



        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
                    if (r != null && r.contains(e.getPoint())) {
                        onClick((Manager) list.getSelectedValue());
                    }
                }
            }
        });

        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(list);
        add(scroll, BorderLayout.CENTER);

        JButton addNewManagerButton = new JButton();
        addNewManagerButton.setText("Добавить нового сотрудника");
        addNewManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel();
                panel.setLayout(null);

                JFrame frame = new JFrame();

                JLabel firstNameLabel = new JLabel("Имя");
                firstNameLabel.setBounds(25, 25, 70, 20);

                JTextField firstNameText = new JTextField();
                firstNameText.setBounds(95, 25, 150, 20);

                JLabel secondNameLabel = new JLabel("Фамилия");
                secondNameLabel.setBounds(25, 55, 70, 20);

                JTextField secondNameText = new JTextField();
                secondNameText.setBounds(95, 55, 150, 20);

                JLabel dateLabel1 = new JLabel("Дата рождения");
                UtilDateModel modell = new UtilDateModel();
                Properties p = new Properties();
                p.put("text.today", "Today");
                p.put("text.month", "Month");
                p.put("text.year", "Year");
                JDatePanelImpl datePanel = new JDatePanelImpl(modell, p);
                JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

                dateLabel1.setBounds(25, 85, 100, 20);
                datePicker.setBounds(125, 85, 120, 30);

                JButton ok = new JButton("Добавить");
                ok.setBounds(100, 160, 100, 20);
                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Manager manager = new Manager();

                        long milliseconds = -1;
                        Date d = new Date();
                        String string_date = datePicker.getJFormattedTextField().getText();
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            d = f.parse(string_date);
                            milliseconds = d.getTime();
                        } catch (ParseException exp) {
                            exp.printStackTrace();
                        }


                        if (milliseconds == -1) {
                            JOptionPane.showMessageDialog(null, "Невозможно считать дату");
                            uiApp.logger.warn("adding new manager abort");
                            return;
                        }


                        try {
                            manager.setFirstName(firstNameText.getText());
                            manager.setSecondName(secondNameText.getText());

                            long curMillis = System.currentTimeMillis();
                            Date curDate = new Date(curMillis);
                            int age = Period.between(
                                    d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                    curDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            ).getYears();

                            manager.setAge(age);
                            manager.setDateBirth(new java.sql.Date(milliseconds));
                            entityManager.getTransaction().begin();
                            app.managers.add(manager);
                            model.addElement(manager);
                            entityManager.persist(manager);

                            ManagerStats stats = new ManagerStats();
                            stats.setManager_id(manager.getId());
                            stats.setManager_deal_count(0);
                            stats.setManager_loan_count(0);
                            stats.setDate(new java.sql.Date(System.currentTimeMillis()));
                            entityManager.persist(stats);

                            entityManager.getTransaction().commit();
                            frame.dispose();
                        } catch (ErrorFormException exception) {
                            exception.showErrorOnScreen();
                        }

                    }
                });

                panel.add(firstNameLabel);
                panel.add(firstNameText);
                panel.add(secondNameLabel);
                panel.add(secondNameText);
                panel.add(dateLabel1);
                panel.add(datePicker);
                panel.add(ok);

                frame.setPreferredSize(new Dimension(300, 250));
                frame.getContentPane().add(panel);
                frame.setVisible(true);
                frame.setResizable(false);
                frame.setTitle("Добавить нового сотрудника");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocation(700, 300);
                frame.pack();
            }
        });

        JPanel comboPanel = new JPanel();
        comboPanel.add(addNewManagerButton);
        add(comboPanel, BorderLayout.SOUTH);

        uiApp.logger.info("tab managers start");
    }

    private void onClick(Manager curManager) {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(480, 360));
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation(700, 300);
        frame.setTitle(curManager.toString());

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel age = new JLabel("Возраст: " + curManager.getAge() + " лет");
        age.setBounds(10, 10, 300, 20);
        panel.add(age);

        JLabel countDeals = new JLabel("Количество сделок менеджера: " + curManager.getCountDeals());
        countDeals.setBounds(10, 40, 300, 20);
        panel.add(countDeals);

        JLabel countLoans = new JLabel("Общая сумма выданных кредитов: " + curManager.getCountLoans());
        countLoans.setBounds(10, 70, 300, 20);
        panel.add(countLoans);

        JLabel countHisClients = new JLabel();
        countHisClients.setBounds(10, 100, 200, 20);
        panel.add(countHisClients);
        int count = 0;
        for (Client c : app.clients) {
            if (c.getHis_manager_id() == curManager.getId()) {
                count++;
            }
        }
        countHisClients.setText("Количество клиентов : " + count);

        JButton remove = new JButton("Удалить менеджера");
        remove.setBounds(140, 280, 200, 20);
        panel.add(remove);
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isFree = true;
                for (int i = 0; i < app.deals.size(); i++) {
                    if (app.deals.get(i).getId_manager() == curManager.getId()) {
                        isFree = false;
                        break;
                    }
                }

                for (int i = 0; i < app.clients.size(); i++) {
                    if (app.clients.get(i).getHis_manager_id() == curManager.getId()) {
                        isFree = false;
                        break;
                    }
                }

                if (isFree) {
                    entityManager.getTransaction().begin();
                    entityManager.remove(curManager);
                    entityManager.getTransaction().commit();

                    model.remove(model.indexOf(curManager));
                    app.managers.remove(curManager);

                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,"Нельзя удалить менеджера, на котором числятся договоры клиентов и клиенты.\nУдалите сначала соотв. договоры и клиентов или переназначте их менеджера.");
                    //JOptionPane.showMessageDialog(null,"Нельзя удалить менеджера, на котором числятся клиенты.\nУдалите сначала его клиентов или переназначте их менеджера.");
                }
            }
        });

        JLabel labelPeriodWord = new JLabel("Отчет по работе за период:");
        labelPeriodWord.setBounds(10, 130, 300, 20);
        panel.add(labelPeriodWord);

        JLabel periodStart = new JLabel("Начало :");
        periodStart.setBounds(10, 150, 70, 20);
        panel.add(periodStart);

        JLabel periodEnd = new JLabel("Конец :");
        periodEnd.setBounds(10, 180, 70, 20);
        panel.add(periodEnd);

        UtilDateModel modelStart = new UtilDateModel();
        UtilDateModel modelEnd = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanelStart = new JDatePanelImpl(modelStart, p);
        JDatePanelImpl datePanelEnd = new JDatePanelImpl(modelEnd, p);
        JDatePickerImpl datePickerStart = new JDatePickerImpl(datePanelStart, new DateLabelFormatter());
        JDatePickerImpl datePickerEnd = new JDatePickerImpl(datePanelEnd, new DateLabelFormatter());
        datePickerStart.setBounds(80, 150, 150, 30);
        datePickerEnd.setBounds(80, 180, 150, 30);
        panel.add(datePickerStart);
        panel.add(datePickerEnd);

        JLabel periodInfoLabel = new JLabel();
        periodInfoLabel.setBounds(10, 210, 300, 60);
        panel.add(periodInfoLabel);

        JButton periodShow = new JButton("Показать");
        periodShow.setBounds(240, 150, 150, 56);
        panel.add(periodShow);
        periodShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long millisStart = -1;
                long millisStop = -1;
                long millisCurrent = System.currentTimeMillis();

                String s_periodStart = datePickerStart.getJFormattedTextField().getText();
                String s_periodEnd = datePickerEnd.getJFormattedTextField().getText();


                Date dateStart;
                Date dateEnd;
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    if (s_periodEnd.isEmpty() || s_periodStart.isEmpty()) {
                        throw new ParseException("", 1);
                    }

                    dateStart = f.parse(s_periodStart);
                    dateEnd = f.parse(s_periodEnd);

                    Date dateCurrent = new Date(millisCurrent);

                    while(true) {
                        if (dateStart.after(dateEnd)) {
                            JOptionPane.showMessageDialog(null,"Стартовая дата не может идти после конечной");
                            break;
                        }

                        if (dateEnd.after(dateCurrent)) {
                            JOptionPane.showMessageDialog(null,"Конечная дата не может быть в будущем");
                            break;
                        }

                        List<ManagerStats> stats =
                                entityManager.createQuery("FROM ManagerStats where manager_id=" + curManager.getId() +
                                        " order by id")
                                        .getResultList();
                        if (stats == null || stats.size() == 0) {
                            JOptionPane.showMessageDialog(null,"У менеджера не было еще ни одной сделки");
                            break;
                        }

                        if (stats.get(0).getDate().after(dateStart)) {
                            JOptionPane.showMessageDialog(null,"Начало работы сотрудника: " + stats.get(0).toString());
                            break;
                        }

                        ManagerStats managerStart = stats.get(0);
                        ManagerStats managerStop = stats.get(stats.size() - 1);
                        boolean isFindStart = true;

                        for (ManagerStats stat : stats) {
                            if (isFindStart && stat.getDate().compareTo(dateStart) == 0) {
                                managerStart = stat;
                                isFindStart = false;
                            }

                            if (isFindStart && stat.getDate().before(dateStart)) {
                                managerStart = stat;
                            }


                            if (stat.getDate().compareTo(dateEnd) == 0) {
                                managerStop = stat;
                                break;
                            }

                            if (stat.getDate().before(dateEnd)) {
                                managerStop = stat;
                            }
                        }

                        int dealCount;
                        int loanCount;

                        //если даты начала и конца совпадают
                        if (managerStart.getDate().compareTo(dateEnd) == 0) {
                            //если начало это самая первая сделка менеджера
                            if (managerStart.getDate().compareTo(stats.get(0).getDate()) == 0) {
                                dealCount = managerStart.getManager_deal_count();
                                loanCount = managerStart.getManager_loan_count();
                            } else {
                              int managerStartIndex = stats.indexOf(managerStart);
                              ManagerStats tempStart = stats.get(managerStartIndex - 1);

                              dealCount = managerStop.getManager_deal_count() - tempStart.getManager_deal_count();
                              loanCount = managerStop.getManager_loan_count() - tempStart.getManager_loan_count();
                            }
                        } else {
                            if (dateStart.compareTo(managerStart.getDate()) == 0) {
                                dealCount = managerStop.getManager_deal_count();
                                loanCount = managerStop.getManager_loan_count();
                            } else {
                                dealCount = managerStop.getManager_deal_count() - managerStart.getManager_deal_count();
                                loanCount = managerStop.getManager_loan_count() - managerStart.getManager_loan_count();
                            }
                        }

                        periodInfoLabel.setText("<html>За этот интервал менеджер совершил " +
                                dealCount +
                                " сделок </br> на сумму " +
                                loanCount +
                                "</html>");

                        break;
                    }
                } catch (ParseException exp) {
                    exp.printStackTrace();
                }
            }
        });

        frame.pack();
    }
}

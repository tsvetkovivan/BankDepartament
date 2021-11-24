package com.tsvetkov.ui;

import com.mysql.cj.conf.ConnectionUrlParser;
import com.tsvetkov.bd.ManagerStats;
import com.tsvetkov.util.PdfThread;
import com.tsvetkov.util.ReportCreater;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.List;
import java.util.Vector;

public class TabReporting extends JPanel {

    private int deals;
    private long loans;
    private uiApp app;
    private EntityManager entityManager;
    private JLabel countDeals, countLoans, countManagers, countClients;

    private class Pair {
        public int dealCount = 0;
        public int loanCount = 0;

        public Pair(int dealCount, int loanCount) {
            this.dealCount = dealCount;
            this.loanCount = loanCount;
        }
    }

    public TabReporting(uiApp app, EntityManager entityManager) {
        this.app = app;
        this.entityManager = entityManager;

        setLayout(null);

        countDeals = new JLabel();
        countDeals.setBounds(10, 10, 300, 20);
        add(countDeals);

        countLoans = new JLabel();
        countLoans.setBounds(10, 40, 300, 20);
        add(countLoans);

        countManagers = new JLabel();
        countManagers.setBounds(10, 70, 300, 20);
        add(countManagers);

        countClients = new JLabel();
        countClients.setBounds(10, 100, 300, 20);
        add(countClients);

        updateInfo();


        JLabel labelPeriodWord = new JLabel("Отчет по работе отдела за период:");
        labelPeriodWord.setBounds(10, 130, 300, 20);
        add(labelPeriodWord);

        JLabel periodStart = new JLabel("Начало :");
        periodStart.setBounds(10, 150, 70, 20);
        add(periodStart);

        JLabel periodEnd = new JLabel("Конец :");
        periodEnd.setBounds(10, 180, 70, 20);
        add(periodEnd);

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
        add(datePickerStart);
        add(datePickerEnd);

        JLabel periodInfoLabel = new JLabel();
        periodInfoLabel.setBounds(10, 210, 300, 60);
        add(periodInfoLabel);

        JButton periodShow = new JButton("Показать");
        periodShow.setBounds(240, 150, 150, 56);
        add(periodShow);
        periodShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ManagerStats> allStats = entityManager
                        .createQuery("from ManagerStats order by date").getResultList();

                if (allStats == null || allStats.size() == 0) {
                    JOptionPane.showMessageDialog(null,"В отделе не было еще ни отдой сделки.");
                    return;
                }

                Date allStatsDateBegin = allStats.get(0).getDate();

                Vector<Integer> uniqueIdsFromStats = new Vector<>();
                for (ManagerStats s : allStats) {
                    if (!uniqueIdsFromStats.contains(s.getManager_id())) {
                        uniqueIdsFromStats.add(s.getManager_id());
                    }
                }

                long millisCurrentTime = System.currentTimeMillis();

                String s_periodStart = datePickerStart.getJFormattedTextField().getText();
                String s_periodEnd = datePickerEnd.getJFormattedTextField().getText();

                Date datePeriodStart = null;
                Date datePeriodEnd = null;
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                Date dateCurrentTime = new Date(millisCurrentTime);

                try {
                    if (s_periodEnd.isEmpty() || s_periodStart.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Не указан промежуток");
                        throw new ParseException("", 1);
                    }

                    datePeriodStart = f.parse(s_periodStart);
                    datePeriodEnd = f.parse(s_periodEnd);
                } catch (ParseException exp) {
                    exp.printStackTrace();
                }


                if (datePeriodEnd == null || datePeriodStart == null) {
                    JOptionPane.showMessageDialog(null,"Не удалось вычислить дату.");
                    return;
                }

                if (datePeriodStart.after(datePeriodEnd)) {
                    JOptionPane.showMessageDialog(null,"Стартовая дата не может идти после конечной.");
                    return;
                }

                if (datePeriodEnd.after(dateCurrentTime)) {
                    JOptionPane.showMessageDialog(null,"Конечная дата не может быть в будущем.");
                    return;
                }

                if (datePeriodStart.equals(datePeriodEnd)) {
                    JOptionPane.showMessageDialog(null,"Стартовая дата не может совпадать с конечной.");
                    return;
                }


                if (datePeriodStart.before(allStatsDateBegin)) {
                    JOptionPane.showMessageDialog(null,"Начало работы отдела: " + f.format(allStatsDateBegin));
                    return;
                }

                int summaryDealCount = 0;
                int summaryLoanCount = 0;
                for (Integer id : uniqueIdsFromStats) {
                    Pair countForManager = getStatsForManager(id, datePeriodStart, datePeriodEnd);
                    summaryDealCount += countForManager.dealCount;
                    summaryLoanCount += countForManager.loanCount;
                }

                periodInfoLabel.setText("<html>За этот интервал в отделе произошло " +
                                 summaryDealCount +
                                " сделок </br> на сумму " +
                                 summaryLoanCount +
                                ".</html>");

            }
        });

        JButton createReports = new JButton("Создать отчеты");
        createReports.setBounds(10, 270, 200, 20);
        add(createReports);
        createReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PdfThread threadDeals = new PdfThread("deals", app.deals);
                PdfThread threadManagers = new PdfThread("managers", app.managers);
                PdfThread threadClients = new PdfThread("clients", app.clients);

                threadDeals.start();
                threadClients.start();
                threadManagers.start();

                threadDeals.join();
                threadClients.join();
                threadManagers.join();
            }
        });

        uiApp.logger.info("tab reporting start");
    }

    public void updateInfo() {
        deals = 0;
        for (int i = 0; i < app.managers.size(); i++) {
            deals += app.managers.get(i).getCountDeals();
        }

        loans = 0;
        for (int i = 0; i < app.managers.size(); i++) {
            loans += app.managers.get(i).getCountLoans();
        }

        countDeals.setText("Общее количесто заключенных сделок: " + deals);
        countLoans.setText("Общая сумма выданных кредитов: " + loans);
        countManagers.setText("Количество менеджеров в отделе: " + app.managers.size());
        countClients.setText("Количество клиентов в отделе: " + app.clients.size());
    }


    private Pair getStatsForManager(int idManager, Date periodStart, Date periodEnd) {
        List<ManagerStats> statsForManager = entityManager
                .createQuery("from ManagerStats where manager_id=" + idManager + " order by id")
                .getResultList();
        if (statsForManager == null || statsForManager.size() == 0) {
            return new Pair(0, 0);
        }

        Date thisManagerPeriodStart = statsForManager.get(0).getDate();
        if (periodStart.before(thisManagerPeriodStart) && thisManagerPeriodStart.before(periodEnd)) {
            periodStart = thisManagerPeriodStart;
        }


//        ManagerStats managerStartStats = statsForManager.get(0);
//        ManagerStats managerStopStats = statsForManager.get(statsForManager.size() - 1);
//        boolean isFindStart = true;
//
//        for (ManagerStats stat : statsForManager) {
//            if (isFindStart && stat.getDate().compareTo(periodStart) == 0) {
//                managerStartStats = stat;
//                isFindStart = false;
//            }
//
//            if (isFindStart && stat.getDate().before(periodStart)) {
//                managerStartStats = stat;
//            }
//
//
//            if (stat.getDate().compareTo(periodEnd) == 0) {
//                managerStopStats = stat;
//                break;
//            }
//
//            if (stat.getDate().before(periodEnd)) {
//                managerStopStats = stat;
//            }
//        }
//
//
//        int dealCount;
//        int loanCount;
//
//        //если даты начала и конца совпадают
//        if (managerStartStats.getDate().compareTo(managerStopStats.getDate()) == 0) {
//            //если начало это самая первая сделка менеджера
//            if (managerStartStats.getDate().compareTo(statsForManager.get(0).getDate()) == 0) {
//                dealCount = managerStartStats.getManager_deal_count();
//                loanCount = managerStartStats.getManager_loan_count();
//            } else {
//                int managerStartIndex = statsForManager.indexOf(managerStartStats);
//                ManagerStats tempStart = statsForManager.get(managerStartIndex - 1);
//
//                dealCount = managerStopStats.getManager_deal_count() - tempStart.getManager_deal_count();
//                loanCount = managerStopStats.getManager_loan_count() - tempStart.getManager_loan_count();
//            }
//        } else {
//            dealCount = managerStopStats.getManager_deal_count() - managerStartStats.getManager_deal_count();
//            loanCount = managerStopStats.getManager_loan_count() - managerStartStats.getManager_loan_count();
//        }
        ManagerStats managerStart = statsForManager.get(0);
        ManagerStats managerStop = statsForManager.get(statsForManager.size() - 1);
        boolean isFindStart = true;

        for (ManagerStats stat : statsForManager) {
            if (isFindStart && stat.getDate().compareTo(periodStart) == 0) {
                managerStart = stat;
                isFindStart = false;
            }

            if (isFindStart && stat.getDate().before(periodStart)) {
                managerStart = stat;
            }


            if (stat.getDate().compareTo(periodEnd) == 0) {
                managerStop = stat;
                break;
            }

            if (stat.getDate().before(periodEnd)) {
                managerStop = stat;
            }
        }

        int dealCount;
        int loanCount;

        //если даты начала и конца совпадают
        if (managerStart.getDate().compareTo(periodEnd) == 0) {
            //если начало это самая первая сделка менеджера
            if (managerStart.getDate().compareTo(statsForManager.get(0).getDate()) == 0) {
                dealCount = managerStart.getManager_deal_count();
                loanCount = managerStart.getManager_loan_count();
            } else {
                int managerStartIndex = statsForManager.indexOf(managerStart);
                ManagerStats tempStart = statsForManager.get(managerStartIndex - 1);

                dealCount = managerStop.getManager_deal_count() - tempStart.getManager_deal_count();
                loanCount = managerStop.getManager_loan_count() - tempStart.getManager_loan_count();
            }
        } else {
            if (periodStart.compareTo(managerStart.getDate()) == 0) {
                dealCount = managerStop.getManager_deal_count();
                loanCount = managerStop.getManager_loan_count();
            } else {
                dealCount = managerStop.getManager_deal_count() - managerStart.getManager_deal_count();
                loanCount = managerStop.getManager_loan_count() - managerStart.getManager_loan_count();
            }
        }

        return new Pair(dealCount, loanCount);
    }
}
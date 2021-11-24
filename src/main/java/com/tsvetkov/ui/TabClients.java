package com.tsvetkov.ui;

import com.tsvetkov.bd.Client;
import com.tsvetkov.bd.Deal;
import com.tsvetkov.bd.Manager;
import com.tsvetkov.util.ErrorFormException;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class TabClients extends JPanel {

    private uiApp app;
    private EntityManager entityManager;
    private DefaultListModel<Client> modelList;
    private JList jlist;
    private int sortLastState = 0;
    private boolean debtorsState = false;

    public TabClients (uiApp app, EntityManager entityManager) {
        this.app = app;
        this.entityManager = entityManager;


        entityManager.getTransaction().begin();
        app.clients = entityManager.createQuery("from Client").getResultList();
        entityManager.getTransaction().commit();

        modelList = new DefaultListModel<>();
        jlist = new JList(modelList);
        jlist.setCellRenderer(new ListItemRenderer("client.jpg"));
        for (Client c : app.clients) {
            modelList.addElement(c);
        }




        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Rectangle r = jlist.getCellBounds(0, jlist.getLastVisibleIndex());
                    if (r != null && r.contains(e.getPoint())) {
                        onClickClient((Client) jlist.getSelectedValue());
                    }
                }
            }
        });

        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(jlist);
        add(scroll, BorderLayout.CENTER);

        JPanel comboPanel = new JPanel();
        //comboPanel.setLayout(new BorderLayout());

        JButton addNewManagerButton = new JButton();
        addNewManagerButton.setText("Добавить нового клиента");
        addNewManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (app.managers.size() == 0) {
                    JOptionPane.showMessageDialog(null,"Сначала добавьте менеджера.");
                    uiApp.logger.warn("adding new client abort");
                    return;
                }

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

                JLabel clientmanager = new JLabel("Обслуживающий менеджер");
                clientmanager.setBounds(25, 115, 200, 20);

                String[] arr = new String[app.managers.size()];
                for (int i = 0; i < app.managers.size(); i++) {
                    arr[i] = app.managers.get(i).toString();
                }
                JComboBox box = new JComboBox(arr);
                box.setBounds(25, 135, 220, 20);

                JButton ok = new JButton("Добавить");
                ok.setBounds(100, 175, 100, 20);
                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Client client = new Client();

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

                        if (milliseconds == -1 || app.managers.size() == 0) {
                            JOptionPane.showMessageDialog(null, "Ошибка в данных");
                            uiApp.logger.warn("adding new client abort");
                            return;
                        }


                        try {
                            client.setFirstName(firstNameText.getText());
                            client.setSecondName(secondNameText.getText());

                            long curMillis = System.currentTimeMillis();
                            Date curDate = new Date(curMillis);
                            int age = Period.between(
                                    d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                    curDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            ).getYears();

                            int ind = box.getSelectedIndex();
                            client.setHis_manager_id(app.managers.get(ind).getId());

                            client.setAge(age);
                            client.setDateBirth(new java.sql.Date(milliseconds));
                            client.setHis_count_deals(0);
                            client.setHis_summary_debt(0);

                            entityManager.getTransaction().begin();
                            app.clients.add(client);
                            modelList.addElement(client);
                            entityManager.persist(client);
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
                panel.add(clientmanager);
                panel.add(box);
                panel.add(ok);

                frame.setPreferredSize(new Dimension(300, 250));
                frame.getContentPane().add(panel);
                frame.setVisible(true);
                frame.setResizable(false);
                frame.setTitle("Добавить нового клиента");
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

                JLabel showClientsForOneManager = new JLabel("Показать клиентов выбранного менеджера");
                showClientsForOneManager.setBounds(10, 10, 300, 20);
                panel.add(showClientsForOneManager);

                String[] managersArray = new String[app.managers.size() + 1];
                managersArray[0] = "Для всех";
                for (int i = 0; i < app.managers.size(); i++) {
                    managersArray[i + 1] = app.managers.get(i).toString();
                }
                JComboBox clientManagerBox = new JComboBox(managersArray);
                clientManagerBox.setBounds(10, 30, 220, 20);
                panel.add(clientManagerBox);
                clientManagerBox.setSelectedIndex(sortLastState);
                clientManagerBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sortLastState = clientManagerBox.getSelectedIndex();
                    }
                });

                JCheckBox debtors = new JCheckBox("Должники");
                debtors.setSelected(debtorsState);
                debtors.setBounds(10, 60, 100, 20);
                panel.add(debtors);
                debtors.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        debtorsState = debtors.isSelected();
                    }
                });

                JButton applySort = new JButton("Применить параметры");
                applySort.setBounds(140, 280, 200, 20);
                panel.add(applySort);
                applySort.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        modelList.clear();

                        if (sortLastState == 0) {
                            for (Client c : app.clients) {
                                modelList.addElement(c);
                            }
                        } else {
                            Manager chosenManager = app.managers.get(sortLastState - 1);
                            for (Client c : app.clients) {
                                if (c.getHis_manager_id() == chosenManager.getId()) {
                                    modelList.addElement(c);
                                }
                            }
                        }


                        if (debtorsState) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            String date =  "'" + format.format(new Date(System.currentTimeMillis())) + "'";
                            String queryStr = "from Deal where ( date_payment < " + date + " and redeemed = false )";

                            List<Deal> debtorsDeals = entityManager
                                    .createQuery(queryStr)
                                    .getResultList();

                            boolean temp;
                            for (int i = 0 ; i < modelList.size(); i++) {
                                temp = true;
                                for (Deal d : debtorsDeals) {
                                    if (modelList.get(i).getId() == d.getId_client()) {
                                        temp = false;
                                        break;
                                    }
                                }
                                if (temp) {
                                    modelList.remove(i);
                                    i--;
                                }
                            }
                        }

                        frame.dispose();
                    }
                });
            }
        });


        comboPanel.add(sortButton);
        comboPanel.add(addNewManagerButton);

        add(comboPanel, BorderLayout.SOUTH);

        uiApp.logger.info("tab clients start");
    }

    private void onClickClient(Client currentClient) {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(480, 360));
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation(700, 300);
        frame.setTitle(currentClient.toString());

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel age = new JLabel("Возраст: " + currentClient.getAge() + " лет");
        age.setBounds(10, 10, 300, 20);
        panel.add(age);


        JLabel clientManager = new JLabel("Менеджер этого клиента: ");
        clientManager.setBounds(10, 40, 200, 20);
        panel.add(clientManager);

        int selectedInd = 0;
        String[] managersArray = new String[app.managers.size()];
        for (int i = 0; i < app.managers.size(); i++) {
            managersArray[i] = app.managers.get(i).toString();
            if (app.managers.get(i).getId() == currentClient.getHis_manager_id()) {
                selectedInd = i;
            }
        }
        JComboBox clientManagerBox = new JComboBox(managersArray);
        clientManagerBox.setBounds(10, 60, 220, 20);
        clientManagerBox.setSelectedIndex(selectedInd);
        panel.add(clientManagerBox);
        clientManagerBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ind = clientManagerBox.getSelectedIndex();
                Manager manager = app.managers.get(ind);
                currentClient.setHis_manager_id(manager.getId());
                entityManager.getTransaction().begin();
                entityManager.persist(currentClient);
                entityManager.getTransaction().commit();
            }
        });


        DefaultListModel<String> paymentDates = new DefaultListModel<>();

        JList<String> listPaymentDates = new JList<String>(paymentDates);

        JScrollPane scrollPaymentDates = new JScrollPane(listPaymentDates);
        scrollPaymentDates.setBounds(10, 130, 300, 100);
        panel.add(scrollPaymentDates);

        JCheckBox checkDealsNotRedeemed = new JCheckBox("Показывать только просроченные");
        checkDealsNotRedeemed.setBounds(10, 100, 300, 20);
        panel.add(checkDealsNotRedeemed);
        checkDealsNotRedeemed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paymentDates.clear();

                if (currentClient.getHis_summary_debt() != 0) {
                    paymentDates.addElement("Суммарный долг банку " + currentClient.getHis_summary_debt());
                }

                if (checkDealsNotRedeemed.isSelected()) {
                    LocalDate now = LocalDate.now();
                    Date dateNow = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

                    for (Deal d : app.deals) {
                        if (d.getId_client() == currentClient.getId() && !d.getRedeemed() && d.getDateOfPayment().before(dateNow)) {
                            paymentDates.addElement("Дата выплаты по " + d.toString() + " - " + d.getDateOfPayment().toString());
                            paymentDates.addElement("Долг по " + d.toString() + " - " + d.getCurrentDebt());
                        }
                    }
                } else {
                    for (Deal d : app.deals) {
                        if (d.getId_client() == currentClient.getId() && !d.getRedeemed()) {
                            paymentDates.addElement("Дата выплаты по " + d.toString() + " - " + d.getDateOfPayment().toString());
                            paymentDates.addElement("Долг по " + d.toString() + " - " + d.getCurrentDebt());
                        }
                    }
                }

                if (paymentDates.isEmpty()) {
                    paymentDates.addElement("Никаких договоров еще не заключено");
                    paymentDates.addElement("или все погашено");
                }

            }
        });
        checkDealsNotRedeemed.setSelected(true);
        checkDealsNotRedeemed.doClick();


        JButton remove = new JButton("Удалить клиента");
        remove.setBounds(140, 280, 200, 20);
        panel.add(remove);
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isFree = true;

                for (int i = 0; i < app.deals.size(); i++) {
                    if (app.deals.get(i).getId_client() == currentClient.getId()) {
                        isFree = false;
                        break;
                    }
                }

                if (isFree) {
                    entityManager.getTransaction().begin();
                    entityManager.remove(currentClient);
                    entityManager.getTransaction().commit();

                    modelList.remove(modelList.indexOf(currentClient));
                    app.clients.remove(currentClient);

                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,"Нельзя удалить клиента, на котором числятся договоры.\nУдалите сначала договоры.");
                }
            }
        });

        frame.pack();
    }
}

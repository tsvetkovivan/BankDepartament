package com.tsvetkov.ui;

import com.tsvetkov.bd.Client;
import com.tsvetkov.bd.Deal;
import com.tsvetkov.bd.Manager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class uiApp extends JFrame {
    public static final int WIDTH  = 640;
    public static final int HEIGHT = 480;
    private static final String TITLE = "Banking Department";

    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;

    public static final String TAB_MANAGERS  = "Менеджеры отдела";
    public static final String TAB_CLIENTS   = "Клиента отдела";
    public static final String TAB_REPORTING = "Отчетность";
    public static final String TAB_DEALS     = "Договоры";

    public List<Manager> managers;
    public List<Client> clients;
    public List<Deal> deals;

    public final static Logger logger = Logger.getLogger(uiApp.class);

    public uiApp() {
        super(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        SCREEN_WIDTH = dimension.width;
        SCREEN_HEIGHT = dimension.height;
        setLocation(SCREEN_WIDTH/2 - WIDTH/2, SCREEN_HEIGHT/2 - HEIGHT/2);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("test_persistence");
        EntityManager entityManager = entityManagerFactory.createEntityManager();



        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(TAB_MANAGERS, new TabManagers(this, entityManager));
        tabbedPane.addTab(TAB_CLIENTS, new TabClients(this, entityManager));
        tabbedPane.addTab(TAB_DEALS, new TabDeals(this, entityManager));
        TabReporting reporting = new TabReporting(this, entityManager);
        tabbedPane.addTab(TAB_REPORTING, reporting);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 3) {
                    reporting.updateInfo();
                    reporting.repaint();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(panel);

        setVisible(true);
        pack();

        logger.info("app start");
    }
}

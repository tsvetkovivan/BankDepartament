package com.tsvetkov.util;

import com.itextpdf.text.pdf.PdfPTable;

import java.util.List;

public class PdfThread {
    private PdfPTable table;
    private myThread tableThread;
    private myThread writeThread;

    private class myThread extends Thread {
        Object mutex;

        public myThread(Object mutex, Runnable runnable) {
            super(runnable);
            this.mutex = mutex;
        }
    }

    public PdfThread(String which, List list) {
        table = new PdfPTable(1);

        tableThread = new myThread(table, new Runnable() {
            @Override
            public void run() {
                synchronized(tableThread.mutex) {
                    switch (which) {
                        case "clients" -> table = ReportCreater.reportClients(list);
                        case "managers" -> table = ReportCreater.reportManagers(list);
                        case "deals" -> table = ReportCreater.reportDeals(list);
                    }
                    tableThread.mutex.notify();
                }
            }
        });

        writeThread = new myThread(table, new Runnable() {
            @Override
            public void run() {
                synchronized (writeThread.mutex) {
                    ReportCreater.reportKernel(which, table);
                    writeThread.mutex.notify();
                }
            }
        });

    }

    public void start() {
        tableThread.start();
        writeThread.start();
    }

    public void join() {
        try {
            //tableThread.join();
            writeThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

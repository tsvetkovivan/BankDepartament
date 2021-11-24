package com.tsvetkov.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tsvetkov.bd.Client;
import com.tsvetkov.bd.Deal;
import com.tsvetkov.bd.Manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.io.FileOutputStream;

public class ReportCreater {
    private static String DEF_REPORT_DIR = "C:\\Users\\tsvetkov\\itextpdf";

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    //private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
    //private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    //private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);



    public static PdfPTable reportClients(List<Client> clients) {
        PdfPTable table = new PdfPTable(8);

        table.addCell("ID");
        table.addCell("first name");
        table.addCell("second name");
        table.addCell("date birth");
        table.addCell("age");
        table.addCell("serving manager ID");
        table.addCell("count deals");
        table.addCell("count summary debt");

        if (clients != null) {
            for (Client c : clients) {
                table.addCell(Integer.toString(c.getId()));
                table.addCell(c.getFirstName());
                table.addCell(c.getSecondName());
                table.addCell(c.getDateBirth().toString());
                table.addCell(Integer.toString(c.getAge()));
                table.addCell(Integer.toString(c.getHis_manager_id()));
                table.addCell(Integer.toString(c.getHis_count_deals()));
                table.addCell(Integer.toString(c.getHis_summary_debt()));
            }
        }

        //reportKernel("clients", table);
        return table;
    }

    public static PdfPTable reportManagers(List<Manager> managers) {
        PdfPTable table = new PdfPTable(7);

        table.addCell("ID");
        table.addCell("first name");
        table.addCell("second name");
        table.addCell("date birth");
        table.addCell("age");
        table.addCell("count deals");
        table.addCell("count loans");

        if (managers != null) {
            for (Manager c : managers) {
                table.addCell(Integer.toString(c.getId()));
                table.addCell(c.getFirstName());
                table.addCell(c.getSecondName());
                table.addCell(c.getDateBirth().toString());
                table.addCell(Integer.toString(c.getAge()));
                table.addCell(Integer.toString(c.getCountDeals()));
                table.addCell(Integer.toString(c.getCountLoans()));
            }
        }

        //reportKernel("managers", table);
        return table;
    }

    public static PdfPTable reportDeals(List<Deal> deals) {
        PdfPTable table = new PdfPTable(9);

        table.addCell("ID");
        table.addCell("serving manager id");
        table.addCell("serving client id");
        table.addCell("date sign");
        table.addCell("date next payment");
        table.addCell("amount loan");
        table.addCell("current debt");
        table.addCell("redeemed");
        table.addCell("paid this month");

        if (deals != null) {
            for (Deal c : deals) {
                table.addCell(Integer.toString(c.getId_deal_key()));
                table.addCell(Integer.toString(c.getId_manager()));
                table.addCell(Integer.toString(c.getId_client()));
                table.addCell(c.getDateOfSighDeal().toString());
                table.addCell(c.getDateOfPayment().toString());
                table.addCell(Integer.toString(c.getTotalAmountOfLoan()));
                table.addCell(Integer.toString(c.getCurrentDebt()));
                table.addCell(Boolean.toString(c.getRedeemed()));
                table.addCell(Boolean.toString(c.getPaidThisSection()));
            }
        }

        //reportKernel("deals", table);
        return table;
    }

    public static void reportKernel(String which, PdfPTable table) {
        try {
            long millis = System.currentTimeMillis();
            Date currentDate = new Date(millis);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
            String date = formatter.format(currentDate);

            Document document = new Document();
            PdfWriter.getInstance(document,
                    new FileOutputStream(DEF_REPORT_DIR + "\\" + which + "_" + date + ".pdf"));
            document.open();

            document.addTitle("Report");

            Anchor anchor = new Anchor("Report " + which + " " + date, catFont);
//            anchor.setName("Report " + which);

            Chapter catPart = new Chapter(new Paragraph(anchor), 1);

            catPart.add(Chunk.NEWLINE);


            catPart.add(table);


            // now add all this to the document
            document.add(catPart);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

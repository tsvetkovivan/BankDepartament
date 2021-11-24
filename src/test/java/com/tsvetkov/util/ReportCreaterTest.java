package com.tsvetkov.util;

import com.itextpdf.text.pdf.PdfPTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportCreaterTest {

    @Test
    void reportClients() {
        PdfPTable table = ReportCreater.reportClients(null);
        assertNotNull(table);
    }

    @Test
    void reportManagers() {
        PdfPTable table = ReportCreater.reportManagers(null);
        assertNotNull(table);
    }

    @Test
    void reportDeals() {
        PdfPTable table = ReportCreater.reportDeals(null);
        assertNotNull(table);
    }
}
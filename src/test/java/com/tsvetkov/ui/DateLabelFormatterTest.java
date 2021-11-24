package com.tsvetkov.ui;

import org.junit.jupiter.api.Test;

import java.security.spec.ECField;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateLabelFormatterTest {

    @Test
    void stringToValue() {
        String testDate = "2021-02-15";
        DateLabelFormatter formatter = new DateLabelFormatter();

        try {
            Object date = formatter.stringToValue(testDate);
            assertNotNull(date);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void valueToString() {
        Calendar calendar = Calendar.getInstance();
        DateLabelFormatter formatter = new DateLabelFormatter();

        try {
            String str = formatter.valueToString(calendar);

            assertNotNull(str);
            assertNotEquals(str, "");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
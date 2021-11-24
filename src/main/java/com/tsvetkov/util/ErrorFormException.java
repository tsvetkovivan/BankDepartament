package com.tsvetkov.util;

import com.tsvetkov.ui.uiApp;

import javax.swing.*;

public class ErrorFormException extends Exception {
    public ErrorFormException(String message) {
        super(message);
        uiApp.logger.error("new Error Form Exception");
    }

    public void showErrorOnScreen() {
        JOptionPane.showMessageDialog(null, super.getMessage());
    }
}

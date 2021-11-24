package com.tsvetkov;

import com.tsvetkov.ui.uiApp;

import java.util.LinkedHashSet;

import java.util.Set;

public class App {
    public static void main(String[] args)     {
        try {
            new uiApp();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Can not connect to BD");
        }
    }

}
package ru.shlomeno4ek.familybudget;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by шлома-сн on 06.02.2017.
 */

public class Purse {
    public static int count = 0;
    private final static ArrayList<String> operations = new ArrayList<>();

    private int id;
    private String name;
    private double balans;
    private double reserve;

    public Purse(String name, int balans, int reserve) {
        this.name = name;
        this.balans = balans;
        this.reserve = reserve;
        id = count++;
    }

    public void addOperationInPurse(Budget budget, int typeOperation) {
        StringBuilder sb = new StringBuilder();
        String star = "*";
        sb.append(this.getId()).append(star).
                append(typeOperation).append(star).
                append(budget.getSummBudget()).append(star).
                append(budget.getNameBudget()).append(star).
                append(budget.getDateBudget());

        operations.add(sb.toString());
    }

    public static ArrayList<String> getOperations() {
        return operations;
    }

    public int getId() {
        return id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalans() {
        return balans;
    }

    public void setBalans(double balans) {
        this.balans = balans;
    }

    public double getReserve() {
        return reserve;
    }

    public void setReserve(double reserve) {
        this.reserve = reserve;
    }
}

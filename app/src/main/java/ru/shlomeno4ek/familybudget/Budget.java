package ru.shlomeno4ek.familybudget;

import java.util.Date;

/**
 * Created by шлома-сн on 06.02.2017.
 */

public class Budget {
    public static final int OPERATION_WITH_PURSE_INCOME = 0;
    public static final int OPERATION_WITH_PURSE_COSTS = 1;
    public static final int OPERATION_WITH_PURSE_TRANSLATION = 2;

    private String nameBudget;
    private int summBudget;
    private Date dateBudget;

    public Budget(String nameBudget, int summBudget, Date dateBudget) {
        this.nameBudget = nameBudget;
        this.summBudget = summBudget;
        this.dateBudget = dateBudget;
    }

    @Override
    public String toString() {
        return summBudget + " руб. " + nameBudget + " от " + dateBudget;
    }

    public String getNameBudget() {
        return nameBudget;
    }

    public void setNameBudget(String nameBudget) {
        this.nameBudget = nameBudget;
    }

    public int getSummBudget() {
        return summBudget;
    }

    public void setSummBudget(int summBudget) {
        this.summBudget = summBudget;
    }

    public Date getDateBudget() {
        return dateBudget;
    }

    public void setDateBudget(Date dateBudget) {
        this.dateBudget = dateBudget;
    }
}

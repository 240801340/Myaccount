package com.example.accountkeeping.Data;



import com.example.accountkeeping.Util.TimeTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DayAccountRecord implements Serializable {
    private Calendar calendar;//日期
    private Double income;//收入
    private Double expend;//支出
    private List<AccountRecord> accountRecords;//收支记录数组


    public DayAccountRecord(String dateStr, Double income, Double expend, List<AccountRecord> accountRecords) {
        this.calendar = Calendar.getInstance();
        getCalendarFromStr(dateStr,this.calendar);
        this.income = income;
        this.expend = expend;
        this.accountRecords = accountRecords;
    }

    public void getCalendarFromStr(String dateStr,Calendar calendar){
        TimeTools.setCalendarFromStr(dateStr,calendar,0);
    }


    public String getDate() {
        return TimeTools.getDate(this.calendar);
    }



    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getExpend() {
        return expend;
    }

    public void setExpend(Double expend) {
        this.expend = expend;
    }

    public List<AccountRecord> getAccountRecords() {
        return accountRecords;
    }

    public void setAccountRecords(List<AccountRecord> accountRecords) {
        this.accountRecords = accountRecords;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    //重写Object的clone方法(深拷贝)
    public DayAccountRecord clone() {
        DayAccountRecord obj=new DayAccountRecord(getDate(), getIncome(), getExpend(), getAccountRecords());
        return obj;
    }
}

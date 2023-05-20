package com.example.accountkeeping.Data;


import com.example.accountkeeping.Util.TimeTools;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AllAccountManager implements Serializable {

    private Double pure;//总纯收入
    private Double all_income;//月总收入
    private Double all_expend;//月总支出
    List<DayAccountRecord> dayAccountRecords;//双层数组，外层是日期，内层是记录

    private Calendar calendar;

    public AllAccountManager() {
        this.dayAccountRecords = new ArrayList<>();
    }
    //读取数据结构后更新数据 used in DataBank
    public void updateRestAbbr(){
        this.pure=0.0;
        this.all_income=0.0;
        this.all_expend=0.0;
        for(int i = 0; i<this.dayAccountRecords.size();i++){
            List<AccountRecord> accountRecords =this.dayAccountRecords.get(i).getAccountRecords();
            for(int j = 0 ;j<accountRecords.size();j++){
                Double money = accountRecords.get(j).getMoney();
                if(TimeTools.checkCalendarSameMonth(this.calendar,this.dayAccountRecords.get(i).getCalendar())){
                    if(money>0){
                        this.all_income+=money;
                    }else{
                        this.all_expend+=money;
                    }

                }
                this.pure+=money;
            }

        }
        this.calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

    }

    //增加一条一天的记录
    public int addDayRecord(String dateStr,String activity,String pay_way,int iconId,Double money){
        if(money ==0 )
            return -1;
        this.pure+=money;
        updateMonthData(true,dateStr,money);
        List<AccountRecord> accountRecords =new ArrayList<>();
        int dateIndex = this.dayAccountRecords.size();
        accountRecords.add(0,new AccountRecord(activity,pay_way,iconId,money));
        //找到第一个比加入记录日期晚的
        for(int i=0;i<this.dayAccountRecords.size();i++) {
            if (TimeTools.compareCalendarByDayThroughStr(this.dayAccountRecords.get(i).getCalendar(),dateStr) <=0) {

                dateIndex = i;
                break;
            }

        }
        dayAccountRecords.add(dateIndex,new DayAccountRecord(dateStr,money>0?money:0,money<0?money:0, accountRecords));
        //更新
        return dateIndex;
    }

    //在某一天中增加一条记录
    public void addRecord(String dateStr,int dateIndex,String activity,String pay_way,int iconId,Double money){
        if(0==money )
            return;
        this.pure+=money;
        updateMonthData(true,dateStr,money);
        updateDateData(dateIndex,0.0,money);
        this.dayAccountRecords.get(dateIndex).getAccountRecords().add(0,new AccountRecord(activity,pay_way,iconId,money));
    }

    //判断是否有那一天的记录，如果有返回index
    public int checkDateExist(String dateStr){
        for(int i=0;i<this.dayAccountRecords.size();i++) {
            if (0 == TimeTools.compareCalendarByDayThroughStr(this.dayAccountRecords.get(i).getCalendar(),dateStr)) {
                return i;
            }
        }
        return -1;
    }
    //修改记录
    public void modifyRecord(String dateStr,int dateIndex,int position,String activity,String pay_way,int iconId,Double money){

        Double oldMoney = dayAccountRecords.get(dateIndex).getAccountRecords().get(position).getMoney();
        this.pure+=(-oldMoney);
        updateMonthData(false,TimeTools.getDate(dayAccountRecords.get(dateIndex).getCalendar()),(-oldMoney));
        this.pure+=money;
        updateMonthData(true,dateStr,money);
        updateDateData(dateIndex,oldMoney,money);

        AccountRecord accountRecord = dayAccountRecords.get(dateIndex).getAccountRecords().get(position);
        accountRecord.setActivity(activity);
        accountRecord.setPayWay(pay_way);
        accountRecord.setIconId(iconId);
        accountRecord.setMoney(money);
    }

    //删除单条记录
    public boolean deleteAccountRecord( int dateIndex,int position){
        //更新数据
        Double oldMoney = dayAccountRecords.get(dateIndex).getAccountRecords().get(position).getMoney();
        this.pure+= (-oldMoney);
        updateMonthData(false,TimeTools.getDate(dayAccountRecords.get(dateIndex).getCalendar()),(-oldMoney));

        updateDateData(dateIndex,oldMoney,0.0);
        dayAccountRecords.get(dateIndex).getAccountRecords().remove(position);

        if( dayAccountRecords.get(dateIndex).getAccountRecords().size()<=0){
            dayAccountRecords.remove(dateIndex);
            return  true;
        }else {
            return false;
        }
    }
    //修改月总收入和月总支出 type true表示正常操作，false表示money已取反
    private void updateMonthData(boolean type, String dateStr,Double money){
        if(type){
            if(TimeTools.checkSameMonth(this.calendar,dateStr)){
                if(money>0){
                    this.all_income+=money;
                }else{
                    this.all_expend+=money;
                }
            }
        }else {
            if(TimeTools.checkSameMonth(this.calendar,dateStr)){
                if(money<0){
                    this.all_income+=money;
                }else{
                    this.all_expend+=money;
                }
            }
        }

    }
    //修改日期收入支出
    private void updateDateData(int dateIndex,Double oldMoney,Double newMoney){
        if(oldMoney>0){
            Double income =dayAccountRecords.get(dateIndex).getIncome();
            dayAccountRecords.get(dateIndex).setIncome(income-oldMoney);
        }else{
            Double expend =dayAccountRecords.get(dateIndex).getExpend();
            dayAccountRecords.get(dateIndex).setExpend(expend-oldMoney);
        }
        if(newMoney>0){
            Double income =dayAccountRecords.get(dateIndex).getIncome();
            dayAccountRecords.get(dateIndex).setIncome(income+newMoney);
        }else{
            Double expend =dayAccountRecords.get(dateIndex).getExpend();
            dayAccountRecords.get(dateIndex).setExpend(expend+newMoney);
        }
    }



    //获取当前月所有记录
    public List<DayAccountRecord> getNewCurrentMonthDayRecords(){
        List<DayAccountRecord> currentMonthDayRecords = new ArrayList<>();
        for(int i = 0; i<this.dayAccountRecords.size();i++){
            if(TimeTools.checkCalendarSameMonth(this.calendar,this.dayAccountRecords.get(i).getCalendar())){
                currentMonthDayRecords.add(currentMonthDayRecords.size(),this.dayAccountRecords.get(i));                ;
            }
        }
        return currentMonthDayRecords;
    }

    //获取对应活动分类的所有记录
    public List<DayAccountRecord> getActivityDayRecords(String activity){
        List<DayAccountRecord> activityDayRecords = new ArrayList<>();
        for(int i = 0; i<this.dayAccountRecords.size();i++){
                List<AccountRecord> accountRecords =this.dayAccountRecords.get(i).getAccountRecords() ;
                List<AccountRecord> activityRecords =new ArrayList<>();
                boolean date_copied = false;//每一天只能复制一次日期栏
                for(int j = 0 ;j<accountRecords.size();j++){
                    if(accountRecords.get(j).getActivity().equals(activity)){
                        //保存日期
                        if(!date_copied){
                            activityDayRecords.add(activityDayRecords.size(),this.dayAccountRecords.get(i).clone());
                            activityDayRecords.get(activityDayRecords.size()-1).setAccountRecords(activityRecords);
                            date_copied=true;
                        }
                        //保存当前项
                        activityDayRecords.get(activityDayRecords.size()-1).getAccountRecords().add(0,accountRecords.get(j).clone());
                    }
                }
        }

        return activityDayRecords;
    }
    //获取对应账户的所有记录
    public List<DayAccountRecord> getAccountDayRecords(String account){
        List<DayAccountRecord> activityDayRecords = new ArrayList<>();
        for(int i = 0; i<this.dayAccountRecords.size();i++){
            List<AccountRecord> accountRecords =this.dayAccountRecords.get(i).getAccountRecords() ;
            List<AccountRecord> activityRecords =new ArrayList<>();
            boolean date_copied = false;//每一天只能复制一次日期栏
            for(int j = 0 ;j<accountRecords.size();j++){
                if(accountRecords.get(j).getPayWay().equals(account)){
                    //保存日期
                    if(!date_copied){
                        activityDayRecords.add(activityDayRecords.size(),this.dayAccountRecords.get(i).clone());
                        activityDayRecords.get(activityDayRecords.size()-1).setAccountRecords(activityRecords);
                        date_copied=true;
                    }
                    //保存当前项
                    activityDayRecords.get(activityDayRecords.size()-1).getAccountRecords().add(0,accountRecords.get(j).clone());
                }
            }
        }

        return activityDayRecords;
    }

    //获取时间段的所有记录
    public List<DayAccountRecord> getHistoryDayRecords(String startDate , String endDate){
        List<DayAccountRecord> activityDayRecords = new ArrayList<>();
        for(int i = 0; i<this.dayAccountRecords.size();i++){
                Calendar calendar = this.dayAccountRecords.get(i).getCalendar();
                if( TimeTools.compareCalendarByDayThroughStr(calendar,startDate)>0 &&  TimeTools.compareCalendarByDayThroughStr(calendar,endDate)<0 ){
                        activityDayRecords.add(activityDayRecords.size(),this.dayAccountRecords.get(i).clone());
                }
        }

        return activityDayRecords;
    }


    //获取统计数据
    public HashMap getAnalysis(List<DayAccountRecord> _dayAccountRecords){
        HashMap<String,Double> analysis = new HashMap<String,Double>();
        Double income = 0.0;
        Double expend = 0.0;
        for(int i = 0; i<_dayAccountRecords.size();i++){
            for(int j = 0 ;j<_dayAccountRecords.get(i).getAccountRecords().size();j++){
                Double money = _dayAccountRecords.get(i).getAccountRecords().get(j).getMoney();
                if(money>0){
                    income+=money;
                }else{
                    expend+=money;
                }
            }
        }
        analysis.put("income",income);
        analysis.put("expend",expend);
        return analysis;
    }






    public Double getPure() {
        return  BigDecimal.valueOf(this.pure).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    public void setPure(Double pure) {
        this.pure=pure;

    }

    public Double getAll_income() {
        return  BigDecimal.valueOf(this.all_income).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    public void setAll_income(Double all_income) {
        this.all_income = all_income;
    }

    public Double getAll_expend() {
        return  BigDecimal.valueOf(this.all_expend).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    public void setAll_expend(Double all_expend) {
        this.all_expend = all_expend;
    }

    public List<DayAccountRecord> getDayAccountRecords() {
        return dayAccountRecords;
    }

    public void setDayAccountRecords(List<DayAccountRecord> dayAccountRecords) {
        this.dayAccountRecords = dayAccountRecords;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }



}

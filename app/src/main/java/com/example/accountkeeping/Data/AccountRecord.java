package com.example.accountkeeping.Data;

import java.io.Serializable;
import com.example.accountkeeping.R;
public class AccountRecord implements Serializable {


    //活动
    private String activity;
    //支付方式
    private String pay_way;
    //活动图的id号
    private int iconId;
    //金额
    private Double money;


    public AccountRecord(String activity,String payWay,int iconId,Double money) {
        this.activity=activity;
        this.pay_way=payWay;
        this.iconId=iconId;
        this.money = money;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getPayWay() {
        return pay_way;
    }

    public void setPayWay(String pay_way) {
        this.pay_way = pay_way;
    }


    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }


    //重写Object的clone方法(深拷贝)
    public AccountRecord clone() {
        AccountRecord obj=new AccountRecord(getActivity(),getPayWay(),getIconId(),getMoney());

        return obj;
    }
}

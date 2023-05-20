package com.example.accountkeeping.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTools {

    //按照日期字符串设置日历
    public static void setCalendarFromStr(String dateStr, Calendar calendar,int choice){
        //choice 表示设置的是年月日(0) 还是 年月(1) 还是 年(2)
        if (dateStr==null||dateStr.equals("")) {
            calendar.setTimeInMillis(System.currentTimeMillis());
        } else {
            String[ ]  dateDivide = dateStr.split("-");
            if(dateDivide.length==3){
                int year = Integer.parseInt(dateDivide [0].trim());//去掉空格
                int month = Integer.parseInt(dateDivide [1].trim());
                int day = Integer.parseInt(dateDivide [2].trim());
                switch (choice){
                    case 0:
                        calendar.set(year, month-1, day);//设定日历的日期
                        break;
                    case 1:
                        calendar.set(Calendar.YEAR,year);//设定日历的年
                        calendar.set(Calendar.MONTH, month-1);//设定日历的月
                        break;
                    case 2:
                        calendar.set(Calendar.YEAR,year);//设定日历的日期
                        break;
                }

            }
        }

    }
    //对比字符串中的时间和日历中日期大小
    public static int compareCalendarByDayThroughStr( Calendar calendar1, String calendar2String){
        Calendar new_calendar= (Calendar) calendar1.clone();
        TimeTools.setCalendarFromStr(calendar2String,new_calendar,0);
        return calendar1.compareTo(new_calendar);
    }

    //对比字符串中的时间和日历中月份大小（包括年）
    public static int compareCalendarByMonthThroughStr(Calendar calendar1, String calendar2String){
        Calendar new_calendar= (Calendar) calendar1.clone();
        TimeTools.setCalendarFromStr(calendar2String,new_calendar,1);
        return calendar1.compareTo(new_calendar);

    }

    //对比两个日历月份大小
    public static int compareTwoCalendarSameMonth(Calendar calendar1, Calendar calendar2){
        String dateStr = getDate(calendar2);
        return compareCalendarByMonthThroughStr(calendar1,dateStr);

    }
    //检查日历和日期字符串是否同月
    public static boolean checkSameMonth(Calendar calendar,String dateStr){
        return (0==TimeTools.compareCalendarByMonthThroughStr(calendar,dateStr));
    }

    //检查两个日历是否同月
    public static boolean checkCalendarSameMonth(Calendar calendar1, Calendar calendar2){
        return (0==compareTwoCalendarSameMonth(calendar1,calendar2));

    }

    //获取日期字符串
    public static String getDate(Calendar calendar) {
        Date date=calendar.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}

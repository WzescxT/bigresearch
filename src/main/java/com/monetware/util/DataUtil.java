package com.monetware.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by venbill on 2017/4/7.
 */
public class DataUtil {

    public static Timestamp getThisMonthFirstDay(){
        //获取前月的第一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal_1=Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        Timestamp firstDay = new Timestamp(cal_1.getTime().getTime());
        return firstDay;
    }


}

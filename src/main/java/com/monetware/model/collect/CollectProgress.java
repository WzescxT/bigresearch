package com.monetware.model.collect;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

//temp implemention
//when several users use this website at the same time,use database to record their total urls and current urls
public class CollectProgress {
    public static HashMap<Integer,Integer> totalurls=new HashMap<>();
    public static HashMap<Integer,Integer> crawledurls=new HashMap<>();
    public static HashMap<Integer,Date> starttime=new HashMap<>();
    
}

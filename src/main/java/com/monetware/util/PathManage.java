package com.monetware.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月30日 上午10:21:34 
 *@describle 路径管理
 */
public class PathManage {
	private static String AnalysisTextLibraryRootPath;   
    //初始化，綁定properties中的jdbc屬性值
    static {   
        Properties prop = new Properties();   
        InputStream in = PathManage.class.getResourceAsStream("/application.properties");
        try {   
            prop.load(in);   
            AnalysisTextLibraryRootPath = prop.getProperty("analysis.textlibrary.path").trim();   

        } catch (IOException e) {   
            e.printStackTrace();   
        } 
    }   
    
    public static String getTextLibraryPath(long textlibraryId){
    	return AnalysisTextLibraryRootPath+textlibraryId+".txt";
    	
    	
    	
    	
    }

}

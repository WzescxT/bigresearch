package com.monetware.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;


import com.mysql.jdbc.Connection;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月2日 上午10:38:44 
 *@describle 用于持久化采集信息
 */
public class jdbcUtil {
	
	private static String driver;   
    private static String url;   
    private static String username;
    private static String password;
    private static int batchno;
    //初始化，綁定properties中的jdbc屬性值
    static {   
        Properties prop = new Properties();   
        InputStream in = jdbcUtil.class.getResourceAsStream("/application.properties");
        try {   
            prop.load(in);   
            driver = prop.getProperty("spring.datasource.driver-class-name").trim();   
            url = prop.getProperty("spring.datasource.url").trim();
            username = prop.getProperty("spring.datasource.username").trim();
            password = prop.getProperty("spring.datasource.password").trim();
            batchno = Integer.parseInt(prop.getProperty("spring.datasource.batchno").trim());

        } catch (IOException e) {   
            e.printStackTrace();   
        } 
    }   





    private static Connection getDBConnection() {
    	Connection conn = null;
    	try {
    		Class.forName(driver); //classLoader,加载对应驱动
    		conn = (Connection) DriverManager.getConnection(url, username, password);
    		} catch (ClassNotFoundException e) {
    			e.printStackTrace();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	return conn;
    }



    	public static void createTable(String tableName,String[] columns) throws SQLException{

    		Connection conn = null;

    		Statement stmt = null;

    		String sql = "CREATE TABLE " + tableName +
    					" (id bigint(20) auto_increment primary key,";
    		for (int i = 0; i < columns.length; i++) {
    				sql = sql + "`"+columns[i]+"` text," ;
			}
    		sql = sql.substring(0, sql.length()-1)+")"+" ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    		try{

    			conn = getDBConnection();

    			stmt = conn.createStatement();

    			System.out.println(sql);

    			stmt.execute(sql);

    		}catch(SQLException e){

    			System.out.println(e.getMessage());

    		}finally{

    			if(stmt!=null){

    				stmt.close();

    			}

    			if(conn!=null){

    				conn.close();

    			}

    		}
    	}
    	
    	
    	
    	public static void createTableBySql(String sql) throws SQLException{

    		Connection conn = null;

    		Statement stmt = null;

    		try{

    			conn = getDBConnection();

    			stmt = conn.createStatement();

    			System.out.println(sql);

    			stmt.execute(sql);

    			System.out.println("Table  is created!");

    		}catch(SQLException e){

    			System.out.println(e.getMessage());

    		}finally{

    			if(stmt!=null){

    				stmt.close();

    			}

    			if(conn!=null){

    				conn.close();

    			}

    		}
    	}
    	
    	
    	public static void insertObjs(String tableName,String[] columns ,List<Object[]> columnObjects ) throws SQLException{
			System.out.println("====>持久化数据");
			Connection conn = getDBConnection();
			String sql = "insert into "+tableName +"(";
			for (int i = 0; i < columns.length; i++) {
					sql = sql+columns[i]+",";
			}
			sql = sql.substring(0, sql.length()-1)+") values(";
			for (int i = 0; i < columns.length; i++) {
				sql = sql+"?,";
			}
			sql = sql.substring(0, sql.length()-1)+")";
			
			PreparedStatement ps=null;
			try {
				ps = conn.prepareStatement(sql);
				for(int i=0;i<columnObjects.size();i++){
					for (int j = 0; j < columns.length; j++) {
						ps.setObject(j+1, columnObjects.get(i)[j]);
					}
					ps.addBatch();
					if(i%batchno==0){
						ps.executeBatch();
						ps.clearBatch();
					}
				}
				ps.executeBatch();
				System.out.println("====》执行数据库操作完毕");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				ps.executeBatch();
				ps.clearBatch();
				if(ps!=null){
    				ps.close();
    			}
    			if(conn!=null){
    				conn.close();
    			}
			}

		}

    		public static void insertStrs(String tableName,String[] columns ,List<String[]> columnObjects ) throws SQLException{
    			System.out.println("====>持久化数据");
    			Connection conn = getDBConnection();
    			String sql = "insert into "+tableName +"(";
    			for (int i = 0; i < columns.length; i++) {
						sql = sql+columns[i]+",";
				}
    			sql = sql.substring(0, sql.length()-1)+") values(";
    			for (int i = 0; i < columns.length; i++) {
					sql = sql+"?,";
    			}
    			sql = sql.substring(0, sql.length()-1)+")";
    			
    			PreparedStatement ps=null;
    			try {
    				ps = conn.prepareStatement(sql);
					for(int i=0;i<columnObjects.size();i++){
						for (int j = 0; j < columns.length; j++) {
							ps.setString(j+1, columnObjects.get(i)[j]);
						}
						ps.addBatch();
						if(i%batchno==0){
							ps.executeBatch();
							ps.clearBatch();
						}
					}
					ps.executeBatch();
					System.out.println("====》执行数据库操作完毕");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					ps.executeBatch();
					ps.clearBatch();
					if(ps!=null){
	    				ps.close();
	    			}
	    			if(conn!=null){
	    				conn.close();
	    			}
				}

    		}
    		
    		
    		
    		public static void insertStr(String tableName,String[] columns ,String[] columnValues ) throws SQLException{
    			System.out.println("====>持久化数据");
    			Connection conn = getDBConnection();
    			String sql = "insert into "+tableName +"(";
    			for (int i = 0; i < columns.length; i++) {
						sql = sql+columns[i]+",";
				}
    			sql = sql.substring(0, sql.length()-1)+") values(";
    			for (int i = 0; i < columns.length; i++) {
					sql = sql+"?,";
    			}
    			sql = sql.substring(0, sql.length()-1)+")";
    			
    			PreparedStatement ps=null;
    			try {
    				ps = conn.prepareStatement(sql);
						for (int j = 0; j < columns.length; j++) {
							ps.setString(j+1, columnValues[j]);
						}
						ps.addBatch();
					ps.executeBatch();
					ps.clearBatch();
					System.out.println("====》执行数据库操作完毕");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					ps.executeBatch();
					ps.clearBatch();
					if(ps!=null){
	    				ps.close();
	    			}
	    			if(conn!=null){
	    				conn.close();
	    			}
				}

    		}


}

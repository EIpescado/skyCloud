package org.skyCloud.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by yq on 2017/04/12 16:52.
 * 获取数据库连接 工具类
 */
public class DBUtils {

    private DBUtils(){

    }

    private static Connection conn = null;

    //mysql驱动类
    private final static String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver" ;

    //sqlServer驱动类
    private final static String SQLSERVER_DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver" ;

    //数据库地址
    private final static String url = "jdbc:mysql://localhost:3306/dota2" ;

    /*帐号*/
    private final static String USERNAME = "root";
    /*密码*/
    private final static String PASSWORD = "root";

    public static Connection getConnection(){
        if(conn == null){
            try {
                Class.forName(MYSQL_DRIVER_CLASS_NAME);
                conn = DriverManager.getConnection(url,USERNAME,PASSWORD);
                //不能存在子父类关系 multi-catch
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return conn ;
    }
}

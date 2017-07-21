package org.skyCloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yq on 2017/04/12 16:34.
 * 数据操作工具类 data Manipulation
 */
public class DMUtils {

    private static final Logger logger = LoggerFactory.getLogger(DMUtils.class);

    private DMUtils(){
    }

    /**
     * 执行查询方法
     * @param sql 查询语句
     */
    public static List<Map<String,Object>> executeQuery(String sql){
        Connection conn = DBUtils.getConnection();
        List<Map<String,Object>> list = new ArrayList<>();
        //Statement为每条条Sql语句生成执行计划,只执行一次的SQL语句选择Statement是最好的
        //PreparedStatement用于使用绑定变量重用执行计划,PreparedStatement的第一次执行消耗是很高,性能体现在后面的重复执行
        Statement stmt = null;
        ResultSet rs = null;
        try {
            /*
             * TYPE
                   ResultSet.TYPE_FORWORD_ONLY 结果集的游标只能向下滚动。
                   ResultSet.TYPE_SCROLL_INSENSITIVE 结果集的游标可以上下移动，当数据库变化时，当前结果集不变。
                   ResultSet.TYPE_SCROLL_SENSITIVE 返回可滚动的结果集，当数据库变化时，当前结果集同步改变。
               concurrency
                   ResultSet.CONCUR_READ_ONLY 不能用结果集更新数据库中的表。
                   ResultSet.CONCUR_UPDATETABLE 能用结果集更新数据库中的表。
             */
            // 创建语句
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // 执行查询
            rs = stmt.executeQuery(sql);
            Map map;
            while (rs.next()) {
                map = new LinkedHashMap();
                //列从1开始
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
                    // 取得列名(转换成小写) : 值
                    map.put(rs.getMetaData().getColumnName(i).toLowerCase(), rs.getObject(i));
                }
                list.add(map);
            }
        } catch (SQLException e) {
            logger.error("查询错误 {}",e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

        }
        return list;
    }

    public static void main(String[] args) throws Exception{
        POIUtils.export(executeQuery("SELECT * from currency"),new FileOutputStream("D:/A/cur.xlsx"),false);
    }
}

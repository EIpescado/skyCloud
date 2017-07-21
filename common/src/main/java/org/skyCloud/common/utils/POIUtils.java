package org.skyCloud.common.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.skyCloud.common.constants.EncodeConsts;
import org.skyCloud.common.constants.FileConsts;
import org.skyCloud.common.constants.POIConsts;
import org.skyCloud.common.dataWrapper.BackResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yq on 2016/12/07 18:11.
 */
public class POIUtils {

    private static final Logger logger = LoggerFactory.getLogger(POIUtils.class);

    private static final String NOT_NULL = "notnull";
    private static final String DATE = "date";
    private static final String NUMBER = "number";

    //导入模版日期默认格式
    private static final String DEFAULT_DATE_FORMAT_TYPE ="1";

    //日期格式化类型:日期格式化字符串map
    private static final Map<String,String> DATE_FORMAT_TYPE_STRING_MAP = new HashMap<String,String>(){
        {
            put("1","yyyy-MM-dd");
            put("2","yyyy/MM/dd");
            put("3","yyyy-MM-dd HH:mm:ss");
            put("4","yyyy/MM/dd HH:mm:ss");
        }
    };

    private POIUtils() {
    }

    /**
     * 利用JAVA的反射机制，将集合中的数据输出到指定IO流中
     * 默认表格标题为 Sheet1
     * 默认日期转化格式为 yyyy-MM-dd
     * @param headers    列标题
     * @param showNames  需要导出的属性名
     * @param clazz      导出实体class
     * @param data   数据集
     * @param out        输出流
     * @throws Exception
     */
    public  static void exportExcel(String[] headers, String[] showNames, Class clazz, Collection data, OutputStream out) throws Exception {
        export("Sheet1", headers,showNames,clazz, data, out ,true);
    }

    /**
     * 单表导出xls 固定了一些样式: 标题 正文 字体 行高
     */
    public static void exportXls(String[] headers, String[] showNames, Class clazz, Collection data, HttpServletResponse response, String fileName) throws Exception{
        //attachment  和 inline 区别 ; attachment 下载附件  ,inline可在浏览器打开
        response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(), EncodeConsts.ISO8859_1) + DateUtils.parse2LongString(new Date()) + FileConsts.DOT + FileConsts.XLS);
        response.setContentType("application/x-excel");
        export("Sheet1", headers,showNames,clazz, data, response.getOutputStream(),true);
    }

    /**
     * 单表导出xlsx
     */
    public static void exportXlsx(String[] headers, String[] showNames, Class clazz, Collection data, HttpServletResponse response, String fileName) throws Exception{
        //attachment  和 inline 区别 ; attachment 下载附件  ,inline可在浏览器打开
        response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(), EncodeConsts.ISO8859_1) + DateUtils.parse2LongString(new Date()) + FileConsts.DOT + FileConsts.XLSX);
        response.setContentType("application/x-excel");
        export("Sheet1", headers,showNames,clazz, data, response.getOutputStream(),false);
    }

    /**
     * 根据是否xls返回不同Workbook对象
     * @param isXls
     */
    public static Workbook createWorkbook(boolean isXls){
        if(isXls){
            // 声明一个工作薄
            return new HSSFWorkbook();
        }else {
            return new XSSFWorkbook();
        }
    }

    /**
     * 根据字体创建单元格样式  默认(水平居中 垂直居中)
     * @param workbook
     * @param font
     */
    public static CellStyle createCellStyle(Workbook workbook, Font font){
        // 标题样式 水平居中 垂直居中
        CellStyle titleStyle  = workbook.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//XSSFCellStyle.ALIGN_CENTER
        titleStyle.setVerticalAlignment( HSSFCellStyle.VERTICAL_CENTER);//XSSFCellStyle.VERTICAL_CENTER
        // 把字体应用到当前的样式
        titleStyle.setFont(font);
        return titleStyle ;
    }

    /**
     * 获取货币样式 保留4位小数
     */
    public static CellStyle createMoneyStyleRound4(Workbook workbook, Font font){
        //金钱数字样式
        CellStyle moneyStyle = createCellStyle(workbook,font);
        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.0000"));//保留四位小数 货币表示
        return  moneyStyle ;
    }

    /**
     * 获取货币样式 保留2位小数
     */
    public static CellStyle createMoneyStyleRound2(Workbook workbook, Font font){
        //金钱数字样式
        CellStyle moneyStyle = createCellStyle(workbook,font);
        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));//保留四位小数 货币表示
        return  moneyStyle ;
    }

    /**
     * 获取货币样式 保留2位小数
     */
    public static CellStyle createTimeStyle(Workbook workbook, Font font){
        CellStyle timeStyle = createCellStyle(workbook,font);
        timeStyle.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy h:mm"));
        return  timeStyle ;
    }

    /**
     * 根据字体名称创建字体
     * @param workbook
     */
    public static Font createFont(Workbook workbook, String fontName){
        // 标题字体 微软雅黑 黑色 文字高度12
        Font titleFont =  workbook.createFont();
        titleFont.setFontName(fontName);
        titleFont.setColor(HSSFColor.BLACK.index);
        titleFont.setFontHeightInPoints(POIConsts.FONT_HEIGHT_IN_POINTS);
        return titleFont;
    }

    /**
     * 创建行
     */
    public static Row createRow(Sheet sheet, int rowNum, float heightInPoints){
        Row row = sheet.createRow(rowNum);
        // 设置行高
        row.setHeightInPoints(heightInPoints);
        return row ;
    }

    /**
     * 创建行
     */
    public static Row createTitleRow(Sheet sheet, int rowNum){
        return createRow(sheet,rowNum,POIConsts.HEAD_HEIGHT_IN_POINTS);
    }

    /**
     * 创建行
     */
    public static Row createDataRow(Sheet sheet, int rowNum){
        return createRow(sheet,rowNum,POIConsts.BODY_HEIGHT_IN_POINTS);
    }

    /**
     * 创建单元格
     */
    public static void createCell(Row row, int colNum, String value, CellStyle style){
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * 创建单元格
     */
    public static void createCell(Row row, int colNum, Date value, CellStyle style){
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * 创建单元格
     */
    public static void createCell(Row row, int colNum, double value, CellStyle style){
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * 合并 第firstRow - lastRow行 firstCol-lastCol列
     * @param sheet 表格
     * @param firstRow 起始行
     * @param lastRow 结束行
     * @param firstCol 起始列
     * @param lastCol 结束列
     */
    public static void merge(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol){
        CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol,lastCol);
        sheet.addMergedRegion(cra);
    }

    /**
     * 合并某一行的 firstCol-lastCol列
     */
    public static void mergeColumn(Sheet sheet, int rowNum, int firstCol, int lastCol){
        merge(sheet,rowNum, rowNum, firstCol,lastCol);
    }

    /**
     * 合并 firstRow-lastRow行 的 colNum列
     */
    public static void mergeRow(Sheet sheet, int firstRow, int lastRow, int colNum){
        merge(sheet,firstRow, lastRow, colNum,colNum);
    }


    /**
     *
     * @param sheetTitle sheet名称
     * @param headers    列标题
     * @param showNames  需要显示的属性名
     * @param clazz      实体class
     * @param data       数据集
     * @param out        输出流
     * @param isXls  是否为xls false为xlsx
     * @throws Exception
     */
    public static void export(String sheetTitle, String[] headers, String[] showNames, Class clazz, Collection data , OutputStream out,boolean isXls) throws Exception{
        Workbook workbook  = createWorkbook(isXls);

        // 生成一个表格
        Sheet sheet = workbook.createSheet(sheetTitle);

        //标题样式
        CellStyle titleStyle  = createCellStyle(workbook,createFont(workbook,POIConsts.MICROSOFT_YAHEI_FONT));

        //宋体
        Font font = createFont(workbook,POIConsts.SIMSUN_FONT);

        // 正文样式(字符串)
        CellStyle bodyStyle = createCellStyle(workbook,font);

        //金钱数字样式
        CellStyle moneyStyle = createMoneyStyleRound2(workbook,font);

        //时间样式
        CellStyle timeStyle = createTimeStyle(workbook,font);

        int index = 0;
        Row row;
//        Cell cell; //excel2003的.xls对应是HSSFCell，而之后的xlsx对应的则是XSSFCell
        if (headers.length > 0) {
            // 产生表格标题行
            row = createTitleRow(sheet,index++);
            for (int i = 0; i < headers.length; i++) {
                createCell(row,i,headers[i],titleStyle);
                // 设置列宽
                sheet.setColumnWidth(i, POIConsts.COLUMN_WIDTH);
            }
        }
        // 遍历集合数据，产生数据行
        Iterator it = data.iterator();
        Field field;
        Object value ;
        String textValue ;
        Date date ;
        //clazz 所有属性
        List<String> fieldNameList = new ArrayList<>() ;
        for(Field f : clazz.getDeclaredFields()){
            fieldNameList.add(f.getName());
        }
        while (it.hasNext()) {
            Object t = it.next();
            row = createDataRow(sheet,index);
            for (int i = 0; i < showNames.length; i++) {
                //属性有可能是从父类继承而来
                if(fieldNameList.contains(showNames[i])){
                    field =clazz.getDeclaredField(showNames[i]);
                }else {
                    field = clazz.getSuperclass().getDeclaredField(showNames[i]);
                }
                field.setAccessible(true);
                // 利用反射，得到属性值
                value = field.get(t);
                if (value instanceof Date) {
                    date = (Date) value;
                    createCell(row,i,date,timeStyle);
                }else if(value instanceof Number) {
                    createCell(row,i,((Number) value).doubleValue(),moneyStyle);
                }else{
                    // 其它数据类型都当作字符串简单处理
                    if(value == null){
                        value ="" ;
                    }
                    textValue = value.toString();
                    createCell(row,i,textValue,bodyStyle);
                }
            }
//            row.setRowStyle(bodyStyle);//设置一行样式
            index++;
        }
        workbook.write(out);
        workbook.close();
        out.flush();
        out.close();
    }

    /**
     * sql 结果集导出
     * @param mapList 结果
     * @param out  输出流
     * @param isXls 是否导出xls
     * @throws Exception
     */
    public static void export(List<Map<String,Object>> mapList ,OutputStream out,boolean isXls) throws Exception{
        Workbook workbook  = createWorkbook(isXls);

        // 生成一个表格
        Sheet sheet = workbook.createSheet();

        //标题样式
        CellStyle titleStyle  = createCellStyle(workbook,createFont(workbook,POIConsts.MICROSOFT_YAHEI_FONT));

        //宋体
        Font font = createFont(workbook,POIConsts.SIMSUN_FONT);

        // 正文样式(字符串)
        CellStyle bodyStyle = createCellStyle(workbook,font);

        //金钱数字样式
        CellStyle moneyStyle = createMoneyStyleRound2(workbook,font);

        //时间样式
        CellStyle timeStyle = createTimeStyle(workbook,font);

        //直接从数据行开始 ,留出标题行
        int index = 1;
        Row row;
        Object value;
        int rowCellBegin;
        List<String> heads = new ArrayList<>();
        for(Map<String,Object> map : mapList){
            row = createTitleRow(sheet,index ++);
            rowCellBegin = 0 ; //单条数据创建cell下标起始
            for(Map.Entry<String,Object> entry : map.entrySet()){
                if(heads.size() < map.entrySet().size()){
                    heads.add(entry.getKey());
                }
                value = entry.getValue() ;
                if (value instanceof Date) {
                    createCell(row,rowCellBegin++,(Date) value,timeStyle);
                }else if(value instanceof Number) {
                    createCell(row,rowCellBegin++,((Number) value).doubleValue(),moneyStyle);
                }else{
                    // 其它数据类型都当作字符串简单处理
                    if(value == null){
                        value ="" ;
                    }
                    createCell(row,rowCellBegin++,value.toString(),bodyStyle);
                }
            }
        }
        //创建标题列
        row = createTitleRow(sheet,0);
        rowCellBegin = 0 ;
        for(String title : heads){
            createCell(row,rowCellBegin++,title,titleStyle);
        }
        workbook.write(out);
        workbook.close();
        out.flush();
        out.close();
    }

    //非空提示
    private static String notNull(String cellValue  , String head){
        if (StringUtils.isEmpty(cellValue)) {
            return  head + "为空 |";
        }else {
            return  "";
        }
    }

    //非数字提示
    private static String notNumber(String cellValue  , String head){
        if (StringUtils.isEmpty(cellValue)) {
            return  "";
        }else {
           return NumberUtils.isBigDecimal(cellValue) ? "" :  head + "不是数字 |";
        }
    }

    /**
     * 非日期提示
     * @param cellValue cell中的值
     * @param head 所属字段
     * @param type 日期转化格式
     */
    private static String notDate(String cellValue , String head ,String type){
        if (StringUtils.isEmpty(cellValue)) {
            return  "";
        }else {
            return parseStr2DateByType(type,cellValue) !=null ? "" :  head + "不符合["+ DATE_FORMAT_TYPE_STRING_MAP.get(type)+"]格式 |";
        }
    }

    private static String whichRow(int i){
        return "第["+ (i+1)+"]行 ";
    }

    /**
     * 根据格式化类型 格式化字符串日期
     * @param type 格式化类型
     * @param dateStr 日期字符串
     */
    private static Date parseStr2DateByType(String type,String dateStr){
        Date date;
        switch (type){
            case "1":
                date = DateUtils.parse2Date(dateStr);
                break;
            case "2":
                date = DateUtils.parse2Date2(dateStr);
                break;
            case "3":
                date = DateUtils.parse2LongDate(dateStr);
                break;
            case "4":
                date = DateUtils.parse2LongDate2(dateStr);
                break;
            default:
                date = DateUtils.parse2Date(dateStr);
                break;
        }
        return date;
    }

    /**
     * 通用导入 导入模版规则 日期使用文本
     * 第一行为标题行 : 名称(验证标识)  第二行为class 对应名称在java类中属性名
     * 验证标识 :
     *  NotNull:不能为空
     *  Number:为数字格式
     *  Date:为日期格式
     *      Date-1 yyyy-MM-dd (默认,只填写Date则为此)
     *      Date-2 yyyy/MM/dd
     *      Date-3 yyyy-MM-dd HH:mm:ss
     *      Date-4 yyyy/MM/dd HH:mm:ss
     * @param in 输入流
     * @param clazz 导入类class
     * @param isXls 是否为xls
     * @return BackResult code 为1 表示成功返回 为 0 验证未通过 message为错误信息
     * @throws Exception
     */
    public static BackResult importExcel(InputStream in  , Class<?> clazz, boolean isXls) throws Exception{
        Workbook workbook ;
        if(isXls){
            workbook = new HSSFWorkbook(in);
        }else {
            workbook = new XSSFWorkbook(in);
        }
        //读取第一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        //存在导入数据行导入才有意义
        if(sheet.getLastRowNum() <= 1){
            return BackResult.failureBack("不存在导入数据!");
        }
        Row row ;
        Cell cell ;
        //读取验证规则
        row = sheet.getRow(0);
        List<String> nameList = new ArrayList<>();
        //非空字段cell下标 集合
        List<Integer> notNullList = new ArrayList<>();
        //日期字段cell下标集合
        List<Integer> dateList = new ArrayList<>();
        //数字字段cell下标集合
        List<Integer> numberList = new ArrayList<>();
        //日期字段cell下标 : 日期格式化字类型 map
        Map<Integer,String> dateFormatStringMap = new HashMap<>();
        for(Cell ce:row){
            if(ce.getStringCellValue().toLowerCase().contains(NOT_NULL)){
                notNullList.add(ce.getColumnIndex());
            }else if(ce.getStringCellValue().toLowerCase().contains(DATE)){
                dateFormatStringMap.put(ce.getColumnIndex(),getDateFormatType(ce.getStringCellValue()));
                dateList.add(ce.getColumnIndex());
            }else if(ce.getStringCellValue().toLowerCase().contains(NUMBER)){
                numberList.add(ce.getColumnIndex());
            }
            nameList.add(ce.getStringCellValue());
        }
        logger.info("标题总列数列数:{} 需验证: NotNull:{} |Date:{} |Number:{} ",
                nameList.size(),notNullList.size(),dateList.size(),numberList.size());
        //读取类属性
        row = sheet.getRow(1);
        List<String> fieldList = new ArrayList<>();
        for(Cell ce:row){
            fieldList.add(ce.getStringCellValue());
        }

        //clazz 所有属性
        List<String> fieldNameList = new ArrayList<>() ;
        for(Field f : clazz.getDeclaredFields()){
            fieldNameList.add(f.getName());
        }

        List<? super Object> objects = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        //前2行为标题行 数据从第三行开始
        // 无值的cell获取为null ,一行cell 个数按最后存在值的cell算
        for(int x = 2 ; x <= sheet.getLastRowNum() ; x ++){
            row = sheet.getRow(x);

            //验证非空
            for(Integer integer: notNullList){
                cell = row.getCell(integer) ;
                sb.append(notNull(getCellStringValue(cell),nameList.get(integer)));
            }
            if(sb.length() > 0){
                sb.insert(0,whichRow(x));
                break;
            }

            //验证数字
            for(Integer integer: numberList){
                cell = row.getCell(integer) ;
                sb.append(notNumber(getCellStringValue(cell),nameList.get(integer)));
            }
            if(sb.length() > 0){
                sb.insert(0,whichRow(x));
                break;
            }

            //验证日期
            for(Integer integer: dateList){
                cell = row.getCell(integer) ;
                sb.append(notDate(getCellStringValue(cell),nameList.get(integer),dateFormatStringMap.get(integer)));
            }
            if(sb.length() > 0){
                sb.insert(0,whichRow(x));
                break;
            }

            Field field ;
            Object obj =  clazz.getConstructor().newInstance();
            //验证通过后 开始初始化对象
            for(int i = 0; i < nameList.size() ; i++){
                cell = row.getCell(i);
                //属性有可能是从父类继承而来
                if(fieldNameList.contains(fieldList.get(i))){
                    field =clazz.getDeclaredField(fieldList.get(i));
                }else {
                    field = clazz.getSuperclass().getDeclaredField(fieldList.get(i));
                }
                setValueByFieldType(obj,field,cell,dateFormatStringMap);
            }
            objects.add(obj);
        }
        if(sb.length() > 0){
            return  BackResult.failureBack(sb.toString());
        }else {
            return  BackResult.successBack(objects);
        }
    }

    //获取单个Cell的值,全部转为String
    private static String getCellStringValue(Cell cell){
        if(cell == null){
            return  null ;
        }else {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                return Double.toString(cell.getNumericCellValue());
            }else {
                return  cell.getStringCellValue();
            }
        }
    }

    /**
     * 根据属性类型设置值 常用类型
     * @param obj 需要设置值的对象
     * @param field 属性
     * @param cell 单元格
     * @param dateFormatStringMap 日期转化格式map
     * @throws IllegalAccessException
     */
    private static void setValueByFieldType(Object obj, Field field, Cell cell, Map<Integer,String> dateFormatStringMap) throws IllegalAccessException {
        field.setAccessible(true);
        Class fieldClass = field.getType();
        if(fieldClass == String.class){
            field.set(obj,getCellStringValue(cell));
        }else if(fieldClass == BigDecimal.class){
            field.set(obj,new BigDecimal(getCellStringValue(cell)));
        }else if(fieldClass == Date.class ){
            field.set(obj,parseStr2DateByType(dateFormatStringMap.get(cell.getColumnIndex()),getCellStringValue(cell)));
        }else if (fieldClass == Double.class){
            field.set(obj,Double.parseDouble(getCellStringValue(cell)));
        }else if (fieldClass == Integer.class){
            field.set(obj,Integer.parseInt(getCellStringValue(cell)));
        }else if (fieldClass == Boolean.class){
            field.set(obj,Boolean.parseBoolean(getCellStringValue(cell)));
        }else {
            field.set(obj,getCellStringValue(cell));
        }
    }

    //获取日期格式化字符串
    private static  String getDateFormatType(String str){
        String content = StringUtils.getBracketContent(str);
        return content.lastIndexOf("-") != -1 ? content.substring(content.lastIndexOf("-") + 1 ) : DEFAULT_DATE_FORMAT_TYPE;
    }
}

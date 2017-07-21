package org.skyCloud.common.utils;

import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.common.entity.BankOfChinaExchangeRate;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yq on 2016/09/23 11:54.
 * 抓取中行汇率工具类
 */
public class ExchangeRateUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateUtils.class) ;

    public static final String BANK_OF_CHINA_URL = "http://srh.bankofchina.com/search/whpj/search.jsp";

    //数据数量正则(某字符到其后面第一个;的内容)
    public static final String RECORD_COUNT_REGEX = "var m_nRecordCount = ([^;]*)";

    //获取页面页数
    public static final String PAGE_SIZE_REGEX = "var m_nPageSize = ([^;]*)";

    public static Pattern RECORD_COUNT_PATTERN = Pattern.compile(RECORD_COUNT_REGEX);

    public static Pattern PAGE_SIZE_PATTERN = Pattern.compile(PAGE_SIZE_REGEX);

    private ExchangeRateUtils() {
    }

    /**
     * 获取单个货币所有汇率
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pjName 货币在中行的编码
     * @throws Exception
     */
    public static BackResult getCurrencyAllPageRate(String startTime, String endTime, String pjName){
        String url = BANK_OF_CHINA_URL + "?erectDate="+startTime+"&nothing="+endTime+"&pjname="+pjName ;
        try{
            //获取页面doc
            Document doc = Jsoup.connect(url).timeout(3000).userAgent("Mozilla").get();
            //totalCount 数据总数   totalPage 总页数
            Map<String,Integer> map = getTotalPage(doc.html());
            if(map.get("totalCount") == 0){
                logger.info("没有检索结果!");
                return  BackResult.failureBack("没有检索结果!");
            }else {
                Integer totalPage = map.get("totalPage");
                logger.info("{} 获取 {} 页数据",pjName,totalPage);
                List<BankOfChinaExchangeRate> list = new CopyOnWriteArrayList<>() ;
                if(totalPage > 0 ){
                    //首次查询获取首页数据 || 只有一页数据
                    list.addAll(getOnePageRateData(doc));
                    if(totalPage >= 2){
                        //创建一个可缓存的线程池，调用execute 将重用以前构造的线程（如果线程可用）。
                        // 如果现有线程没有可用的，则创建一个新线程并添加到池中。
                        // 终止并从缓存中移除那些已有 60 秒钟未被使用的线程。
                        //ExecutorService扩展了Executor并添加了一些生命周期管理的方法 : 运行 ，关闭 ，终止
                        ExecutorService exec = Executors.newCachedThreadPool();
                        //任务完成才执行下一个,内部存在阻塞队列BlockingQueue ,没有任务完成，take()方法也会阻塞
                        CompletionService<List<BankOfChinaExchangeRate>> execcomp = new ExecutorCompletionService<>(exec);
                        //从第二页开始，因为第一页已经在首次查询时获取
                        for (int k=2;k <= totalPage;k++){
                            //提交执行
                            execcomp.submit(getTask(url + "&page=" + k));
                        }
                        Future<List<BankOfChinaExchangeRate>> future ;
                        List<BankOfChinaExchangeRate> rate ;
                        for(int k=2; k<= totalPage; k++){
                            //检索并移除表示下一个已完成任务的 Future，如果目前不存在这样的任务，则等待。
                            future = execcomp.take();//一个Future代表一个异步执行的操作
                            try {
                                rate = future.get(5, TimeUnit.SECONDS);  //超时时间获取
                                if(rate != null && rate.size() > 0){
                                    list.addAll(rate);
                                }
                            } catch (TimeoutException e) {
                                logger.error("从 {} 取中行外汇数据超时 {}",url,e);
                            }
                        }
                        exec.shutdown(); //关闭执行器
                        //所有任务执行完毕并且已调用shutdown，或者超时则终止执行器
                        exec.awaitTermination(10, TimeUnit.SECONDS);
                        logger.info("cachedThreadPool size : {}",((ThreadPoolExecutor)exec).getLargestPoolSize());
                    }
                }
                return  BackResult.successBack(list) ;
            }
        }catch (Exception e){
            logger.error("获取汇率异常 : {}",e.getMessage());
            return  BackResult.failureBack("获取汇率异常!");
        }
    }

    /**
     * 获取一页数据
     * @param document
     * @return
     * @throws Exception
     */
    public static List<BankOfChinaExchangeRate> getOnePageRateData(Document document) throws Exception{
        List<BankOfChinaExchangeRate> list = new CopyOnWriteArrayList<>() ;
        //获取汇率class : BOC_main 的div 下的table 下的所有tr
        Elements trs = document.select("div.BOC_main").get(0).select("table").select("tr") ;
        BankOfChinaExchangeRate rate ;
        for (Element tr : trs){
            if(tr.children().size() == 8 && !(tr.children().first().tagName().equals("th"))){ //有数据行 且不为table头
                rate = new BankOfChinaExchangeRate() ;
                rate.setCurrency(tr.child(0).html());
                rate.setBuyingRate(NumberUtils.String2BigDecimal(tr.child(1).html()));//现汇买入价
                rate.setCashBuyingRate(NumberUtils.String2BigDecimal(tr.child(2).html()));//现钞买入价
                rate.setSellingRate(NumberUtils.String2BigDecimal(tr.child(3).html()));//现汇卖出价
                rate.setCashSellingRate(NumberUtils.String2BigDecimal(tr.child(4).html()));//现钞卖出价
                rate.setSafeMiddleRate(NumberUtils.String2BigDecimal(tr.child(5).html()));//外管局中间价
                rate.setBankConvertRate(NumberUtils.String2BigDecimal(tr.child(6).html()));//中行折算价
                rate.setPulishTime(DateUtils.parseRateDate(tr.child(7).html()));//发布时间
                list.add(rate);
            }
        }
        return  list ;
    }

    /**
     * 一个请求地址一个任务
     */
    public static Callable<List<BankOfChinaExchangeRate>> getTask(String url){
        Callable<List<BankOfChinaExchangeRate>> task = new Callable<List<BankOfChinaExchangeRate>>(){
            public List<BankOfChinaExchangeRate> call() throws Exception {
                Connection connection = Jsoup.connect(url).timeout(3000).userAgent("Mozilla");
                Document doc = connection.get();
                return getOnePageRateData(doc);
            }
        };
        return task;
    }

    /**
     * 取查询出币制汇率分页总页数
     */
    public static Map<String,Integer> getTotalPage(String sb){
        Map<String,Integer> map = new HashMap<>() ;
        Integer totalPage = 0;
        Integer totalNum = 0;
        Integer pageSizeNum = 20;  //默认为20
        try{
//            Pattern pattern = Pattern.compile(RECORD_COUNT_REGEX);
            Matcher matcher = RECORD_COUNT_PATTERN.matcher(sb);
            if (matcher.find()) {
                totalNum = Integer.parseInt(matcher.group(1)) ;
            }

//            pattern = Pattern.compile(PAGE_SIZE_REGEX);
            matcher = PAGE_SIZE_PATTERN.matcher(sb);
            if (matcher.find()) {
                pageSizeNum = Integer.parseInt(matcher.group(1)) ;
            }

            if(totalNum % pageSizeNum == 0){
                totalPage = totalNum / pageSizeNum;
            }else{
                totalPage = totalNum / pageSizeNum + 1;
            }
        }catch(Exception e){
            logger.error("从页面取总记录数和分页显示条数异常",e) ;
        }
        map.put("totalCount" , totalNum ) ;
        map.put("totalPage" , totalPage ) ;
        if(logger.isDebugEnabled()){
            logger.debug("数据总数:{} ; 总页数:{}",totalNum,totalPage);
        }
        return map;
    }

    /**
     * 获取中行货币对应编码及名称
     * @throws Exception
     * @return Map <货币名称,编码>
     */
    public static Map<String,String> getAllCurrencyCodeAndName() throws Exception{
        logger.info("开始获取中行货币名称及编码....");
        Document doc = Jsoup.connect(BANK_OF_CHINA_URL).get();
        Element pjname = doc.getElementById("pjname") ; //获取中行货币select
        Elements options =  pjname.children() ;//所有货币options
        ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();
        for(Element option : options){
            if(!option.html().equals("选择货币")){
                map.put(option.html(),option.val()) ;
            }
        }
        logger.info("获取完成,共{}种",map.size());
        return  map ;
    }
}

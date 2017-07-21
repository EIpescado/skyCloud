package org.skyCloud.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skyCloud.common.constants.ExceptionConsts;
import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.common.exception.SkyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yq on 2017/03/28 16:28.
 * 查询快递信息工具
 */
public class QueryExpressInformationUtils {

    private static final Logger logger = LoggerFactory.getLogger(QueryExpressInformationUtils.class);

    //根据单号查询快递类型地址
    private static final String QUERY_TYPE_URL = "http://www.kuaidi100.com/autonumber/autoComNum";

    //根据单号查询快递详细信息地址
    private static final String QUERY_DETAILS_URL = "http://www.kuaidi100.com/query";

    //快递状态码对应状态名称 map
    private static final Map<String,String> EXPRESS_STATE_MAP =  new HashMap<String,String>(){
        {
            put("0","在途");//即货物处于运输过程中
            put("1","揽件");//货物已由快递公司揽收并且产生了第一条跟踪信息
            put("2","疑难");//货物寄送过程出了问题
            put("3","签收");//收件人已签收
            put("4","退签");//即货物由于用户拒签、超区等原因退回，而且发件人已经签收
            put("5","派件");//即快递正在进行同城派件
            put("6","退回");//货物正处于退回发件人的途中
        }
    };


    private QueryExpressInformationUtils() {
    }

    /**
     * 根据单号查询快递类型
     * @param expressNo
     */
    public static String obtainExpressType(String expressNo) throws Exception{
        if(StringUtils.isNotEmpty(expressNo)){
            Map<String,Object> map = new HashMap<>();
            map.put("text",expressNo);
//            String returnStr = HttpUtils.sendPost(QUERY_TYPE_URL,map,false);
            String returnStr = HttpClientUtils.sendPost(QUERY_TYPE_URL,map);
            ObjectMapper mapper = new ObjectMapper();
            Map dataMap =  mapper.readValue(returnStr,Map.class);
            List<Map> list = (List<Map>) dataMap.get("auto");
            if(list != null && list.size() > 0){
                String type = (String) list.get(0).get("comCode");
                logger.info("快递号 {} 快递类型: {}",expressNo,type);
                return type;
            }else {
                logger.info("未获取到快递号 {} 快递类型",expressNo);
                return "";
            }
        }else {
            throw new SkyException(ExceptionConsts.ARGS_REQUIRED_CODE,ExceptionConsts.ARGS_REQUIRED_MESSAGE);
        }
    }

    /**
     * 根据单号查询快递详细
     */
    public static BackResult obtainExpressDetails(String expressNo) throws Exception{
        //参数
        Map<String,Object> map = new HashMap<>();
        String type = obtainExpressType(expressNo);//获取快递类型
        map.put("type",type);
        map.put("postid",expressNo);

        //发送请求
        String returnStr = HttpUtils.sendGet(QUERY_DETAILS_URL,map,false);

        //解析json
        ObjectMapper mapper = new ObjectMapper();
        Map dataMap =  mapper.readValue(returnStr,Map.class);
        String status = (String) dataMap.get("status");//查询状态
        logger.info("查询状态 :{}",status);
        if(status.equals("200")){//查询成功
            Map<String,Object> returnObj = new HashMap<>();

            String state = (String) dataMap.get("state");//快递单当前的状态
            returnObj.put("state",EXPRESS_STATE_MAP.get(state));
            logger.info("快递单当前的状态 :{}",EXPRESS_STATE_MAP.get(state));

            List<Map> data = (List<Map>) dataMap.get("data"); //地点和跟踪进度
            List<Map> returnData = new ArrayList<>();
            if(data != null && data.size() > 0){
                Map<String,String> rowMap ;
                for(Map row :data ){
                    rowMap = new HashMap<>();
                    rowMap.put("time",DateUtils.parse2StringAccurate2Min(DateUtils.parse2DateAccurate2Min((String) row.get("time"))));
                    rowMap.put("context",(String) row.get("context"));
                    returnData.add(rowMap);
                }
            }
            returnObj.put("data",returnData);
            return   BackResult.successBack(returnObj);
        }else {
            String message = (String) dataMap.get("message");//错误信息
            logger.info("错误信息 :{}",message);
            return BackResult.failureBack(message);
        }
    }

    public static void main(String[] args) throws Exception {
//        obtainExpressType("53016759302");
        obtainExpressDetails("885049988808007214");
//        obtainExpressDetails("53091697656");
//        obtainExpressDetails("53091966938");
    }
}

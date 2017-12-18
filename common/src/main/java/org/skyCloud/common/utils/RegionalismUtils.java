package org.skyCloud.common.utils;

import org.skyCloud.common.constants.ExceptionConsts;
import org.skyCloud.common.exception.SkyException;
import org.skyCloud.common.entity.AdministrativeRegion;
import org.skyCloud.common.entity.RegionLevelEum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by yq on 2017/03/23 16:37.
 *行政区域划分代码工具类
 */
public class RegionalismUtils {

    private static final Logger logger = LoggerFactory.getLogger(RegionalismUtils.class);

    //行政区域划分历史记录地址
    private static final String REGIONALISM_URL = "http://www.stats.gov.cn/tjsj/tjbz/xzqhdm";

    //重庆 海南页面标签与其他省不同,特殊处理
    private static final String[] SPECIAL_PROVINCE_CODE = new String[]{"500000","460000"};

    //市级行政区 span nbsp的个数
    private static final int CITY_NBSP_NUMBER = 8 ;

    //县级行政区 span nbsp的个数
    private static final int AREA_NBSP_NUMBER = 4 ;

    private RegionalismUtils() {
    }

    /**
     * 获取最新的行政区域划分地址
     */
    public static String getLatestRegionalismUrl() throws Exception{
        Document doc = Jsoup.connect(REGIONALISM_URL).get();
        Element center_lis = doc.getElementsByClass("center_list").get(0) ; //获取行政区域划分div center_list 类所对应div 只有一个
        Element a = center_lis.getElementsByTag("a").first();//获取div下第一个a标签
        String href = a.attr("href");
        StringBuffer sb  = new StringBuffer(href);
        sb.replace(0,1,"");
        sb.insert(0,REGIONALISM_URL);
        String url = sb.toString();
        logger.debug("最新的行政区域划分地址: {}",url);
        return url;
    }

    /**
     * 获取行政区域名称及代码 重庆 海南特殊
     */
    public static List<AdministrativeRegion> getAdministrativeRegion(){
        //最新的行政区域划分地址
        String latestRegionalismUrl = null;
        Document doc = null;
        try {
            latestRegionalismUrl = getLatestRegionalismUrl();
            doc = Jsoup.connect(latestRegionalismUrl).timeout(30000).userAgent("Mozilla").get();
        } catch (Exception e) {
            throw new SkyException(ExceptionConsts.SERVER_REQUEST_FAILURE_CODE,ExceptionConsts.SERVER_REQUEST_FAILURE_MESSAGE);
        }
        //数据所在div
        Element dataDiv = doc.getElementsByClass("TRS_PreAppend").get(0) ; //获取行TRS_PreAppend类所对应div 只有1个
        //p标签元素list
        Elements pList = dataDiv.getElementsByClass("MsoNormal");//获取数据div 中所有 类为 MsoNormal的元素
        Element pFirstChild ;//p标签下第一个子元素
        Element p2B1Span ;//p下第二个b的第一个span
        Element b1Span ;//b下第一个span
        String code ; //行政区域编码
        String name ;//行政区域名称
        Integer nbspNum ;//nbsp个数
        Element p2Span ; //p下第二个span
        int provinceNum = 0 ; //省合计数
        int cityNum = 0;//市合计
        int areaNum = 0 ;//县合计
        List<AdministrativeRegion> list = new CopyOnWriteArrayList<>();
        AdministrativeRegion region ;
        String provinceFlag = "" ;//标识现在是哪个省
        String cityFlag = "" ;//标识现在是哪个市
        for (Element element : pList){
            region = new AdministrativeRegion();
            pFirstChild = element.child(0);
            //若p标签下只有2个子元素,则为省
            if(element.childNodeSize() == 2){
                //重庆 海南特殊处理
                if(element.html().contains(SPECIAL_PROVINCE_CODE[0]) ||
                        element.html().contains(SPECIAL_PROVINCE_CODE[1]) ){
                    code = pFirstChild.ownText();
                    name = element.child(1).ownText();
                }else {
                    p2B1Span = element.child(1).child(0);//p下第二个b的第一个span
                    b1Span =  pFirstChild.child(0);//b下第一个span
                    code = b1Span.ownText();
                    name = p2B1Span.ownText();
                }
                region.setRegionLevel(RegionLevelEum.PROVINCE);
                provinceNum++;
                provinceFlag = code ; //标识现在是那个省
            }else{
                p2Span = element.child(1); //编码所在span
                nbspNum = p2Span.child(0).html().split(";").length;
                code = p2Span.ownText();
                name =element.child(2).ownText();
                if (nbspNum == AREA_NBSP_NUMBER){//县级区域
                    region.setRegionLevel(RegionLevelEum.AREA);
                    region.setCity(cityFlag);
                    region.setProvince(provinceFlag);
                    areaNum ++ ;
                }else if(nbspNum == CITY_NBSP_NUMBER){//市级区域
                    region.setRegionLevel(RegionLevelEum.CITY);
                    region.setProvince(provinceFlag); //市的上级省code
                    cityNum ++ ;
                    cityFlag = code ;//标识现在是那个市
                }else{
                    logger.info("异常 - {}:{}",name,code);
                }
            }
            region.setName(StringUtils.getAllChineseInStr(name));
            region.setCode(code.trim());
            list.add(region);
        }
        logger.info("省,直辖市,自治区: {} ;市: {};县: {}",provinceNum,cityNum,areaNum);
        return list ;
    }

    public static void main(String[] args) throws Exception {
        getAdministrativeRegion();
    }

}

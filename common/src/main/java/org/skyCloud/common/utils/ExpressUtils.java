package org.skyCloud.common.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by yq on 2016/08/24 9:17.
 * 境外快递
 */
public class ExpressUtils {

    //DHL地址
    public static final String DHL_URL = "http://hkapps.dhl.com.hk/big5_fi/big5_fi_Result.jsp?awb=";

    //TNT地址
    public static final String TNT_URL = "http://www.tnt-extranet.com.hk/tntflightinfo/FlightInfo.asp?cmdSearch=Submit&txtConnr=";

    //联邦地址
    public static final String FED_URL = "http://wap-asia.fedex.com/hktrade/zh/DeclarationDetails.htm?action=show&tracknumbers=";

    static final SimpleDateFormat EN_SDF = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

    static final SimpleDateFormat CN_SDF = new SimpleDateFormat("yyyy-MM-dd");

    static final SimpleDateFormat TNT_SDF = new SimpleDateFormat("dd/MM/yyyy");

    static final SimpleDateFormat DHL_SDF = new SimpleDateFormat("dd-MM-yyyy");

    static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy");

    static final String NO_AVAILABLE_MSG  = "No information available";

    static final String DHL_REFERER = "http://hkapps.dhl.com.hk/big5_fi/big5_fi_Home.html";

    /**
     * DHL快递
     * @param docNo 快递号
     * @return Map<String,String>  若error !="" 则该快递号不存在可用信息
     * @throws Exception
     */
    public static Map dHL(String docNo) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        //edit by yq 2017年11月6日 15:24:39  请求头需添加Referer:
        Document doc = Jsoup.connect(DHL_URL + docNo).header("Referer",DHL_REFERER).timeout(5000).get();
        Elements trs = doc.select("table").select("tr");
        if(trs.size() <= 1){
            map.put("error", NO_AVAILABLE_MSG);
            return map;
        }
        Elements element = doc.select("table").select("tr").get(1).select("td");//第二个tr的所有td
        if (element.size() < 6) {
            map.put("error", NO_AVAILABLE_MSG);
        } else {
            //裝貨港﹐ 入口國家 栏
            String s = msg(element, 4);
            String[] strings = s.split(",");
            map.put("transport", "4");        //运输方式
            map.put("depArrDT", DHL_SDF.format(DHL_SDF.parse(msg(element, 2)))); //到港/离港时间
            //主题单号
            String ztdh = getMsg(element, 3);
            StringBuilder sb=new StringBuilder(ztdh);
            //空运在第三个数字后添加 -
            sb.insert(3,"-");
            ztdh = sb.toString();
            map.put("masterNo", ztdh);//主题单号/无缝号
            map.put("houseNo", msg(element, 0)); //分提单号
            String zhg = strings[0] ;
            if(zhg.contains("-")){
                zhg = zhg.substring(0, zhg.lastIndexOf("-"));
            }
            map.put("loadingPort", zhg);   //装货港
            map.put("dischargePort",zhg); //卸货港
            map.put("convNo", msg(element, 1));  //飞机班次/航次
            map.put("country", strings[strings.length -1]);//输出国家/目的地国家
            map.put("origin", strings[1]);//原产国
            map.put("error", "");
        }
        return map;
    }

    /**
     * TNT快递
     * @param docNo 快递号
     * @return Map<String,String> 若error !="" 则该快递号不存在可用信息
     * @throws Exception
     */
    public static Map tNT(String docNo) throws Exception {
        Document doc = Jsoup.connect(TNT_URL + docNo).timeout(5000).get();
        //第3个table下第3个tr下的所有td
        Elements element = doc.select("table").get(0).select("table").get(4).select("tr");
        Map<String, String> map = new HashMap<>();
        if(element != null && element.size() == 2 ){
            element = element.get(1).select("td");
            if (element.size() < 6) {
                map.put("error", NO_AVAILABLE_MSG);
            } else {
                map.put("transport", "4");        //运输方式
                map.put("houseNo", getMsg(element, 0)); //分提单号
                map.put("convNo", getMsg(element, 1));  //飞机班次/航次
                map.put("depArrDT", DHL_SDF.format(TNT_SDF.parse(getMsg(element, 2)))); //到港/离港时间
                map.put("masterNo", getMsg(element, 3));//主题单号/无缝号
                map.put("loadingPort", getMsg(element, 4));   //装货港
                map.put("dischargePort", getMsg(element, 5)); //卸货港
                map.put("country", "");//输出国家/目的地国家
                map.put("origin", "");//原产国
                map.put("error", "");
            }
        }else {
            map.put("error", NO_AVAILABLE_MSG);
        }
        return map;
    }

    /**
     * 联邦快递
     * @param docNo
     * @return Map<String,String> 若error !="" 则该快递号不存在可用信息
     * @throws Exception
     */
    public static Map fED(String docNo) throws Exception {
        Document doc = Jsoup.connect(FED_URL + docNo).get();
        Elements element = doc.select("table");//
        Map<String, String> map = new HashMap<>();
        //若存在4个tabel 表示查询到数据
        if (element.size() == 4) {
            String transport = "" ;
            element = element.get(3).select("tr").get(2).select("td"); //table下数据所在tr的所有td
            //主题单号
            String ztdh = getMsg(element, 3);
            StringBuilder sb=new StringBuilder(ztdh);
            if(getMsg(element, 6).equals("Air")){
                //空运在第三个数字后添加 -
                sb.insert(3,"-");
                ztdh = sb.toString();
                transport = "4";
            }else if(getMsg(element, 6).equals("Road")){
                transport = "3";
                ztdh = getMsg(element, 4);
            }
            map.put("transport", transport);        //运输方式
            map.put("depArrDT", DHL_SDF.format(EN_SDF.parse(element.get(2).text()))); //到港/离港时间
            map.put("masterNo",ztdh);//主题单号/无缝号
            map.put("houseNo", getMsg(element, 0)); //分提单号
            map.put("loadingPort", getMsg(element, 5));   //装货港
            map.put("dischargePort", ""); //卸货港
            String s = getMsg(element, 1);
            map.put("country", s.substring(s.lastIndexOf(">") + 1));//输出国家/目的地国家
            map.put("origin", s.substring(0, s.lastIndexOf("-")));//原产国
            map.put("convNo", getMsg(element, 7));  //飞机班次/航次
            map.put("error", "");
        } else {
            map.put("error",NO_AVAILABLE_MSG);
        }
        return map;
    }

    private static String msg(Elements element, int i) {
        return element.get(i).select("font").select("b").get(0).text().replace(" ", "");
    }

    /**
     * 获取某元素集合中 第i个元素的文本值,去空格
     * @param elements
     * @param i
     * @return
     */
    private static String getMsg(Elements elements, int i) {
        return elements.get(i).text().replace(" ", "");
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(dHL("5706744293"));
//        System.out.println(tNT("182320810"));
//        System.out.println(fED("417891803585"));
    }
}

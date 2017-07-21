package org.skyCloud.common.utils;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by yq on 2016/09/26 14:54.
 * JDom Xpath utils
 * 解析,构建xml
 */
public class JDomUtils {

    private static final Logger logger = LoggerFactory.getLogger(JDomUtils.class);

    private JDomUtils() {
    }

    /**
     * 获取第一个匹配项的值(已去除前后空格)
     * @param in 输入流
     * @param xPathExpression xpath 表达式
     * @return 结果
     */
    public static String obtainFirstMatchResult(InputStream in,String xPathExpression){
        //构建文档
        Document doc = buildDocument(in);
        //获取元素
        XPathExpression<Element> xpath =  XPathFactory.instance().compile(xPathExpression, Filters.element());
        Element element =xpath.evaluateFirst(doc) ;
        if(element == null){
            logger.error("[{}] no match result",xPathExpression);
            return  null ;
        }
        return element.getTextTrim();
    }


    /**
     * 获取第一个匹配项的值(已去除前后空格)
     * @param doc 文档对象
     * @param xPathExpression xpath 表达式
     * @return 结果
     */
    public static String obtainFirstMatchResult(Document doc,String xPathExpression){
        //获取元素
        XPathExpression<Element> xpath =  XPathFactory.instance().compile(xPathExpression, Filters.element());
        Element element =xpath.evaluateFirst(doc) ;
        if(element == null){
            logger.error("[{}] no match result",xPathExpression);
            return  null ;
        }
        return element.getTextTrim();
    }

    /**
     * 获取所有的匹配元素
     * @param doc 文档对象
     * @param xPathExpression xpath 表达式
     */
    public static List<Element> obtainAllMatchElement(Document doc,String xPathExpression){
        XPathExpression<Element> xpath = XPathFactory.instance().compile(xPathExpression, Filters.element());
        return xpath.evaluate(doc);
    }

    /**
     * 获取元素属性值
     * @param element 元素
     * @param attrName 属性名称
     */
    public static String obtainAttributeValue(Element element,String attrName){
        Attribute attribute =  element.getAttribute(attrName);
        if(attribute == null){
            logger.error("element [{}] attribute [{}] is not exist",element.getName(),attrName);
            return null ;
        }
        return attribute.getValue().trim();
    }

    /**
     * 创建Document
     */
    public static Document buildDocument(InputStream in){
        SAXBuilder sb = new SAXBuilder();
        Document document = null;
        try {
            document = sb.build(in) ;
        } catch (JDOMException | IOException e) {
            logger.error("document build error : {}",e.getMessage());
        }
        return  document;
    }

    //构建xml测试
    public static void test() throws Exception{
        Element root = new Element("root");
        Document doc = new Document(root);
        Element first =  new Element("first");
        first.setText("first");
        first.setAttribute("name","firstName");
        root.addContent(first);
        XMLOutputter XMLOut = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        logger.info(format.getEncoding());
        XMLOut.setFormat(format);
        OutputStream out =  new FileOutputStream("d:/A/xmlTest.xml");
        XMLOut.output(doc,out);
        out.close();
    }

    public static void main(String[] args) throws Exception {
        Document doc = buildDocument(new FileInputStream("d:/b.xml"));
        XPathExpression<Element> xpath2 =  XPathFactory.instance().compile("/HD/disk/files",Filters.element());
        System.out.println(obtainAttributeValue(xpath2.evaluateFirst(doc),"name"));
//        List<Object> objList = xpath2.diagnose(doc.getRootElement(), true).getRawResults();
//        Element emt;
//        for(Object obj :objList) {
//            emt = (Element) obj;
//            System.out.println(emt.getName());
//        }
    }

}

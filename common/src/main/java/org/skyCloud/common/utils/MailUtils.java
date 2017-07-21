package org.skyCloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by yq on 2017/04/20 16:55.
 * 发送邮件工具类
 * 电子邮件协议包括 SMTP，POP3，IMAP
 * SMTP 是 Simple Mail Transfer Protocol 的简称，即简单邮件传输协议
 * 邮件的创建和发送只需要用到 SMTP协议
 */
public class MailUtils {

    private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);


    //邮件服务提供地址
    public static final String host = "smtp.163.com";
    //发件人帐号
    public static final String sendUser = "yurwisher@163.com";
    //密码
    public static final String password = "******";

    //使用的协议（JavaMail规范要求）
    private static final String transfer_protocol = "smtp";

    //SMTP 服务器的端口 一般为25
    private static final String port = "25";

    private static Properties props = new Properties();

    static {
        //设置邮件属性
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.user", sendUser);
        props.put("mail.password", password);
        props.setProperty("mail.transport.protocol", transfer_protocol);
        props.setProperty("mail.smtp.port", port);
    }

    private MailUtils(){

    }

    /**
     * 发送给单个人
     * @param receiver 收件人邮箱地址 电子邮件的标准格式(RFC822)
     * @param title  邮件标题
     * @param content 邮件内容
     * @throws Exception
     */
    public static void sendMail(String receiver, String title, String content) throws Exception{
        //设置授权信息
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(props.getProperty("mail.user"), props.getProperty("mail.password"));
            }
        };
        //取得session
        Session mailSession = Session.getInstance(props, authenticator);
        // 设置为debug模式, 可以查看详细的发送 log
        mailSession.setDebug(true);

        //使用环境属性和授权信息，创建邮件会话
        MimeMessage message = new MimeMessage(mailSession);
        //设置发件人信息
        InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
        message.setFrom(form);

        //设置收件人
        InternetAddress to = new InternetAddress(receiver);
        //邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
//        InternetAddress to = new InternetAddress(receiver,"呢称","UTF-8");
        //TO 收件 CC 抄送 BCC 密送
        message.setRecipient(Message.RecipientType.TO, to);

        //设置邮件标题
        message.setSubject(title);
        //正文
        message.setContent(content, "text/html;charset=UTF-8");
        //设置显示发送的时间
        message.setSentDate(new Date());
        //发送邮件
        Transport.send(message);
    }

    /**
     * 发送邮件 带附件
     * @param receivers 收件人邮箱地址集合
     * @param title 标题
     * @param content 邮件内容
     * @param attachments 附件集合
     * @throws Exception
     */
    public static void sendMailWithAttachment(List<String> receivers,String title, String content,List<File> attachments) throws Exception{
        //取得session
        Session session = Session.getInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log
        session.setDebug(true) ;
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session) ;

        //邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        //发件人
        InternetAddress from = new InternetAddress(props.getProperty("mail.user"));
        message.setFrom(from);

        //设置收件人多个
        for (String receiver : receivers){
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(receiver));
        }

        //设置邮件主题
        message.setSubject(title) ;

        //邮件正文
        message.setContent(createContent(content,attachments));

        //保存设置
        message.saveChanges();

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器
        //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
        transport.connect(props.getProperty("mail.smtp.host"), props.getProperty("mail.user"),
                props.getProperty("mail.password")) ;

        // 6. 发送邮件, 发到所有的收件地址,
        // message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
    }

    /**
     * 构建正文主体
     * @param html 文本内容
     * @param attachments 附件
     * @throws Exception
     */
    private static Multipart createContent(String html , List<File> attachments) throws Exception{

        //正文主体
        Multipart main = new MimeMultipart();

        //创建文本部分
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(html, "text/html;charset=UTF-8");
        main.addBodyPart(text);

        //设置邮件附件，可以有多个
        if( null != attachments && attachments.size() > 0){
            MimeBodyPart attachment ;
            for(File file : attachments){
                attachment = new MimeBodyPart();
                attachment.setDataHandler(new DataHandler(new FileDataSource(file))); //得到数据源 得到附件本身并放入BodyPart
                attachment.setFileName(MimeUtility.encodeText(file.getName()));  //得到文件名同样放入BodyPart
                main.addBodyPart(attachment);
            }
        }

        return main;
    }

    public static void main(String[] args) throws Exception{
        List<String> list = new ArrayList<String>(){
            {
                add("2");
                add("3");
            }
        };
        list.add("1724537432@qq.com");
        List<File> files = new ArrayList<>();
        files.add(new File("D:\\MyJavaProject\\spring-boot-demo\\src\\main\\resources\\static\\img\\fileIcon\\zip.png"));
        sendMailWithAttachment(list,"测试啊 兄弟","这是一个测试邮件,请审核 谢谢!",files);
    }

}

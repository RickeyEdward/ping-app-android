package com.Utils.vpdntestapp;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailUtils {

//  public static void main(String[] arg) throws Exception {
//    sendEmailWithFile("panqi@qipeng.com", "Every1Knows", "15826985247@163.com", "panqi@qipeng.com",
//      new File("/Users/qp/Desktop/data"));
//  }

  /**
   * 发送带附件邮件（目前有个bug，第一次密码输错了，后面输入对了验证也会不通过。需要重启app）
   *
   * @param userName 邮箱账号
   * @param password 邮箱密码
   * @param addressee 收信邮箱地址
   * @param sender 发件人地址
   * @param file 附件路径
   */
  public static void sendEmailWithFile(String userName, String password, String addressee,
    String sender, File file)
    throws Exception {
    final String user = userName;
    final String pwd = password;

    // 获取系统属性
    Properties properties = new Properties();
    // 设置邮件服务器
    properties.setProperty("mail.transport.protocol", "smtp");
    properties.setProperty("mail.debug", "true");
    properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    properties.setProperty("mail.smtp.socketFactory.fallback", "false");
    properties.setProperty("mail.smtp.ssl.enable", "true");
    properties.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
    properties.setProperty("mail.smtp.port", "465");
    properties.setProperty("mail.smtp.auth", "true");
    properties.setProperty("mail.smtp.socketFactory.port", "465");
    properties.setProperty("mail.smtp.timeout", "10000");

    // 获取默认session对象
    javax.mail.Session session = Session.getDefaultInstance(properties,
      new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          // 登陆邮件发送服务器的用户名和密码
          return new PasswordAuthentication(
            user, pwd);
        }
      });

    MimeBodyPart text = new MimeBodyPart();
    text.setContent("<h4>fromVpdnTestApp</h4>", "text/html;charset=UTF-8");

    //创建邮件附件
    MimeBodyPart attach = new MimeBodyPart();
    DataHandler dataHandler = new DataHandler(new FileDataSource(file));
    attach.setDataHandler(dataHandler);
    attach.setFileName(dataHandler.getName());

    // 创建默认的 MimeMessage 对象
    MimeMessage message = new MimeMessage(session);

    // Set From: 头部头字段
    InternetAddress address = new InternetAddress(sender);
    message.setFrom(address);
    message.addRecipient(Message.RecipientType.CC, address);

    // Set To: 头部头字段
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(addressee));

    //创建容器描述数据关系
    MimeMultipart mimeMultipart = new MimeMultipart();
    mimeMultipart.addBodyPart(text);
    mimeMultipart.addBodyPart(attach);
    mimeMultipart.setSubType("mixed");

    message.setSubject("这里是测试结果，请查收~");
    message.setContent(mimeMultipart);
    message.saveChanges();

    Transport transport = session.getTransport();
    transport.connect();
    transport.sendMessage(message, message.getAllRecipients());
    transport.close();
  }

}

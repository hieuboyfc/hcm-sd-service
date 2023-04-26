package com.xdp.module_spfood.service.impl;

import com.xdp.lib.exceptions.BusinessException;
import com.xdp.module_spfood.service.EmailService;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    private static final String MAIL_USERNAME = "hieudt@sicix.com.vn";
    private static final String MAIL_PASSWORD = "Bacu1997";
    private static final String HOST = "smtp.office365.com";
    private static final int PORT = 587;
    private static final String UTF_8 = "utf-8";
    private static final String TLS = "true";
    private static final String FROM = "hieudt@sicix.com.vn";

    @Override
    @Transactional
    public void sendMail(String email) {
        /*try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setDefaultEncoding(UTF_8);
            mailSender.setHost(HOST);
            mailSender.setPort(PORT);
            mailSender.setUsername(MAIL_USERNAME);
            mailSender.setPassword(MAIL_PASSWORD);

            Properties properties = new Properties();
            properties.setProperty("mail.mime.charset", UTF_8);
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", TLS);
            mailSender.setJavaMailProperties(properties);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, UTF_8);

            helper.setSubject("[SiciX] Notice of request to change user password !!!");
            helper.setFrom(FROM);
            helper.setTo(email);
            boolean html = true;
            helper.setText("Xin chào: <b>" + email + "</b>" +
                            ",<br>Mật khẩu của bạn đã được khôi phục lại thành: <b>SiciX@2023</b>" +
                            ",<br>Xin chân cảm ơn.",
                    html);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }*/
    }

}

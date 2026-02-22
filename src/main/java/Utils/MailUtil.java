package Utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class MailUtil {

    private static String from;
    private static String password;
    private static Properties smtpProps;
    private static boolean loaded;

    static {
        try (InputStream is = MailUtil.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                from = p.getProperty("mail.from", "").trim();
                password = p.getProperty("mail.password", "").trim();
                smtpProps = new Properties();
                smtpProps.put("mail.smtp.host", p.getProperty("mail.smtp.host", "smtp.gmail.com"));
                smtpProps.put("mail.smtp.port", p.getProperty("mail.smtp.port", "587"));
                smtpProps.put("mail.smtp.auth", "true");
                smtpProps.put("mail.smtp.starttls.enable", "true");
                loaded = !from.isEmpty() && !password.isEmpty();
            } else {
                loaded = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loaded = false;
        }
    }

    public static boolean isConfigured() {
        return loaded;
    }

    public static void sendVerificationEmail(String toEmail, String verifyLink) {
        if (!loaded) {
            System.out.println("MailUtil: mail.properties chưa cấu hình (mail.from, mail.password). Bỏ qua gửi mail.");
            return;
        }
        try {
            Session session = Session.getInstance(smtpProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "Averis Cosmetics"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Xác thực email - Averis Cosmetics");
            msg.setText("Xin chào,\n\nVui lòng bấm vào link sau để xác thực email của bạn:\n\n" + verifyLink + "\n\nLink có hiệu lực 24 giờ.\n\nAveris Cosmetics", "UTF-8");
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

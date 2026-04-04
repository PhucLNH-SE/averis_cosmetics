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

    public static boolean sendVerificationEmail(String toEmail, String verifyLink) {
        if (!loaded) {
            System.out.println("MailUtil: mail.properties is not configured (mail.from, mail.password). Skipping email.");
            return false;
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
            msg.setSubject("Email Verification - Averis Cosmetics");
            msg.setText(
                    "Hello,\n\n"
                    + "Please click the link below to verify your email address:\n\n"
                    + verifyLink + "\n\n"
                    + "This link will expire in 24 hours.\n\n"
                    + "Averis Cosmetics",
                    "UTF-8"
            );
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static void sendResetPasswordEmail(String toEmail, String resetLink) {
        if (!loaded) {
            System.out.println("MailUtil: mail.properties not configured. Skip sending mail.");
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
            msg.setSubject("Password Reset - Averis Cosmetics");
            msg.setText(
                    "Hello,\n\n"
                    + "You requested to reset your password.\n\n"
                    + "Please click the link below to reset your password:\n\n"
                    + resetLink + "\n\n"
                    + "This link is valid for 15 minutes.\n\n"
                    + "Averis Cosmetics",
                    "UTF-8"
            );

            Transport.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

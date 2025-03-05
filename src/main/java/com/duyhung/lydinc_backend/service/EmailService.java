package com.duyhung.lydinc_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmailAccountGranted(String to, String username, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("vkksieunhan2003@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("QA Learning Account");

        String htmlContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; border: 2px solid #b39858; border-radius: 10px; padding: 20px; max-width: 600px; margin: 0 auto;\">\n" + "    <p style=\"text-align: center; font-size: 18px; font-weight: bold; color: #2196f3;\">\n" + "        <span style=\"color: #2dc26b;\">Account Login Notification - QA Learning Website</span>\n" + "    </p>\n" + "    <p>Dear <strong>" + to + "</strong>,</p>\n" + "    <p>We are sending you the login account details for the QA Learning system.</p>\n" + "    <p>Please visit the link below to access your account:</p>\n" + "    <p><a href=\"http://localhost:5173/login\" target=\"_blank\" rel=\"noopener\">QA Learning Website</a></p>\n" + "    <p><strong>Account details:</strong></p>\n" + "    <ul>\n" + "        <li><strong>Username: </strong>" + username + "</li>\n" + "        <li><strong>Password: </strong>" + password + "</li>\n" + "    </ul>\n" + "    <p><strong><span style=\"color: #e03e2d;\">NOTE: Please change your password immediately after logging in to ensure the security of your account.</span></strong></p>\n" + "    <p style=\"color: #777; font-size: 14px;\">This is an automated email, please do not reply to this email.</p>\n" + "    <p>Best regards,</p>\n" + "    <p style=\"font-weight: bold;\">QA Learning Website</p>\n" + "</div>\n";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(message);
    }

    public void sendEmailChangePassword(String to, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("vkksieunhan2003@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("QA Learning Account");

        String htmlContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; border: 2px solid #b39858; border-radius: 10px; padding: 20px; max-width: 600px; margin: 0 auto;\">\n" +
                "    <p style=\"text-align: center; font-size: 20px; font-weight: bold; color: #2196f3;\">\n" +
                "        <span style=\"color: #2dc26b;\">Change Password Notification - QA Learning Website</span>\n" +
                "    </p>\n" +
                "    <p>Dear <strong>" + to + "</strong>,</p>\n" +
                "    <p>We are notifying you of an update to your login account details in the <strong>QA Learning System</strong> for account:</p>\n" +
                "  <strong> " + username + " </strong>\n" +
                "    <p><strong style=\"color: #e03e2d;\">If you did not request this change, please contact us immediately to secure your account.</strong></p>\n" +
                "    <p>Best regards,</p>\n" +
                "    <p style=\"font-weight: bold;\">QA Learning Website Team</p>\n" +
                "</div>\n";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(message);
    }

    public void sendLinkResetPassword(String to, String username, String resetUrl) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress("vkksieunhan2003@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Reset Password");
        String htmlContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; border: 2px solid #2196F3; border-radius: 10px; padding: 20px; max-width: 600px; margin: 0 auto;\">\n" +
                "    <p style=\"text-align: center; font-size: 18px; font-weight: bold; color: #2196f3;\">Password Reset Request</p>\n" +
                "    <p>Hello <strong>" + username + "</strong>,</p>\n" +
                "    <p>We received a request to reset your password. If you did not request this, please ignore this email.</p>\n" +
                "    <p>To reset your password, please click the button below:</p>\n" +
                "    <div style=\"text-align: center; margin: 20px 0;\">\n" +
                "        <a href=\"" + resetUrl + "\" style=\"background-color: #2196F3; color: #fff; padding: 12px 20px; text-decoration: none; font-size: 16px; border-radius: 5px; display: inline-block;\">\n" +
                "            Reset Password\n" +
                "        </a>\n" +
                "    </div>\n" +
                "    <p>If the button above does not work, you can also use the following link:</p>\n" +
                "    <p style=\"word-break: break-all;\"><a href=\"" + resetUrl + "\" style=\"color: #2196F3;\">" + resetUrl + "</a></p>\n" +
                "    <p>This link will expire in <b>**5 minutes**</b>. Please reset your password before the link expires.</p>\n" +
                "    <p style=\"color: #777; font-size: 14px;\">This is an automated email. Please do not reply to this message.</p>\n" +
                "    <p>Best regards,</p>\n" +
                "    <p style=\"font-weight: bold;\">LYDINC QA WEBSITE</p>\n" +
                "</div>\n";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(message);
    }

}

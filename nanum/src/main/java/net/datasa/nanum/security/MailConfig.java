// package net.datasa.nanum.security;

// import java.util.Properties;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.JavaMailSenderImpl;

// @Configuration
// public class MailConfig {

// @Bean
// public JavaMailSender mailSender() {
// JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
// javaMailSender.setHost("smtp.naver.com");
// javaMailSender.setUsername("lsh94109"); // 네이버 아이디
// javaMailSender.setPassword("password"); // 네이버 비밀번호
// javaMailSender.setPort(465); // 엥 포트번호가 바뀐건가?
// // javaMailSender.setPort(587);

// javaMailSender.setJavaMailProperties(getMailProperties());
// javaMailSender.setDefaultEncoding("UTF-8");

// return javaMailSender;
// }

// private Properties getMailProperties() {
// Properties properties = new Properties();
// properties.setProperty("mail.transport.protocol", "smtp"); // 프로토콜 설정
// properties.setProperty("mail.smtp.auth", "true"); // smtp 인증
// // properties.setProperty("mail.smtp.starttls.enable", "true"); // smtp
// // strattles 사용
// properties.setProperty("mail.smtp.ssl.trust", "smtp.naver.com"); // ssl 인증
// 서버는 smtp.naver.com
// properties.setProperty("mail.debug", "true"); // 디버그 사용
// properties.setProperty("mail.smtp.ssl.enable", "true"); // ssl 사용
// return properties;
// }

// }

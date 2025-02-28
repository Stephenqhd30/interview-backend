package com.stephen.interview.config.mail;

import com.stephen.interview.config.mail.condition.MailCondition;
import com.stephen.interview.config.mail.properties.MailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;

/**
 * 邮件配置属性
 *
 * @author stephen qiu
 */
@Configuration
@Conditional(MailCondition.class)
@Slf4j
public class MailConfiguration {
	
	@Resource
	private MailProperties mailProperties;
	
	/**
	 * JavaMailSender
	 *
	 * @return JavaMailSender
	 */
	@Bean("javaMailSenderBean")
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailProperties.getHost());
		mailSender.setPort(mailProperties.getPort());
		mailSender.setUsername(mailProperties.getUsername());
		mailSender.setPassword(mailProperties.getPassword());
		
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		return mailSender;
	}
	
	/**
	 * 依赖注入日志输出
	 */
	@PostConstruct
	private void initDi() {
		log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
	}
}
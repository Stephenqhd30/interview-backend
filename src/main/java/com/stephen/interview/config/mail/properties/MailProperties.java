package com.stephen.interview.config.mail.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件配置类
 *
 * @author: stephen qiu
 * @create: 2024-11-07 13:37
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
	
	/**
	 * 是否开启邮件功能
	 */
	private Boolean enable = false;
	
	/**
	 * 邮件服务器地址
	 */
	private String host;
	
	/**
	 * 邮件服务器端口
	 */
	private Integer port;
	
	/**
	 * 邮件服务器用户名
	 */
	private String username;
	
	/**
	 * 邮件服务器密码
	 */
	private String password;
	
}

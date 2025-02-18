package com.stephen.interview.utils.document.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误记录
 *
 * @author stephen qiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRecord<T> {
	/**
	 * 导入的用户数据
	 */
	private T data;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
}

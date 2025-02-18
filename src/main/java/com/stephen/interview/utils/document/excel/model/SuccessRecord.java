package com.stephen.interview.utils.document.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成功记录
 *
 * @author stephen qiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessRecord<T> {
	/**
	 * 导入的用户数据
	 */
	private T data;
	
	/**
	 * 导入成功信息
	 */
	private String message;
}

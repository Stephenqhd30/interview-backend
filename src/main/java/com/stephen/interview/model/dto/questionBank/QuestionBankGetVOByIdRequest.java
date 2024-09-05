package com.stephen.interview.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;

/**
* @author: stephen qiu
* @create: 2024-09-05 20:20
**/
@Data
public class QuestionBankGetVOByIdRequest implements Serializable {
	
	private static final long serialVersionUID = -6594828322246562627L;
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 是否需要关联查询题目信息
	 */
	private Boolean needQueryQuestionList;
}

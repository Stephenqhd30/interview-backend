package com.stephen.interview.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: stephen qiu
 * @create: 2024-09-05 20:32
 **/
@Data
public class QuestionBankQuestionRemoveRequest implements Serializable {
	private static final long serialVersionUID = 2930922849569191710L;
	
	/**
	 * questionBankId
	 */
	private Long questionBankId;
	
	/**
	 * questionId
	 */
	private Long questionId;
}

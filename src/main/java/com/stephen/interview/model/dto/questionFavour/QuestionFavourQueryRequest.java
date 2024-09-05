package com.stephen.interview.model.dto.questionFavour;

import com.stephen.interview.common.PageRequest;
import com.stephen.interview.model.dto.question.QuestionQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询题目收藏请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionFavourQueryRequest extends PageRequest implements Serializable {
	
	private QuestionQueryRequest questionQueryRequest;
	
	/**
	 * 题目 id
	 */
	private Long questionId;
	
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	
	private static final long serialVersionUID = 1L;
}
package com.stephen.interview.model.vo;

import cn.hutool.json.JSONUtil;
import com.stephen.interview.model.entity.QuestionBankQuestion;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题库题目关系视图
 *
 * @author stephen
 */
@Data
public class QuestionBankQuestionVO implements Serializable {
	
	private static final long serialVersionUID = -7970960776943321491L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 题库id
	 */
	private Long questionBankId;
	
	/**
	 * 题目id
	 */
	private Long questionId;
	
	/**
	 * 创建人id
	 */
	private Long userId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 创建用户信息
	 */
	private UserVO userVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param questionBankQuestionVO questionBankQuestionVO
	 * @return QuestionBankQuestion
	 */
	public static QuestionBankQuestion voToObj(QuestionBankQuestionVO questionBankQuestionVO) {
		if (questionBankQuestionVO == null) {
			return null;
		}
		QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
		BeanUtils.copyProperties(questionBankQuestionVO, questionBankQuestion);
		return questionBankQuestion;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param questionBankQuestion questionBankQuestion
	 * @return QuestionBankQuestionVO
	 */
	public static QuestionBankQuestionVO objToVo(QuestionBankQuestion questionBankQuestion) {
		if (questionBankQuestion == null) {
			return null;
		}
		QuestionBankQuestionVO questionBankQuestionVO = new QuestionBankQuestionVO();
		BeanUtils.copyProperties(questionBankQuestion, questionBankQuestionVO);
		return questionBankQuestionVO;
	}
}

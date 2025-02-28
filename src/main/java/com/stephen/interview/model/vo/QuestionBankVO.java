package com.stephen.interview.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionBank;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题库视图
 *
 * @author stephen
 */
@Data
public class QuestionBankVO implements Serializable {
	
	private static final long serialVersionUID = -3719293561347163911L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 图片
	 */
	private String picture;
	
	/**
	 * 创建用户id
	 */
	private Long userId;
	
	/**
	 * 编辑时间
	 */
	private Date editTime;
	
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
	 * 题库里的题目列表(分页)
	 */
	private Page<Question> questionPage;
	
	/**
	 * 封装类转对象
	 *
	 * @param questionBankVO questionBankVO
	 * @return QuestionBank
	 */
	public static QuestionBank voToObj(QuestionBankVO questionBankVO) {
		if (questionBankVO == null) {
			return null;
		}
		QuestionBank questionBank = new QuestionBank();
		BeanUtils.copyProperties(questionBankVO, questionBank);
		return questionBank;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param questionBank questionBank
	 * @return QuestionBankVO
	 */
	public static QuestionBankVO objToVo(QuestionBank questionBank) {
		if (questionBank == null) {
			return null;
		}
		QuestionBankVO questionBankVO = new QuestionBankVO();
		BeanUtils.copyProperties(questionBank, questionBankVO);
		return questionBankVO;
	}
}

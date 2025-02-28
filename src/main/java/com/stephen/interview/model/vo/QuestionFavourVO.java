package com.stephen.interview.model.vo;

import com.stephen.interview.model.entity.QuestionFavour;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目收藏视图
 *
 * @author stephen
 */
@Data
public class QuestionFavourVO implements Serializable {
	
	private static final long serialVersionUID = -7181209070729195079L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 题目 id
	 */
	private Long questionId;
	
	/**
	 * 创建用户 id
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
	 * @param questionFavourVO questionFavourVO
	 * @return QuestionFavour
	 */
	public static QuestionFavour voToObj(QuestionFavourVO questionFavourVO) {
		if (questionFavourVO == null) {
			return null;
		}
		QuestionFavour questionFavour = new QuestionFavour();
		BeanUtils.copyProperties(questionFavourVO, questionFavour);
		return questionFavour;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param questionFavour questionFavour
	 * @return QuestionFavourVO
	 */
	public static QuestionFavourVO objToVo(QuestionFavour questionFavour) {
		if (questionFavour == null) {
			return null;
		}
		QuestionFavourVO questionFavourVO = new QuestionFavourVO();
		BeanUtils.copyProperties(questionFavour, questionFavourVO);
		return questionFavourVO;
	}
}

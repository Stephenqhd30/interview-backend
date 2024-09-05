package com.stephen.interview.model.vo;

import cn.hutool.json.JSONUtil;
import com.stephen.interview.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目视图
 *
 * @author stephen
 */
@Data
public class QuestionVO implements Serializable {
	
	private static final long serialVersionUID = 4886639736047636709L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 标签列表（json数组）
	 */
	private List<String> tagList;
	
	/**
	 * 推荐答案
	 */
	private String answer;
	
	/**
	 * 点赞数
	 */
	private Integer thumbNum;
	
	/**
	 * 收藏数
	 */
	private Integer favourNum;
	
	/**
	 * 创建用户 id
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
	 * 是否点赞
	 */
	private Boolean hasThumb;
	
	/**
	 * 是否收藏
	 */
	private Boolean hasFavour;
	
	/**
	 * 封装类转对象
	 *
	 * @param questionVO questionVO
	 * @return Question
	 */
	public static Question voToObj(QuestionVO questionVO) {
		if (questionVO == null) {
			return null;
		}
		Question question = new Question();
		BeanUtils.copyProperties(questionVO, question);
		List<String> tagList = questionVO.getTagList();
		question.setTags(JSONUtil.toJsonStr(tagList));
		return question;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param question question
	 * @return QuestionVO
	 */
	public static QuestionVO objToVo(Question question) {
		if (question == null) {
			return null;
		}
		QuestionVO questionVO = new QuestionVO();
		BeanUtils.copyProperties(question, questionVO);
		questionVO.setTagList(JSONUtil.toList(question.getTags(), String.class));
		return questionVO;
	}
}

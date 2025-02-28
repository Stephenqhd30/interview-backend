package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.mapper.QuestionThumbMapper;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionThumb;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.service.QuestionService;
import com.stephen.interview.service.QuestionThumbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 题目点赞服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class QuestionThumbServiceImpl extends ServiceImpl<QuestionThumbMapper, QuestionThumb> implements QuestionThumbService {
	
	@Resource
	private QuestionService questionService;
	
	/**
	 * 点赞
	 *
	 * @param questionId questionId
	 * @param loginUser  loginUser
	 * @return int
	 */
	@Override
	public int doQuestionThumb(long questionId, User loginUser) {
		// 判断实体是否存在，根据类别获取实体
		Question question = questionService.getById(questionId);
		if (question == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
		// 是否已点赞
		long userId = loginUser.getId();
		// 每个用户串行点赞
		// 锁必须要包裹住事务方法
		QuestionThumbService questionThumbService = (QuestionThumbService) AopContext.currentProxy();
		synchronized (String.valueOf(userId).intern()) {
			return questionThumbService.doQuestionThumbInner(userId, questionId);
		}
	}
	
	/**
	 * 封装了事务的方法
	 *
	 * @param userId     userId
	 * @param questionId questionId
	 * @return int
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int doQuestionThumbInner(long userId, long questionId) {
		QuestionThumb questionThumb = new QuestionThumb();
		questionThumb.setUserId(userId);
		questionThumb.setQuestionId(questionId);
		QueryWrapper<QuestionThumb> thumbQueryWrapper = new QueryWrapper<>(questionThumb);
		QuestionThumb oldQuestionThumb = this.getOne(thumbQueryWrapper);
		boolean result;
		// 已点赞
		if (oldQuestionThumb != null) {
			result = this.remove(thumbQueryWrapper);
			if (result) {
				// 点赞数 - 1
				result = questionService.update()
						.eq("id", questionId)
						.gt("thumbNum", 0)
						.setSql("thumbNum = thumbNum - 1")
						.update();
				return result ? -1 : 0;
			} else {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR);
			}
		} else {
			// 未点赞
			result = this.save(questionThumb);
			if (result) {
				// 点赞数 + 1
				result = questionService.update()
						.eq("id", questionId)
						.setSql("thumbNum = thumbNum + 1")
						.update();
				return result ? 1 : 0;
			} else {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR);
			}
		}
	}
	
}

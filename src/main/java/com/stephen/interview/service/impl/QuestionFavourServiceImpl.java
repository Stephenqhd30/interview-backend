package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ThrowUtils;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.mapper.QuestionFavourMapper;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionFavour;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.service.QuestionFavourService;
import com.stephen.interview.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 题目收藏服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour> implements QuestionFavourService {
	
	@Resource
	private QuestionService questionService;
	
	/**
	 * 帖子收藏
	 *
	 * @param questionId questionId
	 * @param loginUser  loginUser
	 * @return int
	 */
	@Override
	public int doQuestionFavour(long questionId, User loginUser) {
		// 判断是否存在
		Question question = questionService.getById(questionId);
		ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
		// 是否已帖子收藏
		long userId = loginUser.getId();
		// 每个用户串行帖子收藏
		// 锁必须要包裹住事务方法
		QuestionFavourService questionFavourService = (QuestionFavourService) AopContext.currentProxy();
		synchronized (String.valueOf(userId).intern()) {
			return questionFavourService.doQuestionFavourInner(userId, questionId);
		}
	}
	
	/**
	 * 分页获取题目点赞信息
	 *
	 * @param page         page
	 * @param queryWrapper queryWrapper
	 * @param favourUserId favourUserId
	 * @return Page<Question>
	 */
	@Override
	public Page<Question> listFavourQuestionByPage(IPage<Question> page, Wrapper<Question> queryWrapper, long favourUserId) {
		if (favourUserId <= 0) {
			return new Page<>();
		}
		return baseMapper.listFavourQuestionByPage(page, queryWrapper, favourUserId);
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
	public int doQuestionFavourInner(long userId, long questionId) {
		QuestionFavour questionFavour = new QuestionFavour();
		questionFavour.setUserId(userId);
		questionFavour.setQuestionId(questionId);
		QueryWrapper<QuestionFavour> questionFavourQueryWrapper = new QueryWrapper<>(questionFavour);
		QuestionFavour oldQuestionFavour = this.getOne(questionFavourQueryWrapper);
		boolean result;
		// 已收藏
		if (oldQuestionFavour != null) {
			result = this.remove(questionFavourQueryWrapper);
			if (result) {
				// 帖子收藏数 - 1
				result = questionService.update()
						.eq("id", questionId)
						.gt("favourNum", 0)
						.setSql("favourNum = favourNum - 1")
						.update();
				return result ? -1 : 0;
			} else {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR);
			}
		} else {
			// 未帖子收藏
			result = this.save(questionFavour);
			if (result) {
				// 帖子收藏数 + 1
				result = questionService.update()
						.eq("id", questionId)
						.setSql("favourNum = favourNum + 1")
						.update();
				return result ? 1 : 0;
			} else {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR);
			}
		}
	}
	
}

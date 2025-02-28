package com.stephen.interview.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.interview.common.BaseResponse;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ResultUtils;
import com.stephen.interview.common.ThrowUtils;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.model.dto.question.QuestionQueryRequest;
import com.stephen.interview.model.dto.questionFavour.QuestionFavourAddRequest;
import com.stephen.interview.model.dto.questionFavour.QuestionFavourQueryRequest;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.QuestionVO;
import com.stephen.interview.service.QuestionFavourService;
import com.stephen.interview.service.QuestionService;
import com.stephen.interview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/questionFavour")
@Slf4j
public class QuestionFavourController {
	
	@Resource
	private QuestionFavourService questionFavourService;
	
	@Resource
	private QuestionService questionService;
	
	@Resource
	private UserService userService;
	
	/**
	 * 收藏 / 取消收藏
	 *
	 * @param questionFavourAddRequest questionFavourAddRequest
	 * @param request                  request
	 * @return BaseResponse<Integer>
	 */
	@PostMapping("/")
	public BaseResponse<Integer> doQuestionFavour(@RequestBody QuestionFavourAddRequest questionFavourAddRequest,
	                                              HttpServletRequest request) {
		if (questionFavourAddRequest == null || questionFavourAddRequest.getQuestionId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 登录才能操作
		final User loginUser = userService.getLoginUser(request);
		long questionId = questionFavourAddRequest.getQuestionId();
		int result = questionFavourService.doQuestionFavour(questionId, loginUser);
		return ResultUtils.success(result);
	}
	
	/**
	 * 获取我收藏的帖子列表
	 *
	 * @param questionQueryRequest questionQueryRequest
	 * @param request              request
	 * @return BaseResponse<Page < QuestionVO>>
	 */
	@PostMapping("/my/list/page")
	public BaseResponse<Page<QuestionVO>> listMyFavourQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
	                                                                 HttpServletRequest request) {
		if (questionQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		long current = questionQueryRequest.getCurrent();
		long size = questionQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<Question> questionPage = questionFavourService.listFavourQuestionByPage(new Page<>(current, size),
				questionService.getQueryWrapper(questionQueryRequest), loginUser.getId());
		return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
	}
	
	/**
	 * 获取用户收藏的帖子列表
	 *
	 * @param questionFavourQueryRequest questionFavourQueryRequest
	 * @param request                    request
	 * @return BaseResponse<Page < QuestionVO>>
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<QuestionVO>> listFavourQuestionByPage(@RequestBody QuestionFavourQueryRequest questionFavourQueryRequest,
	                                                               HttpServletRequest request) {
		if (questionFavourQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long current = questionFavourQueryRequest.getCurrent();
		long size = questionFavourQueryRequest.getPageSize();
		Long userId = questionFavourQueryRequest.getUserId();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
		Page<Question> questionPage = questionFavourService.listFavourQuestionByPage(new Page<>(current, size),
				questionService.getQueryWrapper(questionFavourQueryRequest.getQuestionQueryRequest()), userId);
		return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
	}
}

package com.stephen.interview.controller;

import com.stephen.interview.common.BaseResponse;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ResultUtils;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.model.dto.questionThumb.QuestionThumbAddRequest;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.service.QuestionThumbService;
import com.stephen.interview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目点赞接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/questionThumb")
@Slf4j
public class QuestionThumbController {
	
	@Resource
	private QuestionThumbService questionThumbService;
	
	@Resource
	private UserService userService;
	
	/**
	 * 点赞 / 取消点赞
	 *
	 * @param questionThumbAddRequest questionThumbAddRequest
	 * @param request                 request
	 * @return BaseResponse<Integer> resultNum 本次点赞变化数
	 */
	@PostMapping("/")
	public BaseResponse<Integer> doThumb(@RequestBody QuestionThumbAddRequest questionThumbAddRequest,
	                                     HttpServletRequest request) {
		if (questionThumbAddRequest == null || questionThumbAddRequest.getQuestionId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 登录才能点赞
		final User loginUser = userService.getLoginUser(request);
		long questionId = questionThumbAddRequest.getQuestionId();
		int result = questionThumbService.doQuestionThumb(questionId, loginUser);
		return ResultUtils.success(result);
	}
	
	// endregion
}

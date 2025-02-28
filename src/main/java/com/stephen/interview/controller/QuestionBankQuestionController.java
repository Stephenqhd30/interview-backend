package com.stephen.interview.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.interview.common.*;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.constants.UserConstant;
import com.stephen.interview.model.dto.questionBankQuestion.QuestionBankQuestionAddRequest;
import com.stephen.interview.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.stephen.interview.model.dto.questionBankQuestion.QuestionBankQuestionRemoveRequest;
import com.stephen.interview.model.dto.questionBankQuestion.QuestionBankQuestionUpdateRequest;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionBank;
import com.stephen.interview.model.entity.QuestionBankQuestion;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.QuestionBankQuestionVO;
import com.stephen.interview.service.QuestionBankQuestionService;
import com.stephen.interview.service.QuestionBankService;
import com.stephen.interview.service.QuestionService;
import com.stephen.interview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库题目关系接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/questionBankQuestion")
@Slf4j
public class QuestionBankQuestionController {
	
	@Resource
	private QuestionBankQuestionService questionBankQuestionService;
	
	@Resource
	private QuestionService questionService;
	
	@Resource
	private QuestionBankService questionBankService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 创建题库题目关系
	 *
	 * @param questionBankQuestionAddRequest questionBankQuestionAddRequest
	 * @param request                        request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Long> addQuestionBankQuestion(@RequestBody QuestionBankQuestionAddRequest questionBankQuestionAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(questionBankQuestionAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
		BeanUtils.copyProperties(questionBankQuestionAddRequest, questionBankQuestion);
		// 数据校验
		questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion, true);
		Long questionId = questionBankQuestion.getQuestionId();
		Long questionBankId = questionBankQuestion.getQuestionBankId();
		if (ObjectUtils.isNotEmpty(questionBankId)) {
			QuestionBank questionBank = questionBankService.getById(questionBankId);
			ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
		}
		if (ObjectUtils.isNotEmpty(questionId)) {
			Question question = questionService.getById(questionId);
			ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
		}
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		questionBankQuestion.setUserId(loginUser.getId());
		// 写入数据库
		boolean result = questionBankQuestionService.save(questionBankQuestion);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newQuestionBankQuestionId = questionBankQuestion.getId();
		return ResultUtils.success(newQuestionBankQuestionId);
	}
	
	/**
	 * 删除题库题目关系
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Boolean> deleteQuestionBankQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
		ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldQuestionBankQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = questionBankQuestionService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新题库题目关系（仅管理员可用）
	 *
	 * @param questionBankQuestionUpdateRequest questionBankQuestionUpdateRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateQuestionBankQuestion(@RequestBody QuestionBankQuestionUpdateRequest questionBankQuestionUpdateRequest) {
		if (questionBankQuestionUpdateRequest == null || questionBankQuestionUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
		BeanUtils.copyProperties(questionBankQuestionUpdateRequest, questionBankQuestion);
		// 数据校验
		questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion, false);
		// 判断是否存在
		long id = questionBankQuestionUpdateRequest.getId();
		QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
		ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = questionBankQuestionService.updateById(questionBankQuestion);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取题库题目关系（封装类）
	 *
	 * @param id id
	 * @return BaseResponse<QuestionBankQuestionVO>
	 */
	@GetMapping("/get/vo")
	public BaseResponse<QuestionBankQuestionVO> getQuestionBankQuestionVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		QuestionBankQuestion questionBankQuestion = questionBankQuestionService.getById(id);
		ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVO(questionBankQuestion, request));
	}
	
	/**
	 * 分页获取题库题目关系列表（仅管理员可用）
	 *
	 * @param questionBankQuestionQueryRequest questionBankQuestionQueryRequest
	 * @return BaseResponse<Page < QuestionBankQuestion>>
	 */
	@PostMapping("/list/page")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<QuestionBankQuestion>> listQuestionBankQuestionByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
		long current = questionBankQuestionQueryRequest.getCurrent();
		long size = questionBankQuestionQueryRequest.getPageSize();
		// 查询数据库
		Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(new Page<>(current, size),
				questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
		return ResultUtils.success(questionBankQuestionPage);
	}
	
	/**
	 * 分页获取题库题目关系列表（封装类）
	 *
	 * @param questionBankQuestionQueryRequest questionBankQuestionQueryRequest
	 * @param request                          request
	 * @return BaseResponse<Page < QuestionBankQuestionVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<QuestionBankQuestionVO>> listQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest,
	                                                                                   HttpServletRequest request) {
		long current = questionBankQuestionQueryRequest.getCurrent();
		long size = questionBankQuestionQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(new Page<>(current, size),
				questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
		// 获取封装类
		return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVOPage(questionBankQuestionPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的题库题目关系列表
	 *
	 * @param questionBankQuestionQueryRequest questionBankQuestionQueryRequest
	 * @param request                          request
	 * @return BaseResponse<Page < QuestionBankQuestionVO>>
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<QuestionBankQuestionVO>> listMyQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest,
	                                                                                     HttpServletRequest request) {
		ThrowUtils.throwIf(questionBankQuestionQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		questionBankQuestionQueryRequest.setUserId(loginUser.getId());
		long current = questionBankQuestionQueryRequest.getCurrent();
		long size = questionBankQuestionQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(new Page<>(current, size),
				questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
		// 获取封装类
		return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVOPage(questionBankQuestionPage, request));
	}
	
	// endregion
	
	/**
	 * 移除题库题目关联
	 *
	 * @param questionBankQuestionRemoveRequest questionBankQuestionRemoveRequest
	 * @param request                           request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/remove")
	public BaseResponse<Boolean> removeQuestionBankQuestion(@RequestBody QuestionBankQuestionRemoveRequest questionBankQuestionRemoveRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(questionBankQuestionRemoveRequest == null, ErrorCode.PARAMS_ERROR);
		Long questionBankId = questionBankQuestionRemoveRequest.getQuestionBankId();
		Long questionId = questionBankQuestionRemoveRequest.getQuestionId();
		LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
				.eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
				.eq(QuestionBankQuestion::getQuestionId, questionId);
		// 操作数据库
		boolean remove = questionBankQuestionService.remove(lambdaQueryWrapper);
		ThrowUtils.throwIf(!remove, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
}

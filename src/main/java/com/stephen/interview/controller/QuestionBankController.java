package com.stephen.interview.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.interview.common.*;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.constants.UserConstant;
import com.stephen.interview.model.dto.question.QuestionQueryRequest;
import com.stephen.interview.model.dto.questionBank.*;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionBank;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.QuestionBankVO;
import com.stephen.interview.service.QuestionBankService;
import com.stephen.interview.service.QuestionService;
import com.stephen.interview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/questionBank")
@Slf4j
public class QuestionBankController {
	
	@Resource
	private QuestionService questionService;
	
	@Resource
	private QuestionBankService questionBankService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 创建题库
	 *
	 * @param questionBankAddRequest questionBankAddRequest
	 * @param request                request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(questionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		QuestionBank questionBank = new QuestionBank();
		BeanUtils.copyProperties(questionBankAddRequest, questionBank);
		// 数据校验
		questionBankService.validQuestionBank(questionBank, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		questionBank.setUserId(loginUser.getId());
		// 写入数据库
		boolean result = questionBankService.save(questionBank);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newQuestionBankId = questionBank.getId();
		return ResultUtils.success(newQuestionBankId);
	}
	
	/**
	 * 删除题库
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		QuestionBank oldQuestionBank = questionBankService.getById(id);
		ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = questionBankService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新题库（仅管理员可用）
	 *
	 * @param questionBankUpdateRequest questionBankUpdateRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
		if (questionBankUpdateRequest == null || questionBankUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		QuestionBank questionBank = new QuestionBank();
		BeanUtils.copyProperties(questionBankUpdateRequest, questionBank);
		// 数据校验
		questionBankService.validQuestionBank(questionBank, false);
		// 判断是否存在
		long id = questionBankUpdateRequest.getId();
		QuestionBank oldQuestionBank = questionBankService.getById(id);
		ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = questionBankService.updateById(questionBank);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取题库（封装类）
	 * 可以关联查询题目信息
	 *
	 * @param questionBankGetVOByIdRequest questionBankGetVOByIdRequest
	 * @return BaseResponse<QuestionBankVO>
	 */
	@GetMapping("/get/vo")
	public BaseResponse<QuestionBankVO> getQuestionBankVOById(QuestionBankGetVOByIdRequest questionBankGetVOByIdRequest, HttpServletRequest request) {
		
		ThrowUtils.throwIf(questionBankGetVOByIdRequest == null, ErrorCode.PARAMS_ERROR);
		long id = questionBankGetVOByIdRequest.getId();
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		QuestionBank questionBank = questionBankService.getById(id);
		ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);
		QuestionBankVO questionBankVO = questionBankService.getQuestionBankVO(questionBank, request);
		// 是否需要查询题库列表
		Boolean needQueryQuestionList = questionBankGetVOByIdRequest.getNeedQueryQuestionList();
		if (ObjectUtils.isNotEmpty(needQueryQuestionList) && needQueryQuestionList) {
			QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
			questionQueryRequest.setQuestionBankId(id);
			Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
			questionBankVO.setQuestionPage(questionPage);
		}
		// 获取封装类
		return ResultUtils.success(questionBankVO);
	}
	
	/**
	 * 分页获取题库列表（仅管理员可用）
	 *
	 * @param questionBankQueryRequest questionBankQueryRequest
	 * @return BaseResponse<Page < QuestionBank>>
	 */
	@PostMapping("/list/page")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
		long current = questionBankQueryRequest.getCurrent();
		long size = questionBankQueryRequest.getPageSize();
		// 查询数据库
		Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
				questionBankService.getQueryWrapper(questionBankQueryRequest));
		return ResultUtils.success(questionBankPage);
	}
	
	/**
	 * 分页获取题库列表（封装类）
	 *
	 * @param questionBankQueryRequest questionBankQueryRequest
	 * @param request                  request
	 * @return BaseResponse<Page < QuestionBankVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
	                                                                   HttpServletRequest request) {
		long current = questionBankQueryRequest.getCurrent();
		long size = questionBankQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
				questionBankService.getQueryWrapper(questionBankQueryRequest));
		// 获取封装类
		return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的题库列表
	 *
	 * @param questionBankQueryRequest questionBankQueryRequest
	 * @param request                  request
	 * @return BaseResponse<Page < QuestionBankVO>>
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
	                                                                     HttpServletRequest request) {
		ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		questionBankQueryRequest.setUserId(loginUser.getId());
		long current = questionBankQueryRequest.getCurrent();
		long size = questionBankQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
				questionBankService.getQueryWrapper(questionBankQueryRequest));
		// 获取封装类
		return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
	}
	
	/**
	 * 编辑题库（给用户使用）
	 *
	 * @param questionBankEditRequest questionBankEditRequest
	 * @param request                 request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest questionBankEditRequest, HttpServletRequest request) {
		if (questionBankEditRequest == null || questionBankEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		QuestionBank questionBank = new QuestionBank();
		BeanUtils.copyProperties(questionBankEditRequest, questionBank);
		// 数据校验
		questionBankService.validQuestionBank(questionBank, false);
		User loginUser = userService.getLoginUser(request);
		// 判断是否存在
		long id = questionBankEditRequest.getId();
		QuestionBank oldQuestionBank = questionBankService.getById(id);
		ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可编辑
		if (!oldQuestionBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = questionBankService.updateById(questionBank);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	// endregion
}

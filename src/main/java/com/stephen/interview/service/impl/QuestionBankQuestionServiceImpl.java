package com.stephen.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ThrowUtils;
import com.stephen.interview.constants.CommonConstant;
import com.stephen.interview.mapper.QuestionBankQuestionMapper;
import com.stephen.interview.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.stephen.interview.model.entity.QuestionBankQuestion;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.QuestionBankQuestionVO;
import com.stephen.interview.model.vo.UserVO;
import com.stephen.interview.service.QuestionBankQuestionService;
import com.stephen.interview.service.UserService;
import com.stephen.interview.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库题目关系服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {
	
	@Resource
	private UserService userService;
	
	/**
	 * 校验数据
	 *
	 * @param questionBankQuestion questionBankQuestion
	 * @param add                  对创建的数据进行校验
	 */
	@Override
	public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
		ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long questionBankId = questionBankQuestion.getQuestionBankId();
		Long questionId = questionBankQuestion.getQuestionId();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(questionBankId), ErrorCode.PARAMS_ERROR, "题库id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(questionId), ErrorCode.PARAMS_ERROR, "题目id不能为空");
		}
		// 修改数据时，有参数则校验
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param questionBankQuestionQueryRequest questionBankQuestionQueryRequest
	 * @return QueryWrapper<QuestionBankQuestion>
	 */
	@Override
	public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
		QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
		if (questionBankQuestionQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = questionBankQuestionQueryRequest.getId();
		Long notId = questionBankQuestionQueryRequest.getNotId();
		Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
		Long questionId = questionBankQuestionQueryRequest.getQuestionId();
		Long userId = questionBankQuestionQueryRequest.getUserId();
		String sortField = questionBankQuestionQueryRequest.getSortField();
		String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取题库题目关系封装
	 *
	 * @param questionBankQuestion questionBankQuestion
	 * @param request              request
	 * @return QuestionBankQuestionVO
	 */
	@Override
	public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
		// 对象转封装类
		QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = questionBankQuestion.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		questionBankQuestionVO.setUserVO(userVO);
		// endregion
		return questionBankQuestionVO;
	}
	
	/**
	 * 分页获取题库题目关系封装
	 *
	 * @param questionBankQuestionPage questionBankQuestionPage
	 * @param request                  request
	 * @return Page<QuestionBankQuestionVO>
	 */
	@Override
	public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
		List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
		Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
		if (CollUtil.isEmpty(questionBankQuestionList)) {
			return questionBankQuestionVOPage;
		}
		// 对象列表 => 封装对象列表
		List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(QuestionBankQuestionVO::objToVo).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 填充信息
		questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
			Long userId = questionBankQuestionVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			questionBankQuestionVO.setUserVO(userService.getUserVO(user, request));
		});
		// endregion
		
		questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
		return questionBankQuestionVOPage;
	}
}

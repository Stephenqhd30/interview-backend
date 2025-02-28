package com.stephen.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ThrowUtils;
import com.stephen.interview.constants.CommonConstant;
import com.stephen.interview.mapper.QuestionBankMapper;
import com.stephen.interview.model.dto.questionBank.QuestionBankQueryRequest;
import com.stephen.interview.model.entity.QuestionBank;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.QuestionBankVO;
import com.stephen.interview.model.vo.UserVO;
import com.stephen.interview.service.QuestionBankService;
import com.stephen.interview.service.UserService;
import com.stephen.interview.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {
	
	@Resource
	private UserService userService;
	
	/**
	 * 校验数据
	 *
	 * @param questionBank questionBank
	 * @param add          对创建的数据进行校验
	 */
	@Override
	public void validQuestionBank(QuestionBank questionBank, boolean add) {
		ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String title = questionBank.getTitle();
		String description = questionBank.getDescription();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR, "标题不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(title)) {
			ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
		}
		if (StringUtils.isNotBlank(description)) {
			ThrowUtils.throwIf(title.length() > 2048, ErrorCode.PARAMS_ERROR, "描述过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param questionBankQueryRequest questionBankQueryRequest
	 * @return QueryWrapper<QuestionBank>
	 */
	@Override
	public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest) {
		QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
		if (questionBankQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = questionBankQueryRequest.getId();
		Long notId = questionBankQueryRequest.getNotId();
		String searchText = questionBankQueryRequest.getSearchText();
		String title = questionBankQueryRequest.getTitle();
		String description = questionBankQueryRequest.getDescription();
		String picture = questionBankQueryRequest.getPicture();
		Long userId = questionBankQueryRequest.getUserId();
		String sortField = questionBankQueryRequest.getSortField();
		String sortOrder = questionBankQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		if (StringUtils.isNotBlank(searchText)) {
			// 需要拼接查询条件
			queryWrapper.and(qw -> qw.like("title", searchText).or().like("description", searchText));
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
		queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
		queryWrapper.like(StringUtils.isNotBlank(picture), "picture", picture);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取题库封装
	 *
	 * @param questionBank questionBank
	 * @param request      request
	 * @return QuestionBankVO
	 */
	@Override
	public QuestionBankVO getQuestionBankVO(QuestionBank questionBank, HttpServletRequest request) {
		// 对象转封装类
		QuestionBankVO questionBankVO = QuestionBankVO.objToVo(questionBank);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = questionBank.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		questionBankVO.setUserVO(userVO);
		// endregion
		
		return questionBankVO;
	}
	
	/**
	 * 分页获取题库封装
	 *
	 * @param questionBankPage questionBankPage
	 * @param request          request
	 * @return Page<QuestionBankVO>
	 */
	@Override
	public Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage, HttpServletRequest request) {
		List<QuestionBank> questionBankList = questionBankPage.getRecords();
		Page<QuestionBankVO> questionBankVOPage = new Page<>(questionBankPage.getCurrent(), questionBankPage.getSize(), questionBankPage.getTotal());
		if (CollUtil.isEmpty(questionBankList)) {
			return questionBankVOPage;
		}
		// 对象列表 => 封装对象列表
		List<QuestionBankVO> questionBankVOList = questionBankList.stream().map(questionBank -> {
			return QuestionBankVO.objToVo(questionBank);
		}).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = questionBankList.stream().map(QuestionBank::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 填充信息
		questionBankVOList.forEach(questionBankVO -> {
			Long userId = questionBankVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			questionBankVO.setUserVO(userService.getUserVO(user, request));
		});
		// endregion
		
		questionBankVOPage.setRecords(questionBankVOList);
		return questionBankVOPage;
	}
	
}

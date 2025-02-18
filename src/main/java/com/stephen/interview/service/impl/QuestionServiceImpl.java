package com.stephen.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ThrowUtils;
import com.stephen.interview.constants.CommonConstant;
import com.stephen.interview.mapper.QuestionFavourMapper;
import com.stephen.interview.mapper.QuestionMapper;
import com.stephen.interview.mapper.QuestionThumbMapper;
import com.stephen.interview.model.dto.question.QuestionQueryRequest;
import com.stephen.interview.model.entity.*;
import com.stephen.interview.model.vo.QuestionVO;
import com.stephen.interview.model.vo.UserVO;
import com.stephen.interview.service.QuestionBankQuestionService;
import com.stephen.interview.service.QuestionService;
import com.stephen.interview.service.UserService;
import com.stephen.interview.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private QuestionThumbMapper questionThumbMapper;
	
	@Resource
	private QuestionFavourMapper questionFavourMapper;
	
	
	@Resource
	private QuestionBankQuestionService questionBankQuestionService;
	
	/**
	 * 校验数据
	 *
	 * @param question question
	 * @param add      对创建的数据进行校验
	 */
	@Override
	public void validQuestion(Question question, boolean add) {
		ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String title = question.getTitle();
		String content = question.getContent();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR, "标题不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(content), ErrorCode.PARAMS_ERROR, "内容不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(title)) {
			ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
		}
		if (StringUtils.isNotBlank(content)) {
			ThrowUtils.throwIf(content.length() > 10240, ErrorCode.PARAMS_ERROR, "内容过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param questionQueryRequest questionQueryRequest
	 * @return QueryWrapper<Question>
	 */
	@Override
	public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
		QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
		if (questionQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = questionQueryRequest.getId();
		Long notId = questionQueryRequest.getNotId();
		String searchText = questionQueryRequest.getSearchText();
		String title = questionQueryRequest.getTitle();
		String content = questionQueryRequest.getContent();
		List<String> tagList = questionQueryRequest.getTagList();
		String answer = questionQueryRequest.getAnswer();
		Integer thumbNum = questionQueryRequest.getThumbNum();
		Integer favourNum = questionQueryRequest.getFavourNum();
		Long userId = questionQueryRequest.getUserId();
		String sortField = questionQueryRequest.getSortField();
		String sortOrder = questionQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		if (StringUtils.isNotBlank(searchText)) {
			// 需要拼接查询条件
			queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
		queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
		queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
		// JSON 数组查询
		if (CollUtil.isNotEmpty(tagList)) {
			for (String tag : tagList) {
				queryWrapper.like("tags", "\"" + tag + "\"");
			}
		}
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(thumbNum), "thumbNum", thumbNum);
		queryWrapper.eq(ObjectUtils.isNotEmpty(favourNum), "favourNum", favourNum);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取题目封装
	 *
	 * @param question question
	 * @param request  request
	 * @return QuestionVO
	 */
	@Override
	public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
		// 对象转封装类
		QuestionVO questionVO = QuestionVO.objToVo(question);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = question.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		questionVO.setUserVO(userVO);
		// 2. 已登录，获取用户点赞、收藏状态
		long questionId = question.getId();
		User loginUser = userService.getLoginUserPermitNull(request);
		if (loginUser != null) {
			// 获取点赞
			QueryWrapper<QuestionThumb> questionThumbQueryWrapper = new QueryWrapper<>();
			questionThumbQueryWrapper.in("questionId", questionId);
			questionThumbQueryWrapper.eq("userId", loginUser.getId());
			QuestionThumb questionThumb = questionThumbMapper.selectOne(questionThumbQueryWrapper);
			questionVO.setHasThumb(questionThumb != null);
			// 获取收藏
			QueryWrapper<QuestionFavour> questionFavourQueryWrapper = new QueryWrapper<>();
			questionFavourQueryWrapper.in("questionId", questionId);
			questionFavourQueryWrapper.eq("userId", loginUser.getId());
			QuestionFavour questionFavour = questionFavourMapper.selectOne(questionFavourQueryWrapper);
			questionVO.setHasFavour(questionFavour != null);
		}
		// endregion
		
		return questionVO;
	}
	
	/**
	 * 分页获取题目封装
	 *
	 * @param questionPage questionPage
	 * @param request      request
	 * @return Page<QuestionVO>
	 */
	@Override
	public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
		List<Question> questionList = questionPage.getRecords();
		Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
		if (CollUtil.isEmpty(questionList)) {
			return questionVOPage;
		}
		// 对象列表 => 封装对象列表
		List<QuestionVO> questionVOList = questionList.stream().map(QuestionVO::objToVo).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 2. 已登录，获取用户点赞、收藏状态
		Map<Long, Boolean> questionIdHasThumbMap = new HashMap<>();
		Map<Long, Boolean> questionIdHasFavourMap = new HashMap<>();
		User loginUser = userService.getLoginUserPermitNull(request);
		if (loginUser != null) {
			Set<Long> questionIdSet = questionList.stream().map(Question::getId).collect(Collectors.toSet());
			loginUser = userService.getLoginUser(request);
			// 获取点赞
			QueryWrapper<QuestionThumb> questionThumbQueryWrapper = new QueryWrapper<>();
			questionThumbQueryWrapper.in("questionId", questionIdSet);
			questionThumbQueryWrapper.eq("userId", loginUser.getId());
			List<QuestionThumb> questionQuestionThumbList = questionThumbMapper.selectList(questionThumbQueryWrapper);
			questionQuestionThumbList.forEach(questionQuestionThumb -> questionIdHasThumbMap.put(questionQuestionThumb.getQuestionId(), true));
			// 获取收藏
			QueryWrapper<QuestionFavour> questionFavourQueryWrapper = new QueryWrapper<>();
			questionFavourQueryWrapper.in("questionId", questionIdSet);
			questionFavourQueryWrapper.eq("userId", loginUser.getId());
			List<QuestionFavour> questionFavourList = questionFavourMapper.selectList(questionFavourQueryWrapper);
			questionFavourList.forEach(questionFavour -> questionIdHasFavourMap.put(questionFavour.getQuestionId(), true));
		}
		// 填充信息
		questionVOList.forEach(questionVO -> {
			Long userId = questionVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			questionVO.setUserVO(userService.getUserVO(user, request));
			questionVO.setHasThumb(questionIdHasThumbMap.getOrDefault(questionVO.getId(), false));
			questionVO.setHasFavour(questionIdHasFavourMap.getOrDefault(questionVO.getId(), false));
		});
		// endregion
		
		questionVOPage.setRecords(questionVOList);
		return questionVOPage;
	}
	
	
	/**
	 * 分页获取题目列表
	 *
	 * @param questionQueryRequest questionQueryRequest
	 * @return Page<Question>
	 */
	@Override
	public Page<Question> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
		long current = questionQueryRequest.getCurrent();
		long size = questionQueryRequest.getPageSize();
		// 题目表的查询条件
		QueryWrapper<Question> queryWrapper = this.getQueryWrapper(questionQueryRequest);
		// 根据题库查询题目列表接口
		Long questionBankId = questionQueryRequest.getQuestionBankId();
		if (questionBankId != null) {
			// 查询题库内的题目 id 列表
			LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
					.select(QuestionBankQuestion::getQuestionId)
					.eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
			// 查询数据库
			List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionService.list(lambdaQueryWrapper);
			if (CollUtil.isNotEmpty(questionBankQuestionList)) {
				// 取出题目id集合
				Set<Long> questionIdSet = questionBankQuestionList.stream()
						.map(QuestionBankQuestion::getQuestionId)
						.collect(Collectors.toSet());
				queryWrapper.in("id", questionIdSet);
			}
		}
		// 查询数据库
		return this.page(new Page<>(current, size), queryWrapper);
	}
}

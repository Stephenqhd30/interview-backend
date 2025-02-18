package com.stephen.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ThrowUtils;
import com.stephen.interview.constants.CommonConstant;
import com.stephen.interview.mapper.TagMapper;
import com.stephen.interview.model.dto.tag.TagQueryRequest;
import com.stephen.interview.model.entity.Tag;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.enums.TagIsParentEnum;
import com.stephen.interview.model.vo.TagVO;
import com.stephen.interview.model.vo.UserVO;
import com.stephen.interview.service.TagService;
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
 * 标签服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
	
	@Resource
	private UserService userService;
	
	/**
	 * 校验数据
	 *
	 * @param tag tag
	 * @param add 对创建的数据进行校验
	 */
	@Override
	public void validTag(Tag tag, boolean add) {
		ThrowUtils.throwIf(tag == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String tagName = tag.getTagName();
		Long parentId = tag.getParentId();
		Integer isParent = tag.getIsParent();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(tagName), ErrorCode.PARAMS_ERROR, "标签名称不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(tagName)) {
			ThrowUtils.throwIf(tagName.length() > 80, ErrorCode.PARAMS_ERROR, "标签名称过长");
		}
		if (ObjectUtils.isNotEmpty(parentId)) {
			ThrowUtils.throwIf(this.getById(parentId) == null, ErrorCode.NOT_FOUND_ERROR, "父标签id输入有误,标签不存在");
		}
		if (ObjectUtils.isNotEmpty(isParent)) {
			ThrowUtils.throwIf(TagIsParentEnum.getEnumByValue(isParent) == null, ErrorCode.PARAMS_ERROR, "是否为父标签输入有误");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param tagQueryRequest tagQueryRequest
	 * @return QueryWrapper<Tag>
	 */
	@Override
	public QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest) {
		QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
		if (tagQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = tagQueryRequest.getId();
		Long notId = tagQueryRequest.getNotId();
		String tagName = tagQueryRequest.getTagName();
		Long userId = tagQueryRequest.getUserId();
		Long parentId = tagQueryRequest.getParentId();
		Integer isParent = tagQueryRequest.getIsParent();
		String sortField = tagQueryRequest.getSortField();
		String sortOrder = tagQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(tagName), "tagName", tagName);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(parentId), "parentId", parentId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(isParent), "isParent", isParent);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取标签封装
	 *
	 * @param tag     tag
	 * @param request request
	 * @return TagVO
	 */
	@Override
	public TagVO getTagVO(Tag tag, HttpServletRequest request) {
		// 对象转封装类
		TagVO tagVO = TagVO.objToVo(tag);
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = tag.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		tagVO.setUserVO(userVO);
		// endregion
		
		return tagVO;
	}
	
	/**
	 * 分页获取标签封装
	 *
	 * @param tagPage tagPage
	 * @param request request
	 * @return Page<TagVO>
	 */
	@Override
	public Page<TagVO> getTagVOPage(Page<Tag> tagPage, HttpServletRequest request) {
		List<Tag> tagList = tagPage.getRecords();
		Page<TagVO> tagVOPage = new Page<>(tagPage.getCurrent(), tagPage.getSize(), tagPage.getTotal());
		if (CollUtil.isEmpty(tagList)) {
			return tagVOPage;
		}
		// 对象列表 => 封装对象列表
		List<TagVO> tagVOList = tagList.stream().map(tag -> {
			return TagVO.objToVo(tag);
		}).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = tagList.stream().map(Tag::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 填充信息
		tagVOList.forEach(tagVO -> {
			Long userId = tagVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			tagVO.setUserVO(userService.getUserVO(user, request));
		});
		// endregion
		
		tagVOPage.setRecords(tagVOList);
		return tagVOPage;
	}
	
}

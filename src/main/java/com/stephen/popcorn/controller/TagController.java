package com.stephen.popcorn.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.annotation.AuthCheck;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.DeleteRequest;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.constant.UserConstant;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.model.dto.tag.TagAddRequest;
import com.stephen.popcorn.model.dto.tag.TagEditRequest;
import com.stephen.popcorn.model.dto.tag.TagQueryRequest;
import com.stephen.popcorn.model.dto.tag.TagUpdateRequest;
import com.stephen.popcorn.model.entity.Tag;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.TagVO;
import com.stephen.popcorn.service.TagService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.ResultUtils;
import com.stephen.popcorn.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 标签接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {
	
	@Resource
	private TagService tagService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 创建标签
	 *
	 * @param tagAddRequest tagAddRequest
	 * @param request       request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addTag(@RequestBody TagAddRequest tagAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(tagAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Tag tag = new Tag();
		BeanUtils.copyProperties(tagAddRequest, tag);
		// 数据校验
		tagService.validTag(tag, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		tag.setUserId(loginUser.getId());
		// 写入数据库
		boolean result = tagService.save(tag);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newTagId = tag.getId();
		return ResultUtils.success(newTagId);
	}
	
	/**
	 * 删除标签
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Tag oldTag = tagService.getById(id);
		ThrowUtils.throwIf(oldTag == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldTag.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = tagService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新标签（仅管理员可用）
	 *
	 * @param tagUpdateRequest tagUpdateRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateTag(@RequestBody TagUpdateRequest tagUpdateRequest) {
		if (tagUpdateRequest == null || tagUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Tag tag = new Tag();
		BeanUtils.copyProperties(tagUpdateRequest, tag);
		// 数据校验
		tagService.validTag(tag, false);
		// 判断是否存在
		long id = tagUpdateRequest.getId();
		Tag oldTag = tagService.getById(id);
		ThrowUtils.throwIf(oldTag == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = tagService.updateById(tag);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取标签（封装类）
	 *
	 * @param id id
	 * @return BaseResponse<TagVO>
	 */
	@GetMapping("/get/vo")
	public BaseResponse<TagVO> getTagVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Tag tag = tagService.getById(id);
		ThrowUtils.throwIf(tag == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(tagService.getTagVO(tag, request));
	}
	
	/**
	 * 分页获取标签列表（仅管理员可用）
	 *
	 * @param tagQueryRequest tagQueryRequest
	 * @return BaseResponse<Page < Tag>>
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<Tag>> listTagByPage(@RequestBody TagQueryRequest tagQueryRequest) {
		long current = tagQueryRequest.getCurrent();
		long size = tagQueryRequest.getPageSize();
		// 查询数据库
		Page<Tag> tagPage = tagService.page(new Page<>(current, size),
				tagService.getQueryWrapper(tagQueryRequest));
		return ResultUtils.success(tagPage);
	}
	
	/**
	 * 分页获取标签列表（封装类）
	 *
	 * @param tagQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<TagVO>> listTagVOByPage(@RequestBody TagQueryRequest tagQueryRequest,
	                                                 HttpServletRequest request) {
		long current = tagQueryRequest.getCurrent();
		long size = tagQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Tag> tagPage = tagService.page(new Page<>(current, size),
				tagService.getQueryWrapper(tagQueryRequest));
		// 获取封装类
		return ResultUtils.success(tagService.getTagVOPage(tagPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的标签列表
	 *
	 * @param tagQueryRequest tagQueryRequest
	 * @param request         request
	 * @return BaseResponse<Page < TagVO>>
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<TagVO>> listMyTagVOByPage(@RequestBody TagQueryRequest tagQueryRequest,
	                                                   HttpServletRequest request) {
		ThrowUtils.throwIf(tagQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		tagQueryRequest.setUserId(loginUser.getId());
		long current = tagQueryRequest.getCurrent();
		long size = tagQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Tag> tagPage = tagService.page(new Page<>(current, size),
				tagService.getQueryWrapper(tagQueryRequest));
		// 获取封装类
		return ResultUtils.success(tagService.getTagVOPage(tagPage, request));
	}
	
	/**
	 * 编辑标签（给用户使用）
	 *
	 * @param tagEditRequest tagEditRequest
	 * @param request        request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editTag(@RequestBody TagEditRequest tagEditRequest, HttpServletRequest request) {
		if (tagEditRequest == null || tagEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Tag tag = new Tag();
		BeanUtils.copyProperties(tagEditRequest, tag);
		// 数据校验
		tagService.validTag(tag, false);
		User loginUser = userService.getLoginUser(request);
		// 判断是否存在
		long id = tagEditRequest.getId();
		Tag oldTag = tagService.getById(id);
		ThrowUtils.throwIf(oldTag == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可编辑
		if (!oldTag.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = tagService.updateById(tag);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	// endregion
}

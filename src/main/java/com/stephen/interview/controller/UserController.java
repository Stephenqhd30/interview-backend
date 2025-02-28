package com.stephen.interview.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.interview.common.*;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.constants.SaltConstant;
import com.stephen.interview.constants.UserConstant;
import com.stephen.interview.model.dto.user.*;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.LoginUserVO;
import com.stephen.interview.model.vo.UserVO;
import com.stephen.interview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


/**
 * 用户接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	@Resource
	private UserService userService;
	
	
	// region 登录相关
	
	/**
	 * 用户注册
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return BaseResponse<Long> 注册是否成功
	 */
	@PostMapping("/register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
		// 获取请求参数
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			return null;
		}
		long result = userService.userRegister(userAccount, userPassword, checkPassword);
		return ResultUtils.success(result);
	}
	
	/**
	 * 用户登录
	 *
	 * @param userLoginRequest userLoginRequest
	 * @param request          request
	 * @return BaseResponse<LoginUserVO>
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		if (userLoginRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
		return ResultUtils.success(loginUserVO);
	}
	
	/**
	 * 用户注销
	 *
	 * @param request request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = userService.userLogout(request);
		return ResultUtils.success(result);
	}
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request request
	 * @return BaseResponse<LoginUserVO>
	 */
	@GetMapping("/get/login")
	public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
		User user = userService.getLoginUser(request);
		return ResultUtils.success(userService.getLoginUserVO(user));
	}
	
	// endregion
	
	// region 增删改查
	
	/**
	 * 创建用户
	 *
	 * @param userAddRequest userAddRequest
	 * @param request        request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userAddRequest, user);
		// 数据校验
		userService.validUser(user, true);
		// todo 填充默认值
		// 默认密码 12345678
		String encryptPassword = DigestUtils.md5DigestAsHex((SaltConstant.SALT + UserConstant.DEFAULT_PASSWORD).getBytes());
		user.setUserPassword(encryptPassword);
		// 设置一个默认的头像
		user.setUserAvatar(UserConstant.USER_AVATAR);
		// 写入数据库
		boolean result = userService.save(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newTagId = user.getId();
		return ResultUtils.success(newTagId);
	}
	
	/**
	 * 删除用户
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return /ioBaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		User oldUser = userService.getById(id);
		ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldUser.getId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = userService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新用户
	 *
	 * @param userUpdateRequest userUpdateRequest
	 * @param request           request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
	                                        HttpServletRequest request) {
		if (userUpdateRequest == null || userUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userUpdateRequest, user);
		// 数据校验
		userService.validUser(user, false);
		// 判断是否存在
		long id = userUpdateRequest.getId();
		User oldUser = userService.getById(id);
		ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户（仅管理员）
	 *
	 * @param id      用户id
	 * @param request request
	 * @return BaseResponse<User>
	 */
	@GetMapping("/get")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		return ResultUtils.success(user);
	}
	
	/**
	 * 根据 id 获取包装类
	 *
	 * @param id      用户id
	 * @param request request
	 * @return 查询得到的用户包装类
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(userService.getUserVO(user, request));
	}
	
	
	/**
	 * 分页获取用户列表（仅管理员）
	 *
	 * @param userQueryRequest userQueryRequest
	 * @param request          request
	 * @return BaseResponse<Page < User>>
	 */
	@PostMapping("/list/page")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
	                                               HttpServletRequest request) {
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		return ResultUtils.success(userPage);
	}
	
	/**
	 * 分页获取用户封装列表
	 *
	 * @param userQueryRequest 用户查询请求
	 * @param request          request
	 * @return BaseResponse<Page < UserVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
	                                                   HttpServletRequest request) {
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
		List<UserVO> userVO = userService.getUserVO(userPage.getRecords(), request);
		userVOPage.setRecords(userVO);
		return ResultUtils.success(userVOPage);
	}
	
	// endregion
	
	/**
	 * 更新个人信息
	 *
	 * @param userEditRequest userEditRequest
	 * @param request         request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update/my")
	public BaseResponse<Boolean> updateMyUser(@RequestBody UserEditRequest userEditRequest,
	                                          HttpServletRequest request) {
		if (userEditRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userEditRequest, user);
		user.setId(loginUser.getId());
		user.setEditTime(new Date());
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
}

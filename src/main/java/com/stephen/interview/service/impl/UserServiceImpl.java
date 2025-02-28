package com.stephen.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.common.ErrorCode;
import com.stephen.interview.common.ThrowUtils;
import com.stephen.interview.common.exception.BusinessException;
import com.stephen.interview.constants.CommonConstant;
import com.stephen.interview.constants.SaltConstant;
import com.stephen.interview.constants.UserConstant;
import com.stephen.interview.mapper.UserMapper;
import com.stephen.interview.model.dto.user.UserQueryRequest;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.enums.UserGenderEnum;
import com.stephen.interview.model.enums.UserRoleEnum;
import com.stephen.interview.model.vo.LoginUserVO;
import com.stephen.interview.model.vo.UserVO;
import com.stephen.interview.service.UserService;
import com.stephen.interview.utils.regex.RegexUtils;
import com.stephen.interview.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
	/**
	 * 校验数据
	 *
	 * @param user user
	 * @param add  对创建的数据进行校验
	 */
	@Override
	public void validUser(User user, boolean add) {
		ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String userAccount = user.getUserAccount();
		String userPassword = user.getUserPassword();
		String userName = user.getUserName();
		Integer userGender = user.getUserGender();
		String userProfile = user.getUserProfile();
		String userEmail = user.getUserEmail();
		String userPhone = user.getUserPhone();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "账号不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "密码不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "用户名不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(userAccount)) {
			ThrowUtils.throwIf(userAccount.length() < 4 || userAccount.length() > 20, ErrorCode.PARAMS_ERROR, "账号不能小于4位，不能多于20位");
		}
		if (StringUtils.isNotBlank(userProfile)) {
			ThrowUtils.throwIf(userProfile.length() > 50, ErrorCode.PARAMS_ERROR, "用户简介不能多余50字");
		}
		if (StringUtils.isNotBlank(userEmail)) {
			ThrowUtils.throwIf(!RegexUtils.checkEmail(userEmail), ErrorCode.PARAMS_ERROR, "用户邮箱有误");
		}
		if (StringUtils.isNotBlank(userPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkMobile(userPhone), ErrorCode.PARAMS_ERROR, "用户手机号码有误");
		}
		if (ObjectUtils.isNotEmpty(userGender)) {
			ThrowUtils.throwIf(UserGenderEnum.getEnumByValue(userGender) == null, ErrorCode.PARAMS_ERROR, "性别填写有误");
		}
	}
	
	@Override
	public long userRegister(String userAccount, String userPassword, String checkPassword) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
		}
		if (userPassword.length() < 8 || checkPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
		}
		// 密码和校验密码相同
		if (!userPassword.equals(checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
		}
		synchronized (userAccount.intern()) {
			// 账户不能重复
			QueryWrapper<User> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("userAccount", userAccount);
			long count = this.baseMapper.selectCount(queryWrapper);
			if (count > 0) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
			}
			// 2. 加密
			String encryptPassword = DigestUtils.md5DigestAsHex((SaltConstant.SALT + userPassword).getBytes());
			// 3. 插入数据
			User user = new User();
			user.setUserAccount(userAccount);
			user.setUserPassword(encryptPassword);
			// 3. 给用户分配一个默认的头像
			user.setUserAvatar(UserConstant.USER_AVATAR);
			boolean saveResult = this.save(user);
			if (!saveResult) {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
			}
			return user.getId();
		}
	}
	
	@Override
	public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
		}
		if (userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
		}
		// 2. 加密
		String encryptPassword = DigestUtils.md5DigestAsHex((SaltConstant.SALT + userPassword).getBytes());
		// 查询用户是否存在
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount", userAccount);
		queryWrapper.eq("userPassword", encryptPassword);
		User user = this.baseMapper.selectOne(queryWrapper);
		// 用户不存在
		if (user == null) {
			log.info("user login failed, userAccount cannot match userPassword");
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
		}
		// 3. 记录用户的登录态
		request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
		return this.getLoginUserVO(user);
	}
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request
	 * @return
	 */
	@Override
	public User getLoginUser(HttpServletRequest request) {
		// 先判断是否已登录
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User currentUser = (User) userObj;
		if (currentUser == null || currentUser.getId() == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		// 从数据库查询（追求性能的话可以注释，直接走缓存）
		long userId = currentUser.getId();
		currentUser = this.getById(userId);
		if (currentUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		return currentUser;
	}
	
	/**
	 * 获取当前登录用户（允许未登录）
	 *
	 * @param request
	 * @return
	 */
	@Override
	public User getLoginUserPermitNull(HttpServletRequest request) {
		// 先判断是否已登录
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User currentUser = (User) userObj;
		if (currentUser == null || currentUser.getId() == null) {
			return null;
		}
		// 从数据库查询（追求性能的话可以注释，直接走缓存）
		long userId = currentUser.getId();
		return this.getById(userId);
	}
	
	/**
	 * 是否为管理员
	 *
	 * @param request
	 * @return
	 */
	@Override
	public boolean isAdmin(HttpServletRequest request) {
		// 仅管理员可查询
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User user = (User) userObj;
		return isAdmin(user);
	}
	
	@Override
	public boolean isAdmin(User user) {
		return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
	}
	
	/**
	 * 用户注销
	 *
	 * @param request
	 */
	@Override
	public boolean userLogout(HttpServletRequest request) {
		if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
		}
		// 移除登录态
		request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
		return true;
	}
	
	@Override
	public LoginUserVO getLoginUserVO(User user) {
		if (user == null) {
			return null;
		}
		// todo 在此处将实体类和 DTO 进行转换
		LoginUserVO loginUserVO = new LoginUserVO();
		BeanUtils.copyProperties(user, loginUserVO);
		return loginUserVO;
	}
	
	@Override
	public UserVO getUserVO(User user, HttpServletRequest request) {
		// 对象转封装类
		return UserVO.objToVo(user);
	}
	
	
	@Override
	public List<UserVO> getUserVO(List<User> userList, HttpServletRequest request) {
		if (CollUtil.isEmpty(userList)) {
			return new ArrayList<>();
		}
		return userList.stream().map(user -> getUserVO(user, request)).collect(Collectors.toList());
	}
	
	@Override
	public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		Long id = userQueryRequest.getId();
		String userName = userQueryRequest.getUserName();
		String userProfile = userQueryRequest.getUserProfile();
		String userRole = userQueryRequest.getUserRole();
		Integer userGender = userQueryRequest.getUserGender();
		String sortField = userQueryRequest.getSortField();
		String sortOrder = userQueryRequest.getSortOrder();
		String userEmail = userQueryRequest.getUserEmail();
		String userPhone = userQueryRequest.getUserPhone();
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		
		// 精准查询
		queryWrapper.eq(id != null, "id", id);
		queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userGender), "userGender", userGender);
		
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
		queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
		queryWrapper.like(StringUtils.isNotBlank(userEmail), "userEmail", userEmail);
		queryWrapper.like(StringUtils.isNotBlank(userPhone), "userPhone", userPhone);
		queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
}

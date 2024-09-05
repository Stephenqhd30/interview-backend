package com.stephen.popcorn.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.model.entity.Post;
import com.stephen.popcorn.model.entity.PostFavour;
import com.stephen.popcorn.model.entity.User;

/**
 * 帖子收藏服务
 *
 * @author stephen qiu
 */
public interface PostFavourService extends IService<PostFavour> {
	
	/**
	 * 帖子收藏
	 *
	 * @param postId
	 * @param loginUser
	 * @return
	 */
	int doPostFavour(long postId, User loginUser);
	
	/**
	 * 分页获取用户收藏的帖子列表
	 *
	 * @param page
	 * @param queryWrapper
	 * @param favourUserId
	 * @return
	 */
	Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
	                                long favourUserId);
	
	/**
	 * 帖子收藏（内部服务）
	 *
	 * @param userId
	 * @param postId
	 * @return
	 */
	int doPostFavourInner(long userId, long postId);
}

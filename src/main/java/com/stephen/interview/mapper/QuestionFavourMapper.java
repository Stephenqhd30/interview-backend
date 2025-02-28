package com.stephen.interview.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author stephen qiu
* @description 针对表【question_favour(题目收藏)】的数据库操作Mapper
* @createDate 2024-09-05 12:30:50
* @Entity com.stephen.interview.model.entity.QuestionFavour
*/
public interface QuestionFavourMapper extends BaseMapper<QuestionFavour> {
	
	/**
	 * 分页查询收藏帖子列表
	 *
	 * @param page
	 * @param queryWrapper
	 * @param favourUserId
	 * @return
	 */
	Page<Question> listFavourQuestionByPage(IPage<Question> page, @Param(Constants.WRAPPER) Wrapper<Question> queryWrapper,
	                                    long favourUserId);
}





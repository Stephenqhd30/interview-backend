package com.stephen.interview.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.interview.model.dto.questionFavour.QuestionFavourQueryRequest;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionFavour;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.QuestionFavourVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 题目收藏服务
 *
 * @author stephen qiu
 */
public interface QuestionFavourService extends IService<QuestionFavour> {
    
    
    int doQuestionFavour(long questionId, User loginUser);
    
    Page<Question> listFavourQuestionByPage(IPage<Question> page, Wrapper<Question> queryWrapper, long favourUserId);
    
    @Transactional(rollbackFor = Exception.class)
    int doQuestionFavourInner(long userId, long questionId);
}
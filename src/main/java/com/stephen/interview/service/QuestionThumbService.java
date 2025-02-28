package com.stephen.interview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.interview.model.entity.QuestionThumb;
import com.stephen.interview.model.entity.User;
import com.stephen.interview.model.vo.QuestionThumbVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 题目点赞服务
 *
 * @author stephen qiu
 */
public interface QuestionThumbService extends IService<QuestionThumb> {
    
    int doQuestionThumb(long questionId, User loginUser);
    
    @Transactional(rollbackFor = Exception.class)
    int doQuestionThumbInner(long userId, long questionId);
}
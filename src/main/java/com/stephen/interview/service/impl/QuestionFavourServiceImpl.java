package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.model.entity.QuestionFavour;
import com.stephen.interview.service.QuestionFavourService;
import com.stephen.interview.mapper.QuestionFavourMapper;
import org.springframework.stereotype.Service;

/**
* @author stephen qiu
* @description 针对表【question_favour(题目收藏)】的数据库操作Service实现
* @createDate 2024-09-05 12:30:50
*/
@Service
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour>
    implements QuestionFavourService{

}





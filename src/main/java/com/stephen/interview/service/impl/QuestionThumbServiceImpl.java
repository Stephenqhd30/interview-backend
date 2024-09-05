package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.model.entity.QuestionThumb;
import com.stephen.interview.service.QuestionThumbService;
import com.stephen.interview.mapper.QuestionThumbMapper;
import org.springframework.stereotype.Service;

/**
* @author stephen qiu
* @description 针对表【question_thumb(帖子点赞)】的数据库操作Service实现
* @createDate 2024-09-05 12:30:53
*/
@Service
public class QuestionThumbServiceImpl extends ServiceImpl<QuestionThumbMapper, QuestionThumb>
    implements QuestionThumbService{

}





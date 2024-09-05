package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.model.entity.Question;
import com.stephen.interview.mapper.QuestionMapper;
import com.stephen.interview.service.QuestionService;
import org.springframework.stereotype.Service;

/**
* @author stephen qiu
* @description 针对表【question(题目表)】的数据库操作Service实现
* @createDate 2024-09-05 12:27:45
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService {

}





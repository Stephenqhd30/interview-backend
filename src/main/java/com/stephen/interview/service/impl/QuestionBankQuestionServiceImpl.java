package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.model.entity.QuestionBankQuestion;
import com.stephen.interview.service.QuestionBankQuestionService;
import com.stephen.interview.mapper.QuestionBankQuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author stephen qiu
* @description 针对表【question_bank_question(题库题目表（硬删除）)】的数据库操作Service实现
* @createDate 2024-09-05 12:30:47
*/
@Service
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
    implements QuestionBankQuestionService{

}





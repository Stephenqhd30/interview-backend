package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.model.entity.QuestionBank;
import com.stephen.interview.service.QuestionBankService;
import com.stephen.interview.mapper.QuestionBankMapper;
import org.springframework.stereotype.Service;

/**
* @author stephen qiu
* @description 针对表【question_bank(题库表)】的数据库操作Service实现
* @createDate 2024-09-05 12:30:44
*/
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
    implements QuestionBankService{

}





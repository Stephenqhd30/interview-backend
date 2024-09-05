package com.stephen.interview.mapper;

import com.stephen.interview.model.entity.Question;
import com.stephen.interview.model.entity.QuestionBankQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author stephen qiu
* @description 针对表【question_bank_question(题库题目表（硬删除）)】的数据库操作Mapper
* @createDate 2024-09-05 12:30:47
* @Entity com.stephen.interview.model.entity.QuestionBankQuestion
*/
public interface QuestionBankQuestionMapper extends BaseMapper<QuestionBankQuestion> {
	/**
	 * 根据题库id查询题目列表
	 */
	List<Question> getQuestionListByQuestionId(Long questionBankId);
}





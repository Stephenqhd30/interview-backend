package com.stephen.interview.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题库题目关系请求
 *
 * @author stephen qiu
 */
@Data
public class QuestionBankQuestionAddRequest implements Serializable {
    
    /**
     * 题库id
     */
    private Long questionBankId;
    
    /**
     * 题目id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
package com.stephen.interview.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新题库题目关系请求
 *
 * @author stephen qiu
 */
@Data
public class QuestionBankQuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    
    /**
     * 题库id
     */
    private Long questionBankId;
    
    /**
     * 题目id
     */
    private Long questionId;
    
    /**
     * 创建人id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
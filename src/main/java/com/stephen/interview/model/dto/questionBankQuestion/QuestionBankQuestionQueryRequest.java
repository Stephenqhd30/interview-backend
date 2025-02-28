package com.stephen.interview.model.dto.questionBankQuestion;

import com.stephen.interview.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询题库题目关系请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;
    
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
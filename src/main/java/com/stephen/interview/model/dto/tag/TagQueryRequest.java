package com.stephen.interview.model.dto.tag;

import com.stephen.interview.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询标签请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TagQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 用户id
     */
    private Long userId;
    
    /**
     * 父标签id
     */
    private Long parentId;
    
    /**
     * 0-不是父标签，1-是父标签
     */
    private Integer isParent;

    private static final long serialVersionUID = 1L;
}
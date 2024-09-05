package com.stephen.interview.model.dto.tag;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建标签请求
 *
 * @author stephen qiu
 */
@Data
public class TagAddRequest implements Serializable {
    
    /**
     * 标签名称
     */
    private String tagName;
    
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
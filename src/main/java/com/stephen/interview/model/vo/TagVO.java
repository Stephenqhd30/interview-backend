package com.stephen.interview.model.vo;

import com.stephen.interview.model.entity.Tag;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签视图
 *
 * @author stephen
 */
@Data
public class TagVO implements Serializable {
	
	private static final long serialVersionUID = -9105082977368215695L;
	/**
	 * id
	 */
	private Long id;
	
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
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 创建用户信息
	 */
	private UserVO userVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param tagVO tagVO
	 * @return Tag
	 */
	public static Tag voToObj(TagVO tagVO) {
		if (tagVO == null) {
			return null;
		}
		Tag tag = new Tag();
		BeanUtils.copyProperties(tagVO, tag);
		return tag;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param tag tag
	 * @return TagVO
	 */
	public static TagVO objToVo(Tag tag) {
		if (tag == null) {
			return null;
		}
		TagVO tagVO = new TagVO();
		BeanUtils.copyProperties(tag, tagVO);
		return tagVO;
	}
}

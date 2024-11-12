package com.stephen.trajectory.model.dto.chart;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建图表信息请求
 *
 * @author stephen qiu
 */
@Data
public class ChartAddRequest implements Serializable {
	
	/**
	 * 分析目标
	 */
	private String goal;
	
	/**
	 * 图表名称
	 */
	private String name;
	
	/**
	 * 图表数据
	 */
	private String chartData;
	
	/**
	 * 图表类型
	 */
	private String chartType;
	
	/**
	 * 标签列表(JSON数组)
	 */
	private List<String> tags;
	
	
	private static final long serialVersionUID = 1L;
}
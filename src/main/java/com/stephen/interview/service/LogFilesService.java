package com.stephen.interview.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.interview.model.entity.LogFiles;
import com.stephen.interview.model.enums.FileUploadBizEnum;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author stephenqiu
 * @description 针对表【log_files(文件上传日志记录表)】的数据库操作Service
 * @createDate 2025-02-17 17:55:33
 */
public interface LogFilesService extends IService<LogFiles> {
	
	/**
	 * 校验文件
	 *
	 * @param multipartFile     multipartFile
	 * @param fileUploadBizEnum 业务类型
	 */
	void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum);
	
}

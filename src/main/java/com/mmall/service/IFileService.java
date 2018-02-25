package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zhengb on 2018-02-04.
 * 文件上传服务
 */
public interface IFileService {
    /**
     * 上传文件到ftp服务器
     * @param file
     * @param path 待上传文件路径
     * @return
     */
    String uploadToFtpServer(MultipartFile file, String path);
}

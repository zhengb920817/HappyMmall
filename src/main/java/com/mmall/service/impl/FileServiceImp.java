package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by zhengb on 2018-02-04.
 */
@Service("iFileService")
public class FileServiceImp implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileServiceImp.class);

    public String uploadToFtpServer(MultipartFile file, String path){
        String originFile = file.getOriginalFilename();

        String fileExtension = originFile.substring(originFile.lastIndexOf('.') + 1);

        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtension;
        logger.info("开始上传文件，上传文件的文件名：{},上传的路径{},新文件名：{}" ,originFile, path, uploadFileName);

        File saveDir = new File(path);
        if(!saveDir.exists()){
            saveDir.setWritable(true);
            saveDir.mkdirs();
        }
        File targetFile = new File(saveDir, uploadFileName);
        try {
            file.transferTo(targetFile);
            //将targetFile上传到FTP服务器上
            FtpUtil.uploadFiles(Lists.newArrayList(targetFile));
            //上传完成后删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常，原始文件名{}，目标文件路径{}", originFile, targetFile.getAbsolutePath());
            return null;
        }

        return targetFile.getName();
    }
}

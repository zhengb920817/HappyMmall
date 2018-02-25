package com.mmall.util;

import java.io.File;
import java.util.List;

/**
 * Created by zhengb on 2018-02-04.
 */
public class FtpUtil {

    private static String FTPServerIP = PropertiesUtils.getPropertyValue("ftp.server.ip");
    private static String FTPUserName = PropertiesUtils.getPropertyValue("ftp.user");
    private static String FTPPassword = PropertiesUtils.getPropertyValue("ftp.pass");
    private static int FTPPort = 21;
    private static final String remotePath = "image";

    public static boolean uploadFiles(List<File> fileList) {
        FTPFileTransfer ftpFileTransfer = new FTPFileTransfer(FTPServerIP, FTPPort, FTPUserName, FTPPassword);
        return ftpFileTransfer.upLoadFile(remotePath, fileList);
    }
}

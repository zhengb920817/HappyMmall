package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Created by zhengb on 2018-02-04.
 */
public class FTPFileTransfer {
    private Logger logger = LoggerFactory.getLogger(FTPFileTransfer.class);

    private static final int bufferSize = 1024;
    private static final String fileEncoding = "UTF-8";

    private FTPClient ftpClient;
    private String ip;
    private int port;
    private String userName;
    private String password;

    public FTPFileTransfer(String ip, int port, String userName, String password){
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    private boolean connectToServer(String ip, int prort, String username, String password) {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)) {
                logger.error("链接ftp服务器失败，返回代码" + replyCode);
                ftpClient.disconnect();
                return false;
            }
            return true;
        } catch (IOException e) {
            logger.error("链接ftp服务器异常", e);
            return false;
        }
    }

    public boolean upLoadFile(String remotePath, List<File> fileList){
        return doUploadFile(remotePath, fileList);
    }

    private boolean doUploadFile(String remotePath, List<File> fileList) {
        boolean uploaded = false;
        FileInputStream fis = null;
        if (connectToServer(this.ip, this.port, this.userName, this.password)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(bufferSize);
                ftpClient.setControlEncoding(fileEncoding);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("上传文件异常：" + e);
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error("释放上传文件流和ftpClient异常", e);
                }
            }
        }

        return uploaded;
    }

    public boolean downloadFile(String remotePath, String fileName, String saveFilePath) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(new File(saveFilePath));
        try {
            return doDownloadFile(remotePath, fileName, fos);
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
               logger.error("下载ftp文件关闭输出流异常", e);
            }
        }
    }

    private boolean doDownloadFile(String remotePath, String fileName, FileOutputStream saveStream) {
        if (connectToServer(this.ip, this.port, this.userName, this.password)) {
            try {
                try {
                    ftpClient.changeWorkingDirectory(remotePath);
                    ftpClient.setReceiveBufferSize(bufferSize);
                    ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
                    ftpClient.setControlEncoding(fileEncoding);
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    FTPFile[] files = ftpClient.listFiles();
                    for (FTPFile ftpFile : files) {
                        if(ftpFile.getName().equals(fileName)) {
                            ftpClient.retrieveFile(fileName, saveStream);
                            break;
                        }
                    }
                    ftpClient.logout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

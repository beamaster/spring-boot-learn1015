package com.steam.springbootlearn.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String url = "45.40.193.253";
    private static Integer port = 1006;
    private static String username = "kai2024";
    private static String password = "ftp@kai2024";


    public static Boolean uploadFile(String pathName, String fileName, InputStream inputStream) {
        FTPClient client = new FTPClient();
        Boolean flag = false;
        try {
            client.connect(url, port);
            client.login(username, password);
            int replyCode = client.getReplyCode();
            if (FTPReply.isPositiveCompletion(replyCode)) {
                logger.info("connect successful...." + url + ":" + port);
                client.makeDirectory(pathName);
                client.changeWorkingDirectory(pathName);
                client.storeFile(fileName, inputStream);
                inputStream.close();
                client.logout();
                flag = true;
            } else {
                logger.info("connect failure...." + url + ":" + port);
            }
        } catch (IOException ioe) {
            logger.info("upload...IOException..." + ioe);
        } finally {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (IOException ioe) {
                    logger.info("finally...IOException..." + ioe);
                }
            }
        }
        return flag;
    }

    /**
     * 从指定路径上传指定后缀文件到指定目录
     *
     * @param fromPath
     * @param toPath
     * @param suffix   .DT .txt .xls...
     * @return
     */
    public static Boolean uploadSpecifiedSuffixFile(String fromPath, String toPath, String suffix) {
        Boolean flag = false;
        File file = new File(fromPath);
        File[] files = file.listFiles();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File processFile = files[i];
                File tempFile = new File(fromPath, processFile.getName() + ".tmp");
                if (!tempFile.exists()) {
                    //空文件不上传
                    if (processFile.length() > 0 && processFile.getName().endsWith(suffix)) {
                        try {
                            new FileOutputStream(tempFile);
                            flag = uploadFile(toPath, processFile.getName(), new FileInputStream(processFile));
                            if (flag) {
                                logger.info(flag + "...upload...successful..." + processFile.getName());
                            } else {
                                logger.info(flag + "...upload...failure..." + processFile.getName());
                            }
                        } catch (FileNotFoundException fnfe) {
                            logger.info("upload...FileNotFoundException..." + fnfe);
                        }
                    }
                }
            }
        }
        return flag;
    }
}

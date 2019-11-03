package com.yin.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile) {
        String imageUrl ="http://192.168.1.237";

        String f = PmsUploadUtil.class.getResource("/tracker.conf").getPath();

        TrackerServer trackerServer = null;
        try {
            ClientGlobal.init(f);
            TrackerClient trackerClient=new TrackerClient();
            trackerServer=trackerClient.getConnection();
            trackerServer = trackerClient.getConnection();
        }catch (Exception e) {
            e.printStackTrace();
        }

        StorageClient storageClient=new StorageClient(trackerServer,null);

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            int i = originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(i+1);
            String[] upload_file = storageClient.upload_file(multipartFile.getBytes(), "png", null);
            for (String s : upload_file) {
                imageUrl+="/"+ s;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrl;
    }
}

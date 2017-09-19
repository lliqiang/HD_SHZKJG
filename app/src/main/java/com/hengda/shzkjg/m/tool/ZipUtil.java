package com.hengda.shzkjg.m.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {


    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFilePath    name of ZIP
     * @param unzipToDirPath path to be unZIP
     * @throws Exception
     */
    public static void unZipFolder(String zipFilePath, String unzipToDirPath) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry;
        String szName;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            //是文件夹路径方式
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(unzipToDirPath + File.separator + szName);
                folder.mkdirs();
            } else {
                //是文件路径的话
                File file = new File(unzipToDirPath + File.separator + szName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
    /**
     * 解压
     *
     * @param zipFilePath
     * @param unzipToDirPath
     * @param callback
     * @throws Exception
     */
    public static void unzipFolder(String zipFilePath, String unzipToDirPath,
                                   IUnzipCallback callback) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry;
        String szName;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(unzipToDirPath + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(unzipToDirPath + File.separator + szName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        callback.completed();
        inZip.close();
    }
    public interface IUnzipCallback {
        void completed();
    }

    public static void unzipFolder(File zipFile, String unzipToDirPath,
                                   IUnzipCallback callback) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry;
        String szName;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(unzipToDirPath + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(unzipToDirPath + File.separator + szName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        callback.completed();
        zipFile.delete();
        inZip.close();
    }
}

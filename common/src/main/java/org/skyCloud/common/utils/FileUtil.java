package org.skyCloud.common.utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.skyCloud.common.constants.FileConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yq on 2017/02/07 14:42.
 * 依赖 commons-io commons-compress
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static final int BUFFER_SIZE = 4096;

    private FileUtil() {
    }

    /**
     * 复制文件到指定文件夹
     * @param resFilePath 可为文件或文件夹
     * @param distFolder
     * @throws IOException
     */
    public static void copyFile(String resFilePath, String distFolder) throws IOException {
        File resFile = new File(resFilePath);
        File distFile = new File(distFolder);
        if(resFile.isDirectory()) { // 目录时
            FileUtils.copyDirectoryToDirectory(resFile, distFile);
        }else if (resFile.isFile()) { // 文件时
            FileUtils.copyFileToDirectory(resFile, distFile);
//            FileUtils.copyFileToDirectory(resFile, distFile,true);//复制文件日期与原件一致(默认)
        }else {
            if(logger.isDebugEnabled()){
                logger.debug("文件不存在!");
            }
        }
    }

    /**
     * 删除一个文件或者目录
     * @param targetPath    文件或者目录路径
     * @IOException 当操作发生异常时抛出
     */
    public static void deleteFile(String targetPath) throws IOException {
        File targetFile = new File(targetPath);
        if (targetFile.isDirectory()) {
            FileUtils.deleteDirectory(targetFile);
        } else if (targetFile.isFile()) {
            targetFile.delete();
        }else {
            if(logger.isDebugEnabled()){
                logger.debug("文件不存在!");
            }
        }
    }

    /**
     * 把文件压缩成zip格式
     * @param fileNames 需要压缩的文件
     * @param extensions 指定扩展名 ,若全部则填null
     * @param zipFilePath 压缩后的zip文件路径 ,如"D:/test/aa.zip";
     */
    public static void compressFiles2Zip(List<String> fileNames,String[] extensions,String zipFilePath) throws Exception {
        if(fileNames != null && fileNames.size() >0) {
            if(isEndsWithZip(zipFilePath)) {
                File zipFile = new File(zipFilePath);
                ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(zipFile);
                //Use Zip64 extensions for all entries where they are required
                zaos.setUseZip64(Zip64Mode.AsNeeded);

                //再用ZipArchiveOutputStream写到压缩文件中
                ZipArchiveEntry zipArchiveEntry ;
                InputStream is = null;
                byte[] buffer = new byte[1024 * 5];
                int len ;
                List<File> files = getFiles(fileNames,extensions);
                for(File file : files) {
                    if(file.exists()) {
                        //用ZipArchiveEntry封装每个文件
                        zipArchiveEntry = new ZipArchiveEntry(file,file.getName());
                        zaos.putArchiveEntry(zipArchiveEntry);
                        is = new FileInputStream(file);
                        while((len = is.read(buffer)) != -1) {
                            //把缓冲区的字节写入到ZipArchiveEntry
                            zaos.write(buffer, 0, len);
                        }
                        //Writes all necessary data for this entry.
                        zaos.closeArchiveEntry();
                    }
                }
                zaos.finish();
                if(is != null) {
                    is.close();
                }
                zaos.close();
            }

        }

    }

    /**
     * 把zip文件解压到指定的文件夹 图片解压会失真
     * @param zipFilePath zip文件路径, 如 "D:/test/aa.zip"
     * @param saveFileDir 解压后的文件存放路径, 如"D:/test/"
     */
    public static void decompressZip(String zipFilePath,String saveFileDir) {
        if(isEndsWithZip(zipFilePath)) {
            File file = new File(zipFilePath);
            if(file.exists()) {
                InputStream is = null;
                //can read Zip archives
                ZipArchiveInputStream zais = null;
                try {
                    is = new FileInputStream(file);
                    zais = new ZipArchiveInputStream(is);
                    ArchiveEntry archiveEntry = null;
                    //把zip包中的每个文件读取出来
                    //然后把文件写到指定的文件夹
                    while((archiveEntry = zais.getNextEntry()) != null) {
                        //获取文件名
                        String entryFileName = archiveEntry.getName();
                        //构造解压出来的文件存放路径
                        String entryFilePath = saveFileDir + entryFileName;
                        byte[] content = new byte[(int) archiveEntry.getSize()];
                        zais.read(content);
                        OutputStream os = null;
                        try {
                            //把解压出来的文件写到指定路径
                            File entryFile = new File(entryFilePath);
                            os = new FileOutputStream(entryFile);
                            os.write(content);
                        }catch(IOException e) {
                            throw new IOException(e);
                        }finally {
                            if(os != null) {
                                os.flush();
                                os.close();
                            }
                        }

                    }
                }catch(Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    try {
                        if(zais != null) {
                            zais.close();
                        }
                        if(is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 判断文件名是否以.zip为后缀
     * @param fileName 需要判断的文件名
     * @return 是zip文件返回true,否则返回false
     */
    public static boolean isEndsWithZip(String fileName) {
        return isThisExtension(fileName, FileConsts.ZIP);
    }

    /**
     * 判断文件名为指定类型
     * @param fileName 需要判断的文件名
     * @return 是zip文件返回true,否则返回false
     */
    public static boolean isThisExtension(String fileName,String extension) {
        return (!StringUtils.isEmpty(fileName)) && FilenameUtils.getExtension(fileName).equalsIgnoreCase(extension);
    }

    /**
     * 递归取所有文件
     * @param fileNames
     * @param extensions 指定扩展名 数组
     * @return
     */
    public static List<File> getFiles(List<String> fileNames ,String[] extensions){
        if(fileNames != null && fileNames.size() >0) {
            File file ;
            List<File> list = new ArrayList<>();
            List<String> es = Arrays.asList(extensions);
            for (String name : fileNames){
                file = new File(name);
                if(file.exists()){
                    if(file.isFile()){
                        //过滤扩展名不符的文件
                        if(es.contains(FilenameUtils.getExtension(name))){
                            list.add(file);
                        }
                    }else if(file.isDirectory()){
                        list.addAll(FileUtils.listFiles(file,extensions,true));
                    }
                }
            }
            return list ;
        }
        return null;
    }

    /**
     * 复制流
     * @param in 输入流
     * @param out 输出流
     * @return  数据大小 返回-1 代表参数 输入 输出流存在null值
     */
    public static int copyStream(InputStream in, OutputStream out){
        int errorInt = - 1 ;
        if(in != null && out != null){
            try {
                int byteCount = 0;
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    byteCount += bytesRead;
                }
                out.flush();
                logger.info("byteCount : {}" ,byteCount);
                return byteCount;
            }catch (IOException e){
                logger.error("copyStream {}",e.getMessage());
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    logger.error("copyStream {}",ex.getMessage());
                }
                try {
                    out.close();
                } catch (IOException ex) {
                    logger.error("copyStream {}",ex.getMessage());
                }
            }
            return  errorInt;
        }else {
            return  errorInt;
        }
    }

}

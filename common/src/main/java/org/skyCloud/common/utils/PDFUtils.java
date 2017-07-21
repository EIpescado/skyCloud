package org.skyCloud.common.utils;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.io.FilenameUtils;
import org.skyCloud.common.constants.FileConsts;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yq on 2017/02/17 14:03.
 * pdf工具类
 */
public class PDFUtils {

    private PDFUtils() {
    }

    /**
     * pdf转图片 对pdf字体有要求 否则字体会变形
     * BaseFont.createFont("C:/windows/fonts/simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
     * @param path
     * @param imgPath
     * @throws Exception
     */
    public static void pdf2Img(String path,String imgPath) throws Exception{
        org.icepdf.core.pobjects.Document doc = new org.icepdf.core.pobjects.Document();
        doc.setFile(path);
        int pageNum = doc.getNumberOfPages() ;
        String fileName ;
        for (int i = 0; i < pageNum; i++) {
            BufferedImage img = (BufferedImage) doc.getPageImage(i, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0, 1F);
            fileName = FilenameUtils.getFullPath(imgPath) + "第" +  (i + 1) + "页-" +  FilenameUtils.getName(imgPath);
            ImageIO.write(img, FileConsts.JPG, new File(fileName));
        }
    }

    /**
     * 图片转pdf
     * @param imgFilePath
     * @param pdfFilePath
     * @return
     * @throws IOException
     */
    public static boolean img2Pdf(String imgFilePath, String pdfFilePath)throws IOException {
        File file=new File(imgFilePath);
        if(file.exists()){
            Document document = new Document();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pdfFilePath);
                PdfWriter.getInstance(document, fos);
                document.setPageSize(PageSize.A4);
                document.open();
                Image image = Image.getInstance(imgFilePath);
                image.scalePercent(100F); //不缩放
                image.setAlignment(Image.ALIGN_CENTER);
                document.add(image);
            } catch (Exception de) {
                System.out.println(de.getMessage());
            }
            if(document != null && document.isOpen()){
                document.close();
            }
            fos.flush();
            fos.close();
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
//        pdf2Img("d:/A/221.pdf","d:/A/221.jpg");
        img2Pdf("d:/A/221.jpg","d:/A/x.pdf");
    }

}

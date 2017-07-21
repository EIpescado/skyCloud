package org.skyCloud.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yq on 2017年5月4日 14:25:27
 * 验证码工具类
 */
public class VerifyCodeUtils {

    //使用到Algerian字体，系统里没有的话需要安装字体，字体只显示大写，去掉了1,0,i,o几个容易混淆的字符
    public static final char [] VERIFY_CODE_ARRAY = new char[]{
        '2','3','4','5','6','7','8','9',
            'A','B','C','D','E','F','G','H',
                'J','K','L','M','N','P','Q','R',
                    'S','T','U','V','W','X','Y','Z'
    };

    private static ThreadLocalRandom random = ThreadLocalRandom.current();

    //验证码字体
    private static final String FONT_NAME = "Algerian";

    //验证码生成图片类型
    private static final String IMAGE_TYPE = "jpg";

    private VerifyCodeUtils() {
    }

    /**
     * 使用系统默认字符源生成验证码
     * @param verifySize  验证码长度
     */
    public static String  generateVerifyCode(int verifySize){
        return generateVerifyCode(verifySize, VERIFY_CODE_ARRAY);
    }

    /**
     * 使用指定源生成验证码
     * @param verifySize  验证码长度
     * @param source   验证码字符源
     */
    public static String generateVerifyCode(int verifySize, char [] source){
        if(source == null || source.length == 0){
            source = VERIFY_CODE_ARRAY;
        }
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for(int i = 0; i < verifySize; i++){
            verifyCode.append(source[rand.nextInt(source.length - 1 )]);
        }
        return verifyCode.toString();
    }

    /**
     * 输出随机验证码图片流,并返回验证码值
     * @param width 宽像素
     * @param height 高像素
     * @param outputStream 输出流
     * @param verifySize 验证码长度
     * @return 验证码值
     * @throws IOException
     */
    public static String outputVerifyImage(int width, int height, OutputStream outputStream, int verifySize) throws IOException {
        String verifyCode = generateVerifyCode(verifySize);
        outputImage(width, height, outputStream, verifyCode);
        return verifyCode;
    }

    /**
     * 输出指定验证码图片流
     * @param width 宽像素
     * @param height 高像素
     * @param outputStream 输出流
     * @param code 验证码
     * @throws IOException
     */
    public static void outputImage(int width, int height, OutputStream outputStream, String code) throws IOException {
        int verifySize = code.length();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[] { Color.WHITE, Color.CYAN,
                Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
                Color.PINK, Color.YELLOW };
        float[] fractions = new float[colors.length];
        for(int i = 0; i < colors.length; i++){
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);

        g2.setColor(Color.GRAY);// 设置边框色
        g2.fillRect(0, 0, width, height);

        Color c = getRandColor(200, 250);
        g2.setColor(c);// 设置背景色
        g2.fillRect(0, 2, width, height-4);

        //绘制干扰线
        Random random = new Random();
        g2.setColor(getRandColor(160, 200));// 设置线条的颜色
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        // 添加噪点
        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        shear(g2, width, height, c);// 使图片扭曲

        g2.setColor(getRandColor(100, 160));
        int fontSize = height-4;
        Font font = new Font(FONT_NAME, Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = code.toCharArray();
        for(int i = 0; i < verifySize; i++){
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), (width / verifySize) * i + fontSize/2, height/2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((width-10) / verifySize) * i + 5, height/2 + fontSize/2 - 10);
        }

        g2.dispose();
        ImageIO.write(image,IMAGE_TYPE, outputStream);
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }

    public static void main(String[] args) throws Exception {
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        VerifyCodeUtils.outputImage(200,60,new FileOutputStream(new File("d:/A/x.jpg")), verifyCode);
    }
}

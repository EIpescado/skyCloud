package org.skyCloud.common.utils;


import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * 支持脚本文件的按键控制程序
 */
public class RobotUtils {

    public static Robot ROBOT ;
    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void move(int x,int y){
        ROBOT.mouseMove(x,y);
    }

    public static  void pressKey(String k){
        //获得按键种类
        char c = k.toUpperCase().charAt(0);
        //按键
        ROBOT.keyPress(c);
        //释放
        ROBOT.keyRelease(c);
        ROBOT.setAutoDelay(100);
    }

    public static  void pressKeyNotRelease(String k){
        //获得按键种类
        char c = k.toUpperCase().charAt(0);
        //按键
        ROBOT.keyPress(c);
    }

    public static  void delay(int time){
        ROBOT.setAutoDelay(time);
    }

    public static  void  mouseLeft(){
        ROBOT.mousePress(InputEvent.BUTTON1_MASK);
        ROBOT.mouseRelease(InputEvent.BUTTON1_MASK);//左键
    }

    public static  void  back(){
        ROBOT.keyPress(KeyEvent.VK_BACK_SPACE);
        ROBOT.keyRelease(KeyEvent.VK_BACK_SPACE);
    }

    public static  void  enter(){
        ROBOT.keyPress(KeyEvent.VK_ENTER);
    }

    public static  void  ctrl(){
        ROBOT.keyPress(KeyEvent.VK_CONTROL);
    }

    public static  void  releaseCtrl(){
        ROBOT.keyRelease(KeyEvent.VK_CONTROL);
    }

    public static void main(String[] args) throws Exception{
        int i = 30 ;
        while(i > 0 ){
            ctrl();
            pressKey("V");
            releaseCtrl();
            move(1056,669);
            mouseLeft();
            delay(550);
            i-- ;
        }
    }
}
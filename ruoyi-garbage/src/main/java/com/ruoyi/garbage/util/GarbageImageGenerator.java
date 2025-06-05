package com.ruoyi.garbage.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ruoyi.common.config.RuoYiConfig;

/**
 * 垃圾分类图片生成工具
 * 
 * @author ruoyi
 */
@Component
public class GarbageImageGenerator implements CommandLineRunner {

    @Value("${ruoyi.profile}")
    private String profilePath;
    
    @Override
    public void run(String... args) throws Exception {
        generateDefaultImages();
    }
    
    /**
     * 生成默认图片
     */
    public void generateDefaultImages() {
        try {
            // 创建垃圾分类图片目录
            String garbageImagePath = profilePath + "/garbage";
            File garbageImageDir = new File(garbageImagePath);
            if (!garbageImageDir.exists()) {
                garbageImageDir.mkdirs();
            }
            
            // 生成各类垃圾的默认图片
            generateImage(garbageImagePath + "/paper.png", "纸类垃圾", new Color(65, 105, 225));
            generateImage(garbageImagePath + "/plastic.png", "塑料垃圾", new Color(30, 144, 255));
            generateImage(garbageImagePath + "/metal.png", "金属垃圾", new Color(70, 130, 180));
            generateImage(garbageImagePath + "/glass.png", "玻璃垃圾", new Color(0, 191, 255));
            generateImage(garbageImagePath + "/textile.png", "纺织物垃圾", new Color(135, 206, 235));
            
            generateImage(garbageImagePath + "/battery.png", "电池", new Color(220, 20, 60));
            generateImage(garbageImagePath + "/fluorescent.png", "荧光灯", new Color(255, 0, 0));
            generateImage(garbageImagePath + "/medicine.png", "药品", new Color(178, 34, 34));
            generateImage(garbageImagePath + "/paint.png", "油漆", new Color(139, 0, 0));
            generateImage(garbageImagePath + "/hazardous.png", "有害垃圾", new Color(165, 42, 42));
            
            generateImage(garbageImagePath + "/leftover.png", "剩饭剩菜", new Color(50, 205, 50));
            generateImage(garbageImagePath + "/fruit_peel.png", "果皮", new Color(0, 128, 0));
            generateImage(garbageImagePath + "/vegetable.png", "菜叶", new Color(34, 139, 34));
            generateImage(garbageImagePath + "/bone.png", "骨头", new Color(0, 100, 0));
            generateImage(garbageImagePath + "/offal.png", "内脏", new Color(46, 139, 87));
            generateImage(garbageImagePath + "/eggshell.png", "蛋壳", new Color(60, 179, 113));
            generateImage(garbageImagePath + "/kitchen.png", "厨余垃圾", new Color(32, 178, 170));
            
            generateImage(garbageImagePath + "/tissue.png", "纸巾", new Color(128, 128, 128));
            generateImage(garbageImagePath + "/toilet_paper.png", "卫生纸", new Color(169, 169, 169));
            generateImage(garbageImagePath + "/diaper.png", "尿不湿", new Color(192, 192, 192));
            generateImage(garbageImagePath + "/cigarette.png", "烟头", new Color(105, 105, 105));
            generateImage(garbageImagePath + "/ceramic.png", "陶瓷", new Color(119, 136, 153));
            generateImage(garbageImagePath + "/other.png", "其他垃圾", new Color(112, 128, 144));
            
            System.out.println("垃圾分类默认图片生成完成");
        } catch (Exception e) {
            System.err.println("生成垃圾分类默认图片出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 生成图片
     * 
     * @param filePath 图片路径
     * @param text 图片文字
     * @param bgColor 背景颜色
     * @throws IOException IO异常
     */
    private void generateImage(String filePath, String text, Color bgColor) throws IOException {
        // 检查文件是否已存在
        File file = new File(filePath);
        if (file.exists()) {
            return; // 如果文件已存在，则跳过生成
        }
        
        // 创建图片
        int width = 200;
        int height = 200;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // 获取Graphics2D对象
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 设置背景色
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);
        
        // 设置文字
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
        
        // 计算文字位置
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textHeight = g2d.getFontMetrics().getHeight();
        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2;
        
        // 绘制文字
        g2d.drawString(text, x, y);
        
        // 释放资源
        g2d.dispose();
        
        // 保存图片
        ImageIO.write(image, "png", file);
    }

    /**
     * 为特定垃圾项目生成图片
     * 
     * @param itemName 垃圾名称
     * @param category 垃圾类别
     * @return 图片路径
     */
    public String generateImageForItem(String itemName, String category) {
        // 根据类别选择对应的图片
        String imagePath;
        switch (category) {
            case "recyclable":
                // 根据可回收物类型选择图片
                if (itemName.contains("纸") || itemName.contains("报纸") || itemName.contains("书") || itemName.contains("纸板")) {
                    imagePath = "/profile/garbage/paper.png";
                } else if (itemName.contains("塑料") || itemName.contains("瓶")) {
                    imagePath = "/profile/garbage/plastic.png";
                } else if (itemName.contains("玻璃")) {
                    imagePath = "/profile/garbage/glass.png";
                } else if (itemName.contains("金属") || itemName.contains("易拉罐") || itemName.contains("铁") || itemName.contains("钉") || itemName.contains("餐具")) {
                    imagePath = "/profile/garbage/metal.png";
                } else if (itemName.contains("衣") || itemName.contains("布") || itemName.contains("纺织")) {
                    imagePath = "/profile/garbage/textile.png";
                } else {
                    imagePath = "/profile/garbage/recyclable.png";
                }
                break;
            case "hazardous":
                // 根据有害垃圾类型选择图片
                if (itemName.contains("电池") || itemName.contains("充电")) {
                    imagePath = "/profile/garbage/battery.png";
                } else if (itemName.contains("药") || itemName.contains("药品") || itemName.contains("药片")) {
                    imagePath = "/profile/garbage/medicine.png";
                } else if (itemName.contains("灯") || itemName.contains("荧光")) {
                    imagePath = "/profile/garbage/fluorescent.png";
                } else if (itemName.contains("油漆") || itemName.contains("颜料")) {
                    imagePath = "/profile/garbage/paint.png";
                } else {
                    imagePath = "/profile/garbage/hazardous.png";
                }
                break;
            case "kitchen":
                // 根据厨余垃圾类型选择图片
                if (itemName.contains("果皮") || itemName.contains("果核") || itemName.contains("水果")) {
                    imagePath = "/profile/garbage/fruit_peel.png";
                } else if (itemName.contains("菜") || itemName.contains("蔬菜")) {
                    imagePath = "/profile/garbage/vegetable.png";
                } else if (itemName.contains("骨") || itemName.contains("鱼刺")) {
                    imagePath = "/profile/garbage/bone.png";
                } else if (itemName.contains("蛋壳")) {
                    imagePath = "/profile/garbage/eggshell.png";
                } else if (itemName.contains("剩饭") || itemName.contains("剩菜") || itemName.contains("饭菜")) {
                    imagePath = "/profile/garbage/leftover.png";
                } else if (itemName.contains("内脏") || itemName.contains("动物")) {
                    imagePath = "/profile/garbage/offal.png";
                } else {
                    imagePath = "/profile/garbage/kitchen.png";
                }
                break;
            case "other":
                // 根据其他垃圾类型选择图片
                if (itemName.contains("纸巾") || itemName.contains("餐巾纸")) {
                    imagePath = "/profile/garbage/tissue.png";
                } else if (itemName.contains("尿不湿") || itemName.contains("尿布")) {
                    imagePath = "/profile/garbage/diaper.png";
                } else if (itemName.contains("烟头")) {
                    imagePath = "/profile/garbage/cigarette.png";
                } else if (itemName.contains("陶瓷")) {
                    imagePath = "/profile/garbage/ceramic.png";
                } else if (itemName.contains("卫生纸") || itemName.contains("厕纸")) {
                    imagePath = "/profile/garbage/toilet_paper.png";
                } else {
                    imagePath = "/profile/garbage/other.png";
                }
                break;
            default:
                imagePath = "/profile/garbage/other.png";
        }
        
        // 确保图片文件存在
        ensureImageExists(imagePath);
        
        return imagePath;
    }

    /**
     * 确保图片文件存在，如果不存在则创建
     * 
     * @param imagePath 图片路径
     */
    private void ensureImageExists(String imagePath) {
        // 获取完整文件路径
        String fullPath = RuoYiConfig.getProfile() + imagePath.substring(imagePath.indexOf("/profile") + 8);
        File file = new File(fullPath);
        
        // 如果文件不存在，则生成默认图片
        if (!file.exists()) {
            try {
                // 确保目录存在
                File dir = file.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // 生成默认图片
                BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                
                // 设置背景颜色
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, 300, 300);
                
                // 根据文件名设置不同的颜色
                if (imagePath.contains("recyclable") || imagePath.contains("paper") || 
                    imagePath.contains("plastic") || imagePath.contains("glass") || 
                    imagePath.contains("metal") || imagePath.contains("textile")) {
                    g2d.setColor(new Color(0, 122, 204)); // 蓝色 - 可回收物
                } else if (imagePath.contains("hazardous") || imagePath.contains("battery") || 
                          imagePath.contains("medicine") || imagePath.contains("fluorescent") || 
                          imagePath.contains("paint")) {
                    g2d.setColor(new Color(231, 76, 60)); // 红色 - 有害垃圾
                } else if (imagePath.contains("kitchen") || imagePath.contains("fruit") || 
                          imagePath.contains("vegetable") || imagePath.contains("bone") || 
                          imagePath.contains("eggshell") || imagePath.contains("leftover") || 
                          imagePath.contains("offal")) {
                    g2d.setColor(new Color(39, 174, 96)); // 绿色 - 厨余垃圾
                } else {
                    g2d.setColor(new Color(142, 142, 142)); // 灰色 - 其他垃圾
                }
                
                // 绘制圆形
                g2d.fillOval(50, 50, 200, 200);
                
                // 绘制文字 - 提取图片名称
                String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.lastIndexOf("."));
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                
                // 使用FontMetrics计算文本的宽度和高度
                FontMetrics fontMetrics = g2d.getFontMetrics();
                int textWidth = fontMetrics.stringWidth(imageName);
                int textHeight = fontMetrics.getHeight();
                
                // 在图片中心绘制文本
                g2d.drawString(imageName, (300 - textWidth) / 2, 150 + textHeight / 4);
                
                g2d.dispose();
                
                // 保存图片
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成图片
     * 
     * @param name 垃圾名称
     * @param colorName 颜色名称（蓝色、红色、绿色、灰色等）
     * @param category 垃圾类别
     * @return 图片路径
     */
    public String generateImage(String name, String colorName, String category) {
        // 根据颜色名称确定颜色
        Color color;
        switch (colorName.toLowerCase()) {
            case "蓝色":
            case "blue":
                color = new Color(0, 122, 204); // 蓝色 - 可回收物
                break;
            case "红色":
            case "red":
                color = new Color(231, 76, 60); // 红色 - 有害垃圾
                break;
            case "绿色":
            case "green":
                color = new Color(39, 174, 96); // 绿色 - 厨余垃圾
                break;
            case "灰色":
            case "gray":
                color = new Color(142, 142, 142); // 灰色 - 其他垃圾
                break;
            default:
                color = new Color(50, 50, 50); // 默认深灰色
        }
        
        // 生成图片路径
        String fileName = category + "_" + name.hashCode() + ".png";
        String imagePath = "/profile/garbage/" + fileName;
        
        // 确保图片存在
        try {
            String fullPath = RuoYiConfig.getProfile() + "/garbage/" + fileName;
            File file = new File(fullPath);
            
            // 如果文件不存在，则生成
            if (!file.exists()) {
                // 确保目录存在
                File dir = file.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // 创建图片
                BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                
                // 设置抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 设置背景颜色
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, 300, 300);
                
                // 绘制圆形
                g2d.setColor(color);
                g2d.fillOval(50, 50, 200, 200);
                
                // 设置文字
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
                
                // 计算文字位置
                FontMetrics fontMetrics = g2d.getFontMetrics();
                int textWidth = fontMetrics.stringWidth(name);
                int textHeight = fontMetrics.getHeight();
                
                // 如果文字太长，则缩小字体
                if (textWidth > 180) {
                    g2d.setFont(new Font("微软雅黑", Font.BOLD, 18));
                    fontMetrics = g2d.getFontMetrics();
                    textWidth = fontMetrics.stringWidth(name);
                    textHeight = fontMetrics.getHeight();
                }
                
                // 绘制文字
                g2d.drawString(name, (300 - textWidth) / 2, 160);
                
                // 绘制类别
                g2d.setFont(new Font("微软雅黑", Font.PLAIN, 16));
                fontMetrics = g2d.getFontMetrics();
                textWidth = fontMetrics.stringWidth(category);
                g2d.drawString(category, (300 - textWidth) / 2, 200);
                
                // 释放资源
                g2d.dispose();
                
                // 保存图片
                ImageIO.write(image, "png", file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return imagePath;
    }
} 
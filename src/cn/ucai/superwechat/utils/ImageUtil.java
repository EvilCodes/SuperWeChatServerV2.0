package cn.ucai.superwechat.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageUtil {
	 /** 
     * 压缩图片 
     * @param sourcePath 源路径(包含图片) 
     * @param width 压缩后宽度 
     * @param height 压缩后高度 
     * @throws IOException if sourcePath image does not exist 
     */  
    public static void zoom(String sourcePath,/*String targetPath,*/OutputStream os,int width,int height) throws IOException{  
        File imageFile = new File(sourcePath);  
        if(!imageFile.exists()){  
            throw new IOException("Not found the images:"+sourcePath);  
        }  
        String format = sourcePath.substring(sourcePath.lastIndexOf(".")+1,sourcePath.length());  
        BufferedImage image = ImageIO.read(imageFile);  
        image = zoom(image,width,height);  
        ImageIO.write(image, format, os);  
        os.close();
    }  
    
    public static void zoom(String sourcePath,OutputStream os) throws IOException{  
        File imageFile = new File(sourcePath);  
        if(!imageFile.exists()){  
            throw new IOException("Not found the images:"+sourcePath);  
        }  
        String format = sourcePath.substring(sourcePath.lastIndexOf(".")+1,sourcePath.length());  
        BufferedImage image = ImageIO.read(imageFile);  
//        image = zoom(image,width,height);  
        ImageIO.write(image, format, os);  
        os.close();
    }  
      
     /** 
     * 压缩图片 
     * @param sourceImage    待压缩图片 
     * @param width          压缩图片高度 
     * @param heigt          压缩图片宽度 
     */  
    private static BufferedImage zoom(BufferedImage sourceImage , int width , int height){  
        BufferedImage zoomImage = new BufferedImage(width, height, sourceImage.getType());  
        Image image = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);  
        Graphics gc = zoomImage.getGraphics();  
        gc.setColor(Color.WHITE);  
        gc.drawImage( image , 0, 0, null);  
        return zoomImage;  
    }  
}

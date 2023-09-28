package net.multiplique.tests;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.fileupload.FileItem;

public class LocalFileManager implements FileManager {

    private static final Logger logger = Logger.getLogger(LocalFileManager.class.getName());
    
    @Override
    public void copy(String source, String dest) throws FileNotFoundException {
        File sourceFile = new File(source);
        if(!sourceFile.exists()){
            throw new FileNotFoundException("Source does not exist.");
        }
        
        if(dest.endsWith("/")){
            dest = dest + sourceFile.getName();
        }
        File destFile = new File(dest);
        
        try {
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public InputStream read(String fileName) throws FileNotFoundException {
        return new FileInputStream(fileName);
    }

    @Override
    public void delete(String fileName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void write(FileItem fileItem, String fileName) {
        try {
            File serverFile = new File(fileName.replace(":", "_"));
            fileItem.write(serverFile);
        } catch (Exception ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void writeImage(BufferedImage scaledImage, FileOutputStream os) {
        try {
            ImageIO.write(scaledImage, "jpg", os);
        } catch (IOException ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void writeImage(BufferedImage image, String fileName) {
        try {
            File file = new File(fileName);
            ImageIO.write(image, fileName.substring(fileName.lastIndexOf(".") + 1), file);
        } catch (IOException ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public BufferedImage getImage(String fileName){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

}

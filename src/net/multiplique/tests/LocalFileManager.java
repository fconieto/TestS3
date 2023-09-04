package net.multiplique.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.fileupload.FileItem;

public class LocalFileManager implements FileManager {

    private static final Logger logger = Logger.getLogger(LocalFileManager.class.getName());

    @Override
    public File read() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void write(FileItem fileItem, File file) {
        try {
            fileItem.write(file);
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
}

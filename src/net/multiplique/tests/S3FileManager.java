package net.multiplique.tests;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.fileupload.FileItem;

public class S3FileManager implements FileManager {
    private static final String bucketName = "multiplique";
    private static final Regions region = Regions.US_EAST_1;
         
    private static final Logger logger = Logger.getLogger(S3FileManager.class.getName());
    
    @Override
    public File read(String keyName) {    
        keyName = "test/test2/prueba.jpg"; 
        
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .build();
        
        try {
            if(!s3.doesObjectExist(bucketName, keyName)){
                throw new Exception("keyName does not exist.");
            }
            InputStream in = s3.getObject(bucketName, keyName).getObjectContent();
            
            File tmp = File.createTempFile("s3temp_", "");
            
            byte[] buf = new byte[1024];
            int count = 0;
            OutputStream out = new FileOutputStream(tmp);
            while( (count = in.read(buf)) > 0)
            {
               out.write(buf, 0, count);
            }
            out.close();
            in.close();
            
            return tmp;
        } catch (Exception ex) {
            Logger.getLogger(S3FileManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void delete(String keyName) {
        keyName = "test/test2/prueba.jpg"; // + fileName;
        
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .build();
        try {
            if(!s3.doesObjectExist(bucketName, keyName)){
                throw new Exception();
            }
            s3.deleteObject(bucketName, keyName);        
        } catch (Exception ex) {
            Logger.getLogger(S3FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write(FileItem fileItem, String keyName) {
        keyName = "test/test2/prueba.jpg"; // + fileName;
        
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .build();
        try {
            ObjectMetadata om = new ObjectMetadata();
            om.setContentType(fileItem.getContentType());
            om.setContentLength(fileItem.getSize());
            
            s3.putObject(bucketName, keyName, fileItem.getInputStream(), om);
        
        } catch (Exception ex) {
            Logger.getLogger(S3FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void writeImage(BufferedImage scaledImage, FileOutputStream os) {
        try {
            ImageIO.write(scaledImage, "jpg", os);
        } catch (IOException ex) {
            Logger.getLogger(S3FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getFolder(String keyName){
        return keyName.substring(0, keyName.lastIndexOf("/") + 1 );
    }
    
}

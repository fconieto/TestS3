package net.multiplique.tests;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;

public class S3FileManager implements FileManager {

    private static final String BUCKET_NAME = "multiplique";
    private static final Regions REGION = Regions.US_EAST_1;

    private static final Logger logger = Logger.getLogger(S3FileManager.class.getName());

    @Override
    public void copy(String sourceKey, String destKey) throws FileNotFoundException {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .build();

        if (!s3.doesObjectExist(BUCKET_NAME, sourceKey)) {
            throw new FileNotFoundException("Source does not exist.");
        }

        if (sourceKey.endsWith("/")) {
            ListObjectsV2Result result = s3.listObjectsV2(BUCKET_NAME, sourceKey);
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            for (S3ObjectSummary os : objects) {
                String destinoFileName = destKey + os.getKey().substring(os.getKey().lastIndexOf("/"));
                s3.copyObject(BUCKET_NAME, BUCKET_NAME, os.getKey(), destinoFileName);
            }
        } else {
            if (destKey.endsWith("/")) {
                destKey = destKey + sourceKey.substring(sourceKey.lastIndexOf("/") + 1);
            }
            // copyObject(BUCKET_SOURCE, BUCKET_DEST, SOURCE_KEY, DEST_KEY)
            s3.copyObject(BUCKET_NAME, BUCKET_NAME, sourceKey, destKey);
        }
    }

    @Override
    public InputStream read(String keyName) throws FileNotFoundException {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .build();

        if (!s3.doesObjectExist(BUCKET_NAME, keyName)) {
            throw new FileNotFoundException("keyName does not exist.");
        }
        return s3.getObject(BUCKET_NAME, keyName).getObjectContent();
    }

    @Override
    public void delete(String keyName) {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .build();
        try {
            if (!s3.doesObjectExist(BUCKET_NAME, keyName)) {
                throw new Exception();
            }
            s3.deleteObject(BUCKET_NAME, keyName);
        } catch (Exception ex) {
            Logger.getLogger(S3FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write(FileItem fileItem, String keyName) {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .build();
        try {
            ObjectMetadata om = new ObjectMetadata();
            om.setContentType(fileItem.getContentType());
            om.setContentLength(fileItem.getSize());

            s3.putObject(BUCKET_NAME, keyName, fileItem.getInputStream(), om);

        } catch (SdkClientException | IOException ex) {
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

    @Override
    public void writeImage(BufferedImage image, String keyName) {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .build();
        String formatName = keyName.substring(keyName.lastIndexOf(".") + 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, formatName, baos);
            InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
            ObjectMetadata om = s3.getObject(BUCKET_NAME, keyName).getObjectMetadata();

            s3.putObject(BUCKET_NAME, keyName, inputStream, om);

        } catch (SdkClientException | IOException ex) {
            Logger.getLogger(S3FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BufferedImage getImage(String keyName) {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .build();
        BufferedImage image = null;
        try {
            try (InputStream is = s3.getObject(BUCKET_NAME, keyName).getObjectContent()) {
                image = ImageIO.read(is);
            }
        } catch (IOException ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

}

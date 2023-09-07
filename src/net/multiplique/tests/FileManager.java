package net.multiplique.tests;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.commons.fileupload.FileItem;

public interface FileManager {

    public void write(FileItem fileItem, String fileName);

    public void writeImage(BufferedImage scaledImage, FileOutputStream os);

    public InputStream read(String fileName) throws FileNotFoundException;

    public void delete(String fileName);
}

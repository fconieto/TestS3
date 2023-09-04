package net.multiplique.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.commons.fileupload.FileItem;

public interface FileManager {

    public void write(FileItem fileItem, File file);

    public void writeImage(BufferedImage scaledImage, FileOutputStream os);

    public File read();

    public void delete();
}

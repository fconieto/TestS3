package net.multiplique.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.apache.commons.fileupload.FileItem;

public class LocalFileManager implements FileManager {

    private static final Logger logger = Logger.getLogger(LocalFileManager.class.getName());

    public static final String homeTomcatK2Path = "";

    @Override
    public void copy(String source, String dest) throws FileNotFoundException {
        File origen = new File(homeTomcatK2Path + source);
        if (!origen.exists()) {
            throw new FileNotFoundException("Source does not exist.");
        }

        int indexName = dest.endsWith("/") ? dest.length() : dest.lastIndexOf("/");

        File dirDestino = new File(homeTomcatK2Path + dest.substring(0, indexName));
        dirDestino.mkdirs();

        if (origen.isDirectory()) {
            try (Stream<Path> walk = Files.walk(origen.toPath())) {
                walk.filter(Files::isRegularFile).forEach(fpath -> {
                    try {
                        String destinoFileName = fpath.toString().substring(fpath.toString().lastIndexOf("/"));
                        Files.copy(fpath, Paths.get(dirDestino.getAbsolutePath() + destinoFileName), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "Copy directory Internal ", ex);
                    }
                });
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Copy directory External: ", ex);
            }
        }

        if (origen.isFile()) {
            try {
                String destinoFileName = dest.substring(indexName);

                if (dest.endsWith("/")) {
                    destinoFileName = source.substring(source.lastIndexOf("/"));
                }

                Files.copy(origen.toPath(), Paths.get(dirDestino.getAbsolutePath() + destinoFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Copy file error", ex);
            }
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
    public BufferedImage getImage(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException ex) {
            Logger.getLogger(LocalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

}

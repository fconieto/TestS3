package net.multiplique.tests;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ImageTool {

    private static final Logger logger = Logger.getLogger(ImageTool.class.getName());

    private ImageTool() {

    }

    public static void rotate(FileReference rf) {
        try {
            File file = new File(rf.ruta + rf.id);
            BufferedImage source = ImageIO.read(file);
            BufferedImage output = new BufferedImage(source.getHeight(), source.getWidth(), source.getType());
            AffineTransformOp op = new AffineTransformOp(rotateCounterClockwise90(source), AffineTransformOp.TYPE_BILINEAR);
            op.filter(source, output);
            ImageIO.write(output, rf.id.substring(rf.id.lastIndexOf(".") + 1), file);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error loading the images", ex);
        }
    }

    public static final AffineTransform rotateCounterClockwise90(BufferedImage source) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(-Math.PI / 2, (source.getWidth() / 2.0), (source.getHeight() / 2.0));
        double offset = (source.getWidth() - source.getHeight()) / 2.0;
        transform.translate(-offset, -offset);
        return transform;
    }

    public static void createStamp(FileReference fr, double factor, String prefix) {
        try {
            Path path = Paths.get(fr.ruta + fr.id);
            long bytes = Files.size(path);

            if (bytes > 0) {
                BufferedImage im = ImageIO.read(new File(fr.ruta + fr.id));
                if (im != null) {
                    int w = im.getWidth();
                    int h = im.getHeight();

                    int fw = (int) (w * factor);
                    int fh = (int) (h * factor);

                    BufferedImage scaledImage = new BufferedImage(fw, fh, im.getType());

                    AffineTransform tx = AffineTransform.getScaleInstance(factor, factor);

                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
                    scaledImage = op.filter(im, scaledImage);

                    FileOutputStream os = new FileOutputStream(fr.ruta + prefix + fr.id);
                    ImageIO.write(scaledImage, "jpg", os);
                    im.flush();
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "createStamp", ex);
        }
    }

}

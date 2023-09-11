package net.multiplique.tests;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageTool {

    private static final Logger logger = Logger.getLogger(ImageTool.class.getName());
    private final FileManager fileManager;

    public ImageTool(FileManager _fileManager) {
         fileManager = _fileManager;
    }

    public void rotate(FileReference fr) {
        try {
            BufferedImage source = fileManager.getImage(fr.ruta + fr.id);
            BufferedImage output = new BufferedImage(source.getHeight(), source.getWidth(), source.getType());
            AffineTransformOp op = new AffineTransformOp(rotateCounterClockwise90(source), AffineTransformOp.TYPE_BILINEAR);
            op.filter(source, output);
            
            fileManager.writeImage(output, fr.ruta + fr.id);
        } catch (Exception ex) {
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

    public void createStamp(FileReference fr, double factor, String prefix) {
        try {
            BufferedImage im = fileManager.getImage(fr.ruta + fr.id);
            if (im != null) {
                int w = im.getWidth();
                int h = im.getHeight();

                int fw = (int) (w * factor);
                int fh = (int) (h * factor);

                BufferedImage scaledImage = new BufferedImage(fw, fh, im.getType());

                AffineTransform tx = AffineTransform.getScaleInstance(factor, factor);

                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
                scaledImage = op.filter(im, scaledImage);
                
                fileManager.writeImage(scaledImage, fr.ruta + fr.id);
                im.flush();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "createStamp", ex);
        }
    }

}

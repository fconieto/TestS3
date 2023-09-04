package net.multiplique.tests;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class for writing out an image.
 * <P>
 * A framework for classes that encode and write out an image in a particular
 * file format.
 * <P>
 * This provides a simplified rendition of the ImageConsumer interface. It
 * always delivers the pixels as ints in the RGBdefault color model. It always
 * provides them in top-down left-right order. If you want more flexibility you
 * can always implement ImageConsumer directly.
 * <P>
 * <A
 * HREF="/resources/classes/Acme/JPM/Encoders/ImageEncoder.java">Fetch the
 * software. </A> <BR>
 * <A HREF="/resources/classes/Acme.tar.gz">Fetch the entire Acme package. </A>
 * <P>
 *
 * @see GifEncoder
 * @see PpmEncoder
 */
public abstract class ImageEncoder implements ImageConsumer {

    protected OutputStream out;

    private ImageProducer producer;
    private int width = -1;
    private int height = -1;
    private int hintflags = 0;
    private boolean started = false;
    private boolean encoding;
    private IOException iox;
    private static final ColorModel rgbModel = ColorModel.getRGBdefault();
    private HashMap<String, String> props = null;

    public ImageEncoder(Image img, OutputStream out) throws IOException {
        this(img.getSource(), out);
    }

    public ImageEncoder(ImageProducer producer, OutputStream out)
            throws IOException {
        this.producer = producer;
        this.out = out;
    }

    abstract void encodeStart(int w, int h) throws IOException;

    abstract void encodePixels(int x, int y, int w, int h, int[] rgbPixels,
            int off, int scansize) throws IOException;

    abstract void encodeDone() throws IOException;

    public void encode() throws IOException {
        encoding = true;
        iox = null;
        producer.startProduction(this);
//        while (encoding) {
//            try {
//                wait();
//            } catch (InterruptedException e) {
//            }
//        }
        if (iox != null) {
            throw iox;
        }
    }

    private boolean accumulate = false;
    private int[] accumulator;

    private void encodePixelsWrapper(int x, int y, int w, int h,
            int[] rgbPixels, int off, int scansize)
            throws IOException {
        if (!started) {
            started = true;
            encodeStart(width, height);
            if ((hintflags & TOPDOWNLEFTRIGHT) == 0) {
                accumulate = true;
                accumulator = new int[width * height];
            }
        }
        if (accumulate) {
            for (int row = 0; row < h; ++row) {
                System.arraycopy(rgbPixels,
                        row * scansize + off,
                        accumulator,
                        (y + row) * width + x,
                        w);
            }
        } else {
            encodePixels(x, y, w, h, rgbPixels, off, scansize);
        }
    }

    private void encodeFinish() throws IOException {
        if (accumulate) {
            encodePixels(0, 0, width, height, accumulator, 0, width);
            accumulator = null;
            accumulate = false;
        }
    }

    private void stop() {
        encoding = false;
        // notifyAll();
    }

    @Override
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void setProperties(Hashtable _props) {
         if (_props == null) {
             this.props = null;
            return;
        }
         
        this.props = new HashMap<>();
        Iterator iter = _props.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            this.props.put(key, value);
        }
    }

    public HashMap getProperties() {
        return this.props;
    }

    @Override
    public void setColorModel(ColorModel model) {
        // Ignore.
    }

    @Override
    public void setHints(int hintflags) {
        this.hintflags = hintflags;
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model,
            byte[] pixels, int off, int scansize) {
        int[] rgbPixels = new int[w];
        for (int row = 0; row < h; ++row) {
            int rowOff = off + row * scansize;
            for (int col = 0; col < w; ++col) {
                rgbPixels[col] = model.getRGB(pixels[rowOff + col] & 0xff);
            }
            try {
                encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
            } catch (IOException e) {
                iox = e;
                stop();
                return;
            }
        }
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model,
            int[] pixels, int off, int scansize) {
        if (model == rgbModel) {
            try {
                encodePixelsWrapper(x, y, w, h, pixels, off, scansize);
            } catch (IOException e) {
                iox = e;
                stop();
            }
        } else {
            int[] rgbPixels = new int[w];
            for (int row = 0; row < h; ++row) {
                int rowOff = off + row * scansize;
                for (int col = 0; col < w; ++col) {
                    rgbPixels[col] = model.getRGB(pixels[rowOff + col]);
                }
                try {
                    encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
                } catch (IOException e) {
                    iox = e;
                    stop();
                    return;
                }
            }
        }
    }

    @Override
    public void imageComplete(int status) {
        producer.removeConsumer(this);
        if (status == ImageConsumer.IMAGEABORTED) {
            iox = new IOException("image aborted");
        } else {
            try {
                encodeFinish();
                encodeDone();
            } catch (IOException e) {
                iox = e;
            }
        }
        stop();
    }

}

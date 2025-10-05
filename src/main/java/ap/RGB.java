package ap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class RGB {
    private int width;
    private int height;
    private boolean hasAlphaChannel;
    private int pixelLength;
    private byte[] pixels;

    RGB(BufferedImage image) {
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
        hasAlphaChannel = image.getAlphaRaster() != null;
        pixelLength = (hasAlphaChannel) ? 4 : 3;
    }

    public int getRGB(int x, int y) {
        int pos = (y * pixelLength * width) + (x * pixelLength);
        int rgb = 0x0;
        if (hasAlphaChannel) {
            pos++;
        }

        rgb += ((int) pixels[pos++] & 0xff);
        rgb += ((int) pixels[pos++] & 0xff) << 8;
        rgb += ((int) pixels[pos++] & 0xff) << 16;
        return rgb;
    }

    public int getPixelCount() {
        return width * height;
    }
}

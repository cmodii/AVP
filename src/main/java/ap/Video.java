package ap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class Video {
    private ArrayList<Frame> Frames = new ArrayList<>();
    private int width;
    private int height;
    private boolean hasAlphaChannel;

    public int getLength() {
        return Frames.size();
    }

    private char RGBToSymbol(byte R, byte G, byte B) {        
        double r = ((int)R & 0xFF) / 255.0;
        double g = ((int)G & 0xFF) / 255.0;
        double b = ((int)B & 0xFF) / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double hue;

        if (max-min == 0.0) {
            hue = 0.0;
        } else if (r == max) {
            hue = 60*((g-b)/(max-min) % 6);
        } else if (g == max) {
            hue = 60*(2.0 + (b-r)/(max-min));
        } else {
            hue = 60*(4.0 + (r-g)/(max-min));
        }

        if (hue < 0.0) {
            hue += 360;
        }

        if (hue < 60.0) {
            return '&'; // orange
        } else if (hue < 120.0) {
            return '#'; // yellow
        } else if (hue < 180.0) {
            return '@'; // green
        } else if (hue < 240.0) {
            return '$'; // blue
        } else if (hue < 300.0) {
            return '%'; // pink
        } else {
            return '~'; // reddish pink?
        }
    }

    public void play() {
        try {
            Java2DFrameConverter Converter = new Java2DFrameConverter();
            for (int i = 0; i < 500; i++) {
                BufferedImage image = Converter.convert(Frames.get(i));
                byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                String data = "";
                int offset = width / 160;

                for (int p = 0; p < 14400; p++) {
                    if (hasAlphaChannel) {
                        data += RGBToSymbol(pixels[(p+offset)*4+1], pixels[(p+offset)*4+2], pixels[(p+offset)*4+3]);
                    } else {
                        // data += RGBToSymbol(pixels[(p+offset)*3], pixels[(p+offset)*3+1], pixels[(p+offset)*3+2]);
                        data += RGBToSymbol(pixels[((p % 160) * offset + (p / 160) * offset * 1920) * 3], pixels[((p % 160) * offset + (p / 160) * offset * 1920) * 3 + 1], pixels[((p % 160) * offset + (p / 160) * offset * 1920) * 3 + 2]);
                    }

                    if (p != 0 && p % 160 == 0) {
                        data += "\n";
                    }
                }
    
                System.out.print(data);
                Thread.sleep(1000/30);
                System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                System.out.print("\033[H\033[2J"); // clear terminal
                System.out.flush();
            }
            Converter.close();
        } catch (Exception e) {
            System.err.print(e);
        }
    }

    Video(String fpath) {
        try {
            FFmpegFrameGrabber Grabber = new FFmpegFrameGrabber(fpath);
            Java2DFrameConverter Converter = new Java2DFrameConverter();
            Frame capturedFrame = null;
        
            Grabber.start();
            capturedFrame = Grabber.grab();

            while (capturedFrame != null) {
                Frames.add(capturedFrame.clone());
                capturedFrame = Grabber.grab();
            }

            Grabber.close();
            
            height =  Converter.convert(Frames.get(0)).getHeight();
            width = Converter.convert(Frames.get(0)).getWidth();
            hasAlphaChannel = Converter.convert(Frames.get(0)).getAlphaRaster() != null;
            Converter.close();
        } catch (Exception e) {
            System.err.print(e);
        }
    }


}
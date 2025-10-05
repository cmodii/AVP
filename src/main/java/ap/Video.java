package ap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.lang.StringBuilder;

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
        Terminal terminal = null;
        Java2DFrameConverter Converter = null;
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            Converter = new Java2DFrameConverter();

            for (int i = 0; i < getLength(); i++) {
                BufferedImage image = Converter.convert(Frames.get(i));
                byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                StringBuilder data = new StringBuilder();
                int offset = width / 80;

                for (int p = 0; p < 4000; p++) {
                    if (hasAlphaChannel) {
                        data.append(RGBToSymbol(pixels[(p+offset)*4+1], pixels[(p+offset)*4+2], pixels[(p+offset)*4+3]));
                    } else {
                        // data += RGBToSymbol(pixels[(p+offset)*3], pixels[(p+offset)*3+1], pixels[(p+offset)*3+2]);
                        data.append(RGBToSymbol(pixels[((p % 80) * offset + (p / 80) * offset * width) * 3], pixels[((p % 80) * offset + (p / 80) * offset * width) * 3 + 1], pixels[((p % 80) * offset + (p / 80) * offset * width) * 3 + 2]));
                    }

                    if (p != 0 && p % 80 == 0) {
                        data.append('\n');
                    }
                }
    
                // Print the frame data

                terminal.writer().print(data.toString());
                //terminal.writer().flush();

                
                Thread.sleep(1000);
                System.out.print("\033\143");
                //terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
                //terminal.flush();

                // System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                // System.out.print("\033[H\033[2J"); // clear terminal
            }
            Converter.close();
        } catch (Exception e) {
            System.err.print(e);
        }  finally {
            if (terminal != null) {
                try {
                    terminal.close();
                } catch (Exception e) {
                    System.err.print(e);
                }
            }

            if (Converter != null) {
                try {
                    Converter.close();
                } catch (Exception e) {
                    System.err.print(e);
                }
            }
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
package ap;

import java.util.Scanner;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

public class AP {
    public static void main( String[] args ) {
        System.out.print(".mp4 file path: ");
        Scanner IO = new Scanner(System.in);
        String fpath = IO.nextLine();
        
        IO.close();
        FFmpegFrameGrabber Grabber = null;
        CanvasFrame Frame = null;

        try {
            Grabber = new FFmpegFrameGrabber(fpath);
            Grabber.start();
            System.out.println("Video loaded successfully");
            
            Frame = new CanvasFrame("Video Display");
            Frame CapturedFrame;

            while (Frame.isVisible()) {
                CapturedFrame = Grabber.grab();
                if (CapturedFrame == null) {break;}
                Frame.showImage(CapturedFrame);
                Thread.sleep(10);
            }

            Frame.dispose();
        } catch (Exception e) {
            System.err.println("File path incorrect\nFeedback: "+e);
            System.exit(1);
        } finally {
            try {
                Frame.dispose();
                Grabber.close();
            } catch (Exception e) {
                System.err.println("Unable to close Grabber: " + e);
            }
        } 

    }
}

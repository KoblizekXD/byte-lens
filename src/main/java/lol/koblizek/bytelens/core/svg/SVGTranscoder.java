package lol.koblizek.bytelens.core.svg;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;

public class SVGTranscoder extends ImageTranscoder {
    private BufferedImage img;

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void writeImage(BufferedImage bufferedImage, TranscoderOutput transcoderOutput) throws TranscoderException {
        this.img = bufferedImage;
    }

    public BufferedImage getImg() {
        return img;
    }
}

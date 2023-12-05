import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Figure {
    public final byte BlockSize = 25;
    private final String[][][] figures = {
            {{"0", "0"},{"0", "0"}},
            {{"0", ""}, {"0", ""}, {"0", "0"}},
            {{"",  "0"}, {"",  "0"}, {"0", "0"}},
            {{"0", "0", ""}, {"", "0", "0"}},
            {{"", "0", "0"}, {"0", "0", ""}},
            {{"", "0", ""}, {"0", "0", "0"}},
            {{"0", "0", "0", "0"},}
    };

    private final HashMap<String, Image> images;
    private final String[] keysColors;

    {
        images = new HashMap<String, Image>();
    }
    public Figure() throws RuntimeException{

        try {
            images.put("gr",  ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/gr.png"))));
            images.put("bl",  ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/bl.png"))));
            images.put("bir", ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/bir.png"))));
            images.put("red", ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/re.png"))));
            images.put("or",  ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/or.png"))));
            images.put("ye",  ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/ye.png"))));
            images.put("fi",  ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/fi.png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int i = 0;
        keysColors = new String[images.size()];

        for ( String key : images.keySet() ) {
            keysColors[i] = key;
            i++;
        }

    }

    public boolean haveColorInMap(String colorKey) {
        for ( String key : images.keySet() ) {
            if(Objects.equals(colorKey, key)){
                return true;
            }
        }

        return false;
    }

    public Image getColorFromKey(String key){
        return images.get(key);
    }

    public String getRandomColorKey()
    {
        int length = keysColors.length;
        return keysColors[(int) (Math.random() * length)];
    }

    public String[][] getRandomFigure(){
        int length = figures.length;
        return figures[(int) (Math.random() * length)];
    }
}

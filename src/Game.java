import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

interface MyFunctionalInterface {

    public void calc(int y, int x);
}
public class Game{
    protected final Short widthWindows = 450, heightWindows = 655, countLineLvl = 12;
    protected Integer countGame = 0, countLine = 0, loop = 0, speed = 100;
    protected String[][] field, figure, nextFigure;
    protected Integer[] lastPos;
    protected MyComp myCompClass;
    protected Rectangle2D rectangle2DClass;
    protected Figure figureClass;
    protected Image titleImageClass;
    protected Image backgroundImageClass;
    protected AudioPlayer audioPlayerClass;

    {
        audioPlayerClass = new AudioPlayer();
        rectangle2DClass = new Rectangle2D.Double( 0,  0, 0, 0);
        figureClass = new Figure();
        myCompClass = new MyComp();

        try {
            backgroundImageClass = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/tet1.jpg")));
            titleImageClass = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("image/t2.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void start() throws RuntimeException{
        field = new String[20][10];

        for (String[] strings : field) {
            Arrays.fill(strings, "");
        }

        audioPlayerClass.startAudio();

        figure = figureClass.getRandomFigure();
        nextFigure = figureClass.getRandomFigure();
        lastPos = new Integer[2];
        lastPos[1] = 4;

        JFrame frame = getFrame();

        frame.addKeyListener(myCompClass);
        frame.add(myCompClass);

        myCompClass.initFirstFigure();

    }

    class MyComp extends JPanel implements Runnable, KeyListener {
        protected boolean right = false, left = false, down = false, up = false;

        public MyComp () {
            addKeyListener(this);
            (new Thread(this)).start();
        }

        @Override
        public void run () throws RuntimeException {
            while (true){
                try{
                    Thread.sleep(speed - (int) (Math.floor((double) countLine / countLineLvl) * 10));
                    super.repaint();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            drawGame(g2);
            moveRightLeft();

            if(loop == 0){
                countNextStep();
            }

            countLoop();

            g2.drawString("Очки : " + countGame + " Линия : " + countLine,  300, 15);
            g2.drawString("Уровень : " + (int) Math.floor((double) countLine / countLineLvl),  300, 25);
        }

        protected void drawGame(Graphics2D g2){
            g2.drawImage(backgroundImageClass, 0, 120, field[0].length * figureClass.BlockSize, field.length * figureClass.BlockSize, this);
            g2.drawImage(titleImageClass, 0, 10, field[0].length * figureClass.BlockSize, 100, this);
            drawField(g2);
            drawNextFigure(g2);
        }

        private void drawField(Graphics2D g2)
        {
            for(int y = field.length - 1; y >= 0; y--){
                for(int x = field[y].length - 1; x >= 0; x--){
                    if (figureClass.haveColorInMap(field[y][x])){
                        g2.drawImage(figureClass.getColorFromKey(field[y][x]), x * figureClass.BlockSize, y * figureClass.BlockSize + 120, figureClass.BlockSize, figureClass.BlockSize, this);
                    } else if (Objects.equals(field[y][x], "0")) {
                        g2.drawImage(figureClass.getColorFromKey("bl"), x * figureClass.BlockSize, y * figureClass.BlockSize  + 120, figureClass.BlockSize, figureClass.BlockSize, this);
                    }
                }
            }
        }

        private void drawNextFigure(Graphics2D g2)
        {
            for(int y = nextFigure.length - 1; y >= 0; y--){
                for(int x = nextFigure[y].length - 1; x >= 0; x--){
                    if(!Objects.equals(nextFigure[y][x], "")){
                        g2.drawImage(figureClass.getColorFromKey("gr"), 300 + x * figureClass.BlockSize, 50 + y * figureClass.BlockSize, figureClass.BlockSize, figureClass.BlockSize, this);
                    }
                }
            }
        }

        private void checkFullLine() {

            boolean foolLine;

            for (int y = 0; y < field.length; y++) {

                foolLine = true;

                for (int x = 0; x < field[y].length; x++) {
                    if (Objects.equals(field[y][x], "")) {
                        foolLine = false;
                        break;
                    }
                }

                if (foolLine) {
                    countLine++;
                    countGame += 100;

                    System.out.println(speed);

                    String[][] newField = new String[field.length][field[0].length];
                    String[][] newFieldStart = Arrays.copyOfRange(field, 0, y);
                    String[][] newFieldEnd = Arrays.copyOfRange(field, y + 1, field.length);
                    String[][] mergedField = new String[newFieldStart.length + newFieldEnd.length][field[y].length];

                    int pos = 0;

                    for (String[] element : newFieldStart)
                    {
                        mergedField[pos] = element;
                        pos++;
                    }

                    for (String[] element : newFieldEnd)
                    {
                        mergedField[pos] = element;
                        pos++;
                    }

                    int fieldCount = field.length - newFieldStart.length - newFieldEnd.length;

                    for (String[] elem : newField){
                        Arrays.fill(elem, "");
                    }

                    System.arraycopy(mergedField, 0, newField, fieldCount, mergedField.length);

                    field = newField;
                    audioPlayerClass.setPlayAnother();
                }
            }
        }

        private void countLoop() {
            loop++;
            if(loop > (down ? 0 : 6)){
                up = false;
                loop = 0;
            }
        }

        private void rotateFigure(){
            String[][] rotateFigure = new String[figure[0].length][figure.length];

            for(int o = 0; o < figure[0].length; o++) {
                for (int i = figure.length - 1; i >= 0; i--) {
                    rotateFigure[o][figure.length - i - 1] = figure[i][o];
                }
            }

            try {
                clearFieldFromActive();
                for (int i = 0; i < rotateFigure.length; i++) {
                    for (int o = 0; o < rotateFigure[i].length; o++) {
                        if(lastPos[0] + i >= field.length || lastPos[1] + o >= field[0].length || !(field[lastPos[0] + i][lastPos[1] + o] == "")){
                            throw new Exception("Limit");
                        }
                    }
                }
            } catch (Exception e) {
                clearFieldFromActive();
                drawActiveFigure();
                return;
            }

            figure = rotateFigure;

            clearFieldFromActive();
            drawActiveFigure();
        }

        private void moveRightLeft(){
            boolean cantLeft = false;
            boolean cantRight = false;

            for(int y = field.length - 1; y >= 0; y--){
                for(int x = field[y].length - 1; x >= 0; x--){
                    if(Objects.equals(field[y][x], "0")){
                        if(x + 1 >= field[y].length || (x + 1 <= field[y].length && figureClass.haveColorInMap(field[y][x+1]))){
                            cantRight = true;
                        }
                        if(x - 1 < 0 || figureClass.haveColorInMap(field[y][x-1])){
                            cantLeft = true;
                        }
                    }
                }
            }

            if(right && !cantRight){
                lastPos[1]++;
            }else if (left && !cantLeft) {
                lastPos[1]--;
            }

            for(int y = field.length - 1; y >= 0; y--){
                if(right) {
                    for(int x = field[y].length - 1; x >= 0; x--){
                        if (Objects.equals(field[y][x], "0")) {
                            if(x + 1 <= field[y].length && !cantRight){
                                field[y][x + 1] = "0";
                                field[y][x] = "";
                            }
                        }

                    }
                }else if (left){
                    for(int x = 0; x < field[y].length; x++){
                        if (Objects.equals(field[y][x], "0")) {
                            if(x - 1 >= 0 && !cantLeft) {
                                field[y][x - 1] = "0";
                                field[y][x] = "";
                            }
                        }
                    }
                }
            }
        }

        private void parseField(MyFunctionalInterface msg){
            for(int y = field.length - 1; y >= 0; y--){
                for(int x = field[y].length - 1; x >= 0; x--){
                    msg.calc(y, x);
                }
            }
        }

        private void countNextStep(){
            lastPos[0]++;

            MyFunctionalInterface calc = (int y, int x) -> {
                if (Objects.equals(field[y][x], "0") && y + 1 >= field.length || (y + 1 < field.length && figureClass.haveColorInMap(field[y + 1][x]) && Objects.equals(field[y][x], "0"))) {
                    stopAllBlock();
                }
            };

            parseField(calc);

            calc = (int y, int x) -> {
                if (Objects.equals(field[y][x], "0")) {
                    field[y + 1][x] = "0";
                    field[y][x] = "";
                }
            };

            parseField(calc);
        }

        private void clearFieldFromActive(){
            MyFunctionalInterface calc = (int y, int x) -> {
                if (Objects.equals(field[y][x], "0")) {
                    field[y][x] = "";
                }
            };

            parseField(calc);
        }
        private void initFirstFigure(){
            String colorKey = figureClass.getRandomColorKey();

            MyFunctionalInterface calc = (int y, int x) -> {
                if (Objects.equals(field[y][x], "0")) {
                    field[y][x] = colorKey;
                }
            };

            parseField(calc);

            replaceAndSetNextFigure();

            drawActiveFigure();
        }

        protected void replaceAndSetNextFigure(){
            lastPos[0] = 0;
            lastPos[1] = 4;

            figure = nextFigure;
            nextFigure = figureClass.getRandomFigure();
        }

        private void stopAllBlock(){

            String colorKey = figureClass.getRandomColorKey();
            MyFunctionalInterface calc = (int y, int x) -> {
                if (Objects.equals(field[y][x], "0")) {
                    field[y][x] = colorKey;
                }
            };
            parseField(calc);
            checkFullLine();
            replaceAndSetNextFigure();
            drawActiveFigure();
        }

        protected void drawActiveFigure(){
            for (int i = 0; i < figure.length; i++) {
                for (int o = 0; o < figure[i].length; o++) {
                    if(!Objects.equals(figure[i][o], "")){
                        field[lastPos[0] + i][lastPos[1] + o] = "0";
                    }
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                right = true;
                left = false;
            }
            if(e.getKeyCode() == KeyEvent.VK_LEFT){
                left = true;
                right = false;
            }
            if(e.getKeyCode() == KeyEvent.VK_DOWN){
                down = true;
            }
            if(e.getKeyCode() == KeyEvent.VK_UP){
                rotateFigure();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_DOWN){
                down = false;
            }
            if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                right = false;
            }

            if(e.getKeyCode() == KeyEvent.VK_LEFT){
                left = false;
            }

        }
    }

    protected JFrame getFrame() {
        JFrame frame = new JFrame() {};
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(widthWindows, heightWindows);
        frame.setResizable(false);
        Container c = frame.getContentPane();
        c.setBackground(new Color(55,55,55));

        frame.setContentPane(c);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        frame.setLocation(((int) width / 2) - widthWindows / 2, ((int) height / 2) - heightWindows / 2);
        frame.setTitle("Tetris ept");
        frame.setVisible(true);

        return frame;
    }
}
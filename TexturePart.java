import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class TexturePart {

	static final int VEIN_SIZE = 100, BLOCK_SIZE = 8,
			VAL_INCREASE = 1, TEMP_RANGE = 50;
	static Random r = new Random();

	static BufferedImage getTexture(Color base, Color second) {
		BufferedImage b = new BufferedImage(Game.MAX_ZOOM, Game.MAX_ZOOM,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = b.createGraphics();
		g.setColor(base);
		g.fillRect(0, 0, Game.MAX_ZOOM, Game.MAX_ZOOM);

		for (int i = 0; i < 1; i++) {
			createColorVein(g, r.nextInt(b.getWidth()), r.nextInt(b.getHeight()), second.getRed(),
					second.getGreen(), second.getBlue(), VEIN_SIZE, r.nextBoolean());

		}

		return b;
	}
	

	/*
	 * Creates a color "vein" on a texture, using a similar recursive algorithm
	 * to the one that creates the game's terrain.
	 * 
	 * g = Graphics object to painted on.
	 * (x, y) = starting position of vein on the texture.
	 * rVal, gVal, bVal = colour that will be painted i.e. new Color(rVal, gVal, bVal)
	 * kill = decreasing integer that terminates recursion.
	 * increase determines whether the variations in rgb will increase or decrease.
	 */
	protected static void createColorVein(Graphics g, int x, int y, int rVal,
			int gVal, int bVal, int kill, boolean increase) {

		
		if (kill > 1) {

			g.setColor(new Color(rVal, gVal, bVal));
			g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);

			// increases/decreases rVal, bVal or gVal.
			if (increase){
				int i = r.nextInt(3);
				
				if(i == 0 && rVal + VAL_INCREASE < 256){
					rVal += VAL_INCREASE;
				} else if (i == 1 && gVal + VAL_INCREASE < 256){
					gVal += VAL_INCREASE;
				} else if (i == 2 && bVal + VAL_INCREASE < 256){
					bVal += VAL_INCREASE;
				}
			} else {
				int i = r.nextInt(3);
				
				if(i == 0 && rVal - VAL_INCREASE >= 0){
					rVal -= VAL_INCREASE;
				} else if (i == 1 && gVal - VAL_INCREASE >= 0){
					gVal -= VAL_INCREASE;
				} else if (i == 2  && bVal - VAL_INCREASE >= 0){
					bVal -= VAL_INCREASE;
				}
			}
			
			if (r.nextInt(kill) != 0) {
				int i1 = r.nextInt(4);
				// Sends vein right.
				if (i1 == 0) {
					createColorVein(g, x + BLOCK_SIZE, y, rVal, gVal, bVal,
							kill - 1, increase);
				} else

				// Sends vein left.
				if (i1 == 1) {
					createColorVein(g, x - BLOCK_SIZE, y, rVal, gVal, bVal,
							kill - 1, increase);
				} else

				// Sends vein up.
				if (i1 == 2) {
					createColorVein(g, x, y - BLOCK_SIZE, rVal, gVal, bVal,
							kill - 1, increase);
				} else

				// Sends vein down.
				{
					createColorVein(g, x, y + BLOCK_SIZE, rVal, gVal, bVal,
							kill - 1, increase);
				}
			}
		}
	}
}

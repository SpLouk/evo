import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

/*
 * A Part represents a part or "piece" of an organism, or may function as
 * 	a "single cell organism" without any neighbours.
 */

public class Part extends Object {

	int[] neighbours = new int[8];
	/**
	 * These are the other parts that this part may be "tied" to. This is how a
	 * creature is made up: smaller parts tie together to form an organism. The
	 * arrangement of the 8 slots is as follows:
	 * 
	 * [0][1][2] [3][p][4] [5][6][7]
	 * 
	 * Where for int i, [i] represents the i-th member of neighbours, and p is
	 * this part.
	 */

	/*
	 * MUTATION_RATE: Lower value = more mutations.
	 */
	static final int FOOD_INIT = 200, LIFETIME = 5000, WASTE_INTERVAL = 1000,
			MUTATION_RATE = 4;

	/*
	 * food = current food level, creature dies if food == 0. life = number of
	 * turns before this Part dies of old age. temp = temperature this Part is
	 * best adapted to. (will survive in climates within +- TEMP_RANGE of temp.
	 * wasteTimer = unused.
	 */
	private int food = FOOD_INIT, life = LIFETIME, temp, tempRange,
			wasteTimer = WASTE_INTERVAL, moveSpeed, climbAbl, vision, wanderX,
			wanderY, wanderAmt;

	/*
	 * foodPath[] = current list of instructions for finding food. Each point in
	 * foodPath[] is either (0, 1) (0, -1) (1, 0), (-1, 0) and represents a
	 * series of valid moves that should be taken towards a Food object.
	 * 
	 * currentGoal is the (x, y) that foodPath leads to. foodID is the position
	 * in objectList of the food goal.
	 */
	Point[] foodPath = new Point[0];
	Point currentGoal = new Point(0, 0);

	BufferedImage texture;
	Color baseColor, secondColor;

	Waste waste;

	/*
	 * DNA = "code" that is passed between parent and child. DNA will determine
	 * certain attributes about a creaure.
	 * 
	 * Form:
	 * 
	 * |012| baseColor = new Color(X, X, X);
	 * 
	 * |345| secondColor = new Color(X, X, X);
	 * 
	 * |6| temp
	 * 
	 * |7| tempRange
	 * 
	 * |8| moveSpeed
	 * 
	 * |9| climbAbl
	 * 
	 * |10| vision
	 */

	final String DNA;

	public Part(Game game, String pDNA) {
		super(game);
		DNA = pDNA;

		intializeCreature();
	}

	public Part(Game game, int x, int y, String pDNA) {
		super(game);
		setCoordinates(x, y);
		DNA = pDNA;

		intializeCreature();
	}

	protected void intializeCreature() {

		int[] ints = DNAtoInts(DNA);

		try {

			baseColor = new Color(ints[0] * 10, ints[1] * 10, ints[2] * 10);
			secondColor = new Color(ints[3] * 10, ints[4] * 10, ints[5] * 10);
			texture = TexturePart.getTexture(baseColor, secondColor);
			temp = ints[6];
			tempRange = ints[7];
			moveSpeed = ints[8];
			climbAbl = ints[9];
			vision = ints[10];

		} catch (IllegalArgumentException e) {
			remove("Bad Mutation");
		}
	}

	@Override
	protected void action() {
		if (!checkForDeath()) {
			life--;
			food--;

			for (int i = 0; i < moveSpeed; i++) {
				boolean moved = findFood();

				if (!moved) {
					moved = wander();
				}

				if (moved)
					food--;
			}

			checkForFood();
			checkForReproduction();

			if (wasteTimer == 0) {
				wasteTimer = WASTE_INTERVAL;
				emitWaste();
			} else {
				wasteTimer--;
			}

		}
	}

	// Vision is multiplied by 10.
	protected int getVision() {
		return vision * 10;
	}

	protected boolean emitWaste() {
		for (int i = -1; i <= 1; i++) {
			for (int i1 = -1; i1 <= 1; i1++) {
				int x = getXCoord() + i, y = getYCoord() + i1;

				if (Game.validPos(x, y) && game.isEmpty(x, y) && waste != null) {
					waste.action(x, y);
					return true;
				}
			}
		}

		return false;
	}

	protected boolean checkForDeath() {
		if (food <= 0) {
			remove("Food");
			return true;
		}

		if (life <= 0) {
			remove("Life");
			return true;
		}

		if (!isTraversable(getXCoord(), getYCoord())) {
			remove("Temp");
			return true;
		}

		return false;
	}

	protected boolean findFood() {

		if (!(game.getObject(currentGoal.x, currentGoal.y) instanceof Food)) {
			foodPath = new Point[0];
		}

		if (foodPath.length != 0 && move(foodPath[0].x, foodPath[0].y)) {

			Point[] temp = new Point[foodPath.length - 1];
			System.arraycopy(foodPath, 1, temp, 0, temp.length);
			foodPath = temp;
			return true;
		}

		if(foodPath.length != 0){
			wanderAmt = 0;
		}
		
		foodPath = newFoodPath();
		return false;
	}

	protected boolean wander() {

		if (wanderAmt == 0) {
			Random r = new Random();
			int i = r.nextInt(4);

			switch (i) {
			case 0:
				wanderX = 1;
				wanderY = 0;
				break;
			case 1:
				wanderX = -1;
				wanderY = 0;
				break;
			case 2:
				wanderX = 0;
				wanderY = 1;
				break;
			case 3:
				wanderX = 0;
				wanderY = -1;
				break;
			}

			wanderAmt = r.nextInt(Game.GAME_SIZE / 2);
		}

		if (move(wanderX, wanderY)) {
			wanderAmt--;
			return true;
		}

		// If movement returns false.
		wanderAmt = 0;
		return false;
	}

	/*
	 * Checks for, and "eats" any food adjacent to this Part.
	 */
	protected void checkForFood() {
		for (int i = -1; i <= 1; i++) {
			for (int i1 = -1; i1 <= 1; i1++) {
				int x = getXCoord() + i, y = getYCoord() + i1;

				if (Game.validPos(x, y) && game.getObject(x, y) instanceof Food) {
					Object possFood = game.getObject(x, y);

					if (possFood instanceof Food) {
						food += ((Food) possFood).getAmount();
						waste = ((Food) possFood).getWaste();

						foodPath = new Point[0];

						possFood.remove();
					}

				}
			}
		}
	}

	protected Point[] newFoodPath() {
		Point[] path = PointArray.getShortest(new PointArray[] {
				searchForFood(getVision(), getXCoord(), getYCoord(),
						getXCoord(), getYCoord(), -1, -1,
						new boolean[Game.GAME_SIZE][Game.GAME_SIZE]),
				searchForFood(getVision(), getXCoord(), getYCoord(),
						getXCoord(), getYCoord(), 1, -1,
						new boolean[Game.GAME_SIZE][Game.GAME_SIZE]),
				searchForFood(getVision(), getXCoord(), getYCoord(),
						getXCoord(), getYCoord(), -1, 1,
						new boolean[Game.GAME_SIZE][Game.GAME_SIZE]),
				searchForFood(getVision(), getXCoord(), getYCoord(),
						getXCoord(), getYCoord(), 1, 1,
						new boolean[Game.GAME_SIZE][Game.GAME_SIZE]) }).p;
		if (path != null) {

			currentGoal = path[path.length - 1];

			for (int i = path.length - 1; i > 0; i--) {
				path[i] = new Point(path[i].x - path[i - 1].x, path[i].y
						- path[i - 1].y);
			}

			Point[] temp = new Point[path.length - 1];
			System.arraycopy(path, 1, temp, 0, temp.length);

			return temp;
		}

		return new Point[0];
	}

	/*
	 * SearchForFood returns a list of distances of food from the current (x, y)
	 * position. The distances are integers < VISION, such that a larger int is
	 * a shorter distance. Expects (x, y) to be a valid coordinate.
	 */
	protected PointArray searchForFood(int vision, int oldX, int oldY, int x,
			int y, int addX, int addY, boolean[][] visited) {

		if (vision > 0 && isTraversable(oldX, oldY, x, y) && !visited[x][y]) {

			vision--;
			visited[x][y] = true;

			if (isDestination(x, y)) {
				return new PointArray(new Point[] { new Point(x, y) });
			} else {
				return new PointArray(new Point(x, y),
						PointArray.getShortest(new PointArray[] {
								searchForFood(vision, x, y, x + addX, y, addX,
										addY, visited),
								searchForFood(vision, x, y, x, y + addY, addX,
										addY, visited) }));
			}
		} else
			return new PointArray();
	}

	protected boolean isDestination(int x, int y) {
		if (!game.isEmpty(x, y)) {
			Object obj = game.getObject(x, y);

			if (obj instanceof Food) {
				return true;
			}
		}

		return false;
	}

	protected void checkForReproduction() {
		if (food > FOOD_INIT * 6) {
			if (isTraversable(getXCoord() + 1, getYCoord())
					&& game.isEmpty(getXCoord() + 1, getYCoord())) {
				reproduce(getXCoord() + 1, getYCoord());
			} else if (isTraversable(getXCoord() - 1, getYCoord())
					&& game.isEmpty(getXCoord() - 1, getYCoord())) {
				reproduce(getXCoord() - 1, getYCoord());
			} else if (isTraversable(getXCoord(), getYCoord() - 1)
					&& game.isEmpty(getXCoord(), getYCoord() - 1)) {
				reproduce(getXCoord(), getYCoord() - 1);
			} else if (isTraversable(getXCoord(), getYCoord() + 1)
					&& game.isEmpty(getXCoord(), getYCoord() + 1)) {
				reproduce(getXCoord(), getYCoord() + 1);
			}
		}
	}

	protected void reproduce(int x, int y) {

		food -= FOOD_INIT;
		game.addToMap(DNA);
		new Part(game, x, y, mutateDNA());
	}

	protected String mutateDNA() {
		Random r = new Random();
		int[] ints = DNAtoInts(DNA);

		if (r.nextInt(MUTATION_RATE) == 0) {

			int index = r.nextInt(ints.length);
			int i = ints[index];

			if (r.nextBoolean())
				i++;
			else
				i--;

			ints[index] = i;
		}

		return intsToDNA(ints);
	}

	protected boolean isTraversable(int x, int y) {
		if (Game.validPos(x, y)) {
			Terrain t = game.terrainMap[x][y];
			int tTemp = t.temp;

			if (tTemp >= temp - tempRange && tTemp <= temp + tempRange) {
				return true;
			}
		}

		return false;
	}

	/*
	 * This version of isTraversable() also accounts for possible inability of
	 * creature to climb a steep slope.
	 */
	protected boolean isTraversable(int oldX, int oldY, int newX, int newY) {
		if (Game.validPos(newX, newY)) {
			Terrain t = game.terrainMap[newX][newY];
			int tTemp = t.temp;

			if (tTemp >= temp - tempRange && tTemp <= temp + tempRange) {

				Terrain oldT = game.terrainMap[oldX][oldY];

				int height1 = oldT.altitude, height2 = t.altitude;

				if (Math.abs(height2 - height1) < climbAbl) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean move(int x, int y) {
		if (isTraversable(getXCoord(), getYCoord(), getXCoord() + x,
				getYCoord() + y)
				&& game.isEmpty(getXCoord() + x, getYCoord() + y)) {

			setCoordinates(getXCoord() + x, getYCoord() + y);
			return true;
		}

		return false;
	}

	@Override
	protected void paint(Graphics g) {

		if (game.zoomLevel > 8) {

			BufferedImage resized = new BufferedImage(game.zoomLevel,
					game.zoomLevel, texture.getType());
			Graphics2D resizedG = resized.createGraphics();
			resizedG.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			resizedG.drawImage(texture, 0, 0, game.zoomLevel, game.zoomLevel,
					0, 0, texture.getWidth(), texture.getHeight(), null);
			resizedG.dispose();

			g.drawImage(resized, getXCoord() * game.zoomLevel, getYCoord()
					* game.zoomLevel, game);

		} else {
			g.setColor(baseColor);
			g.fillRect(getXCoord() * game.zoomLevel, getYCoord()
					* game.zoomLevel, game.zoomLevel, game.zoomLevel);
		}

	}

	protected void remove(String cause) {
		super.remove();
	}

}

/*
 * A PointArray is a class that may or may not contain a Point[]. If isPA is
 * true, there is a Point[] in p, else p is null.
 */
class PointArray {
	final boolean isPA;
	Point[] p;

	public PointArray() {
		isPA = false;
	}

	public PointArray(Point[] point) {
		isPA = true;
		p = point;
	}

	public PointArray(Point point, PointArray pa) {

		if (pa.isPA) {
			isPA = true;
			p = new Point[pa.p.length + 1];
			p[0] = point;
			System.arraycopy(pa.p, 0, p, 1, pa.p.length);
		} else {
			isPA = false;
		}
	}

	/*
	 * Finds the PointArray in the given PointArray[] with the shortest
	 * p.length. Or returns an empty PointArray if all elements of PointArray[]
	 * are empty.
	 */
	public static PointArray getShortest(PointArray[] list) {
		PointArray temp = new PointArray();
		int i1 = getFirstinList(list);

		if (i1 != -1) {

			temp = list[i1];

			for (int i = i1 + 1; i < list.length; i++) {
				if (list[i].isPA && list[i].p.length < temp.p.length) {
					temp = list[i];
				}
			}
		}

		return temp;
	}

	/*
	 * Helper for getShortest(), returns index for the first valid PointArray in
	 * list[], or -1 if not valid PointArray is found.
	 */
	public static int getFirstinList(PointArray[] list) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].isPA) {
				return i;
			}
		}
		return -1;
	}
}

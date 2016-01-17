import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Plant extends Object {

	static final int DEFAULT_ENERGY = 50, SEEDING_DISTANCE = 30,
			LIFETIME = 1000, TEMP_RANGE = 20;

	int energy = DEFAULT_ENERGY;
	int life;
	int temp;

	final String DNA;

	Color leafColor;

	public Plant(Game game, int x, int y, String pDNA) {
		super(game);
		setCoordinates(x, y);
		DNA = pDNA;
		intializePlant();

		if (!isTraversable(x, y)) {
			remove();
		}
	}

	protected void intializePlant() {
		String[] strands = DNA.split(DNA_SPLIT);
		int[] ints = new int[strands.length];

		for (int i = 0; i < strands.length; i++) {
			ints[i] = Integer.parseInt(strands[i]);
		}

		Random r = new Random();
		life = LIFETIME + r.nextInt(LIFETIME);
		temp = ints[3];

		leafColor = new Color(ints[0], ints[1], ints[2]);

	}

	@Override
	protected void action() {

		if (!checkForDeath()) {

			life--;
			energy++;

			if (life == LIFETIME / 2) {

				Random r = new Random();
				if (r.nextBoolean()) {
					reproduce();
				}
			}

			boolean loop = true;
			while (energy > DEFAULT_ENERGY * 2 && loop) {
				loop = growLeaf();
			}
		} else {
			reproduce();
		}

	}

	protected boolean checkForDeath() {

		if (life <= 0) {
			remove();

			return true;
		}

		return false;
	}

	protected boolean growLeaf() {
		for (int i = -1; i <= 1; i++) {
			for (int i1 = -1; i1 <= 1; i1++) {
				if (Game.validPos(getXCoord() + i, getYCoord() + i1)
						&& game.isEmpty(getXCoord() + i, getYCoord() + i1)) {

					new Food(game, getXCoord() + i, getYCoord() + i1, this);
					energy -= DEFAULT_ENERGY;
					return true;
				}
			}
		}

		return false;
	}

	protected void reproduce() {
		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			int x = r.nextInt(SEEDING_DISTANCE*2) - SEEDING_DISTANCE;
			int y = r.nextInt(SEEDING_DISTANCE*2) - SEEDING_DISTANCE;

			if (Game.validPos(getXCoord() + x, getYCoord() + y)
					&& game.isEmpty(getXCoord() + x, getYCoord() + y)) {

				new Plant(game, getXCoord() + x, getYCoord() + y, DNA);
				energy -= DEFAULT_ENERGY * 5;
				return;
			}
		}
	}

	protected boolean isTraversable(int x, int y) {
		if (Game.validPos(x, y)) {
			Terrain t = game.terrainMap[x][y];
			int tTemp = t.temp;

			if (tTemp >= temp - TEMP_RANGE && tTemp <= temp + TEMP_RANGE) {
				
				//Checks that no plants are adjacent
				for (int i = -1; i <= 1; i++) {
					for (int i1 = -1; i1 <= 1; i1++) {
						if (Game.validPos(x + i, y + i1)) {
							Object obj = game.getObject(x + i, y + i1);
							if (obj instanceof Plant && obj != this) {
								return false;
							}
						}
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	protected void paint(Graphics g) {
		g.setColor(new Color(193, 68, 35));
		g.fillRect(getXCoord() * game.zoomLevel, getYCoord() * game.zoomLevel,
				game.zoomLevel, game.zoomLevel);

	}

}

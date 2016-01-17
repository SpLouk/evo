import java.util.Random;

public class GenerateTerrain {

	/*
	 * Amount of smoothing done by algorithm. Larger number = less smoothing.
	 */
	static final int TEMP_DROP = 4, VEIN_SIZE = 20, BLOCK_SIZE = 4,
			TEMP_INCREASE = 1, TEMP_RANGE = 12, MAX_VEINS = 60,
			HEIGHT_RANGE = 12, STEEP_AMT = 4;

	final Game game;
	Random r = new Random();

	public GenerateTerrain(Game g) {
		game = g;
	}

	public void generate() {

		for (int i = 0; i < Game.GAME_SIZE; i++) {
			for (int i1 = 0; i1 < Game.GAME_SIZE; i1++) {
				game.terrainMap[i][i1] = new Terrain(game, i, i1, 0, 0);
			}
		}

		for (int i = 0; i < r.nextInt(MAX_VEINS); i++) {
			int temp = r.nextInt(TEMP_RANGE), addX, addY;
			if (r.nextBoolean()) {
				addX = BLOCK_SIZE;
			} else {
				addX = -BLOCK_SIZE;
			}

			if (r.nextBoolean()) {
				addY = BLOCK_SIZE;
			} else {
				addY = -BLOCK_SIZE;
			}

			createVein(r.nextInt(Game.GAME_SIZE), r.nextInt(Game.GAME_SIZE),
					addX, addY, temp, r.nextInt(VEIN_SIZE - 1) + 1,
					r.nextBoolean());
		}

		for (int i = 0; i < r.nextInt(MAX_VEINS); i++) {
			int temp = -r.nextInt(TEMP_RANGE), addX, addY;
			if (r.nextBoolean()) {
				addX = BLOCK_SIZE;
			} else {
				addX = -BLOCK_SIZE;
			}

			if (r.nextBoolean()) {
				addY = BLOCK_SIZE;
			} else {
				addY = -BLOCK_SIZE;
			}

			createVein(r.nextInt(Game.GAME_SIZE), r.nextInt(Game.GAME_SIZE),
					addX, addY, temp, r.nextInt(VEIN_SIZE - 1) + 1,
					r.nextBoolean());
		}

		for (int i = 0; i < r.nextInt(MAX_VEINS); i++) {
			int height = r.nextInt(HEIGHT_RANGE * 2) - HEIGHT_RANGE;

			createAlt(r.nextInt(Game.GAME_SIZE), r.nextInt(Game.GAME_SIZE),
					height, r.nextInt(STEEP_AMT), r.nextInt(VEIN_SIZE - 1) + 1,
					new boolean[Game.GAME_SIZE][Game.GAME_SIZE]);
		}

	}

	/*
	 * createVein creates a "vein" of temperature in a random location on the
	 * map, automatically smoothing in the process. x and y are the starting
	 * point for the vein. temp is the temperature of the vein. kill is an
	 * accumulator for recursion. Expects 0 < kill increase decides whether temp
	 * should increase as vein progresses.
	 */

	protected void createVein(int x, int y, int addX, int addY, int temp,
			int kill, boolean increase) {

		if (Game.validPos(x, y) && kill > 1) {

			if ((temp > 0 && game.terrainMap[x][y].temp >= 0)
					|| (temp < 0 && game.terrainMap[x][y].temp <= 0)) {
				makeTempBlock(x, y, temp);
				smoothTerrain(x, y);
			}

			if (increase && r.nextInt(20) == 0) {
				if (temp + TEMP_INCREASE <= TEMP_RANGE)
					temp += TEMP_INCREASE;
				else
					increase = false;
			}

			if (!increase && r.nextInt(20) == 0) {
				if (temp - TEMP_INCREASE >= -TEMP_RANGE)
					temp -= TEMP_INCREASE;
				else
					increase = true;
			}

			if (r.nextInt(20) == 0) {
				addX *= -1;
			}
			if (r.nextInt(20) == 0) {
				addY *= -1;
			}

			if (r.nextInt(kill) != 0) {
				/*
				 * int i1 = r.nextInt(4);
				 * 
				 * switch (i1) { case 0: createVein(x + BLOCK_SIZE, y, temp,
				 * kill - 1, increase); break; case 1: createVein(x -
				 * BLOCK_SIZE, y, temp, kill - 1, increase); break; case 2:
				 * createVein(x, y - BLOCK_SIZE, temp, kill - 1, increase);
				 * break; case 3: createVein(x, y + BLOCK_SIZE, temp, kill - 1,
				 * increase); break; }
				 */

				createVein(x + addX, y + addY, addX, addY, temp, kill - 1, increase);
				 createVein(x, y + addY, addX, addY, temp, kill - 1, increase);
			}
		}
	}

	protected void makeTempBlock(int x, int y, int temp) {
		for (int i = -BLOCK_SIZE; i < BLOCK_SIZE; i++) {
			for (int i1 = -BLOCK_SIZE; i1 < BLOCK_SIZE; i1++) {

				int newX = x + i, newY = y + i1;

				if (Game.validPos(newX, newY)) {
					game.terrainMap[newX][newY].setTemperature(temp);
				}
			}
		}
	}

	protected void smoothTerrain(int x, int y) {

		int temperature = game.terrainMap[x][y].temp;
		boolean warm;

		if (temperature > 0) {
			warm = true;
		} else {
			warm = false;
		}

		for (int i = -BLOCK_SIZE; i <= BLOCK_SIZE; i+= BLOCK_SIZE) {
			for (int i1 = -BLOCK_SIZE; i1 <= BLOCK_SIZE; i1+= BLOCK_SIZE) {

				int posX = x + i, posY = y + i1;

				if (Game.validPos(posX, posY)) {
					if (warm
							&& game.terrainMap[posX][posY].temp < temperature
									- TEMP_DROP) {

						makeTempBlock(posX, posY, temperature - TEMP_DROP);

						smoothTerrain(posX, posY);

					} else if (!warm
							&& game.terrainMap[posX][posY].temp > temperature
									+ TEMP_DROP) {

						game.terrainMap[posX][posY].setTemperature(temperature
								+ TEMP_DROP);

						makeTempBlock(posX, posY, temperature + TEMP_DROP);
					}
				}
			}
		}
	}

	protected void createAlt(int x, int y, int height, int steepness, int kill,
			boolean visited[][]) {
		if (Game.validPos(x, y) && kill > 1 && !visited[x][y]) {

			kill--;
			visited[x][y] = true;

			makeAltBlock(x, y, height);

			if (Math.abs(height - steepness) < steepness)
				kill = 1;

			if (height > 0) {
				height -= steepness;
			} else {
				height += steepness;
			}

			createAlt(x + BLOCK_SIZE, y, height, steepness, kill - 1, visited);
			createAlt(x - BLOCK_SIZE, y, height, steepness, kill - 1, visited);
			createAlt(x, y - BLOCK_SIZE, height, steepness, kill - 1, visited);
			createAlt(x, y + BLOCK_SIZE, height, steepness, kill - 1, visited);
		}
	}

	protected void makeAltBlock(int x, int y, int alt) {
		for (int i = -BLOCK_SIZE; i < BLOCK_SIZE; i++) {
			for (int i1 = -BLOCK_SIZE; i1 < BLOCK_SIZE; i1++) {

				int newX = x + i, newY = y + i1;

				if (Game.validPos(newX, newY)) {
					game.terrainMap[newX][newY].setAltitude(alt);
				}
			}
		}
	}
}

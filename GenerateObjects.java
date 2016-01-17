import java.util.Random;

public class GenerateObjects {
	final Game game;
	Random r = new Random();

	public GenerateObjects(Game g) {
		game = g;
	}

	public void generate() {

		for (int i = 0; i < r.nextInt(30) + 30; i++) {
			createPart();
		}

		for (int i = 0; i < r.nextInt(30) + 30; i++) {
			createPlant();
		}
	}

	protected void createPart() {
		int x = r.nextInt(Game.GAME_SIZE);
		int y = r.nextInt(Game.GAME_SIZE);

		String dna = Object.intsToDNA(new int[] { 12, 12, 12, 12, 12, 12,
				game.terrainMap[x][y].temp, 1, 1, 5, 4});

		new Part(game, x, y, dna);
	}

	protected void createPlant() {
		int x = r.nextInt(Game.GAME_SIZE);
		int y = r.nextInt(Game.GAME_SIZE);

		String dna = Object.intsToDNA(new int[] { 0, r.nextInt(156) + 100, 0,
				game.terrainMap[x][y].temp });

		new Plant(game, x, y, dna);
	}

}

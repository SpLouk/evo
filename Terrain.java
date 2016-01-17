import java.awt.Color;
import java.awt.Graphics;

public class Terrain {

	static final int DEF_R = 255, DEF_G = 255, DEF_B = 255, COLOR_CONSTANT = 5;

	int xCoord, yCoord, temp, altitude;
	// An Object's position, expects int between 0 and Game.GAME_SIZE

	Color tileTemp = Color.WHITE, tileAlt = Color.WHITE;

	Game game;

	public Terrain(Game game, int x, int y, int temp, int altitude) {
		this.game = game;
		xCoord = x;
		yCoord = y;
		setTemperature(temp);
		setAltitude(altitude);
	}

	public Terrain(Game game, int x, int y) {
		this.game = game;
		xCoord = x;
		yCoord = y;
		setTemperature(0);
		setAltitude(0);
	}

	void setTemperature(int temp) {
		this.temp = temp;

		if (temp < 0) {
			tileTemp = new Color(DEF_R + (temp*COLOR_CONSTANT), DEF_G + (temp*COLOR_CONSTANT), DEF_B);
		} else {
			tileTemp = new Color(DEF_R, DEF_G - (temp*COLOR_CONSTANT), DEF_B - (temp*COLOR_CONSTANT));
		}

	}

	void setAltitude(int alt) {
		this.altitude = alt;

		tileAlt = new Color(0,
				DEF_G + (((alt - GenerateTerrain.HEIGHT_RANGE) / 2) *COLOR_CONSTANT), DEF_B);
	}

	protected void paint(Graphics g) {
		switch (game.currentView) {
		case Game.VIEW_TEMP:
			g.setColor(tileTemp);
			break;
		case Game.VIEW_ALT:
			g.setColor(tileAlt);
			break;
		}

		g.fillRect(xCoord * game.zoomLevel, yCoord * game.zoomLevel,
				game.zoomLevel, game.zoomLevel);

	}
}

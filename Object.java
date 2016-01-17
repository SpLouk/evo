import java.awt.Graphics;

public abstract class Object {

	static final String DNA_SPLIT = "/";

	private int xCoord, yCoord;
	// An Object's position, expects int between 0 - 255;

	Game game;

	// The Game instance that encapsulates this Object.

	public Object(Game game) {
		this.game = game;
		game.objectListAdd(this);
	}

	protected void setCoordinates(int x, int y) {
		if (Game.validPos(x, y)) {
			game.setEmpty(getXCoord(), getYCoord());

			xCoord = x;
			yCoord = y;

			game.setObject(x, y, this);
		}
	}

	protected int getXCoord() {
		return xCoord;
	}

	protected int getYCoord() {
		return yCoord;
	}

	protected void remove() {
		game.setEmpty(xCoord, yCoord);
		game.objectListRemove(this);
	}

	protected abstract void action();

	protected abstract void paint(Graphics g);

	protected static String intsToDNA(int[] args) {
		String s = "" + args[0];

		for (int i = 1; i < args.length; i++) {
			s += Part.DNA_SPLIT + args[i];
		}

		return s;
	}

	protected static int[] DNAtoInts(String DNA) {
		String[] strands = DNA.split(DNA_SPLIT);
		int[] ints = new int[strands.length];

		for (int i = 0; i < strands.length; i++) {
			ints[i] = Integer.parseInt(strands[i]);
		}
		
		return ints;
	}

}

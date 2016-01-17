
public abstract class Waste {

	Game game;
	
	public Waste(Game game) {
		this.game = game;
	}
	
	protected abstract void action(int x, int y);

}

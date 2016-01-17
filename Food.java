import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Food extends Object {

	private static final int DEFAULT_AMOUNT = 100;

	int amount = DEFAULT_AMOUNT;
	
	Plant plant;
	Color color;

	public Food(Game game, Plant p) {
		super(game);
		plant = p;
		color = plant.leafColor;
	}

	public Food(Game game, int x, int y, Plant p) {
		super(game);
		setCoordinates(x, y);
		plant = p;
		color = plant.leafColor;
	}
	
	public Food(Game game, int x, int y, int amount, Plant p) {
		super(game);
		setCoordinates(x, y);
		this.amount = amount;
		plant = p;
		color = plant.leafColor;
	}

	@Override
	protected void action() {
		if(plant.life == 0){
			remove();
		}
	}

	protected int getAmount() {
		return amount;
	}
	
	protected Waste getWaste(){
		return new WasteSeed(game, plant.DNA);
	}
	
	protected void remove(){
		super.remove();
	}

	@Override
	protected void paint(Graphics g) {
		g.setColor(color);
		g.fillRect(getXCoord() * game.zoomLevel, getYCoord() * game.zoomLevel,
				game.zoomLevel, game.zoomLevel);
	}

}

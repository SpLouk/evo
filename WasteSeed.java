

public class WasteSeed extends Waste {

	final String plantDNA;
	
	public WasteSeed(Game game, String pDNA) {
		super(game);
		
		plantDNA = pDNA;
	}

	protected void action(int x, int y) {
		//new Plant(game, x, y, plantDNA);
	}

}

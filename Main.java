import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JFrame;

public class Main extends Frame {

	static JFrame frame = new JFrame("Evo");
	static Game game = new Game();

	public static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(Game.GAME_SIZE*2, Game.GAME_SIZE*2 + 22));
		frame.setMaximumSize(new Dimension(Game.GAME_SIZE * Game.MAX_ZOOM,
				Game.GAME_SIZE * Game.MAX_ZOOM));

		frame.add(game);
		frame.pack();
		frame.setVisible(true);

		// Display the window.

	}

	public static void main(String args[]) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Main.createAndShowGUI();
			}
		});
	}
}

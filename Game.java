import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

public class Game extends JPanel {

	public static final byte EMPTY_SPACE = -1;
	public static final int GAME_SIZE = 256, KEY_SCROLL_R = 39,
			KEY_SCROLL_L = 37, KEY_SCROLL_UP = 38, KEY_SCROLL_DN = 40,
			KEY_ZOOM_IN = 61, KEY_ZOOM_OUT = 45, KEY_ADVANCE = 32,
			KEY_CHANGE_VIEW = 86, SCROLL_AMOUNT = 4, MIN_ZOOM = 1,
			MAX_ZOOM = 50, VIEW_TEMP = 0, VIEW_ALT = 1;

	public static final int[] LIST_VIEWS = { VIEW_TEMP, VIEW_ALT };

	// Allows for scrolling and zooming.
	int offsetX = 0, offsetY = 0, zoomLevel = 3, currentView = 0;

	HashMap<String, Integer> dnaMap = new HashMap<String, Integer>();

	Terrain[][] terrainMap = new Terrain[GAME_SIZE][GAME_SIZE];
	private Object[][] objectMap = new Object[GAME_SIZE][GAME_SIZE];
	ArrayList<Object> objectList = new ArrayList<Object>();
	GenerateObjects objGen = new GenerateObjects(this);

	public Game() {

		this.setSize(new Dimension(GAME_SIZE * zoomLevel, GAME_SIZE * zoomLevel));

		this.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				formKeyPressed(evt);
			}
		});

		new GenerateTerrain(this).generate();
		objGen.generate();

		this.setFocusable(true);
		this.requestFocusInWindow();

		for (int i = 0; i < objectList.size(); i++) {
			objectList.get(i).action();
		}
	}

	public static boolean validPos(int x, int y) {
		if (0 <= x && 0 <= y && x < GAME_SIZE && y < GAME_SIZE) {
			return true;
		} else {
			return false;
		}
	}

	protected void setObject(int x, int y, Object obj) {
		objectMap[x][y] = obj;
	}

	protected Object getObject(int x, int y) {
		return objectMap[x][y];
	}

	protected void setEmpty(int x, int y) {
		this.objectMap[x][y] = null;
	}

	protected boolean isEmpty(int x, int y) {
		if (objectMap[x][y] == null) {
			return true;
		}

		return false;
	}

	private void formKeyPressed(KeyEvent evt) {

		if (evt.getKeyCode() == KEY_ADVANCE) {
			for (int i = 0; i < objectList.size(); i++) {
				objectList.get(i).action();
			}
		}

		if (evt.getKeyCode() == KEY_SCROLL_L) {
			offsetX += SCROLL_AMOUNT;

		}

		if (evt.getKeyCode() == KEY_SCROLL_R) {
			offsetX -= SCROLL_AMOUNT;

		}

		if (evt.getKeyCode() == KEY_SCROLL_UP) {
			offsetY += SCROLL_AMOUNT;

		}

		if (evt.getKeyCode() == KEY_SCROLL_DN) {
			offsetY -= SCROLL_AMOUNT;

		}

		if (evt.getKeyCode() == KEY_ZOOM_IN && zoomLevel < MAX_ZOOM) {
			zoomLevel++;
		}

		if (evt.getKeyCode() == KEY_ZOOM_OUT && zoomLevel > MIN_ZOOM) {
			zoomLevel--;
		}

		if (evt.getKeyCode() == KEY_CHANGE_VIEW) {
			changeView();
		}

		if (evt.getKeyCode() == 67) {
			try {
				File write = new File("DNA.txt");

				if (!write.exists()) {
					write.createNewFile();
				}

				FileWriter fw = new FileWriter(write);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(dnaMap.toString());
				bw.close();
				System.out.println("Saved");

			} catch (IOException e) {
			}
		}

		repaint();

	}

	protected void changeView() {
		if (currentView < LIST_VIEWS.length - 1) {
			currentView++;
		} else {
			currentView = 0;
		}
	}

	protected void addToMap(String dna) {
		if (dnaMap.containsKey(dna)) {
			Integer i = dnaMap.get(dna);
			dnaMap.put(dna, i + 1);
		} else {
			dnaMap.put(dna, 1);
		}
	}

	protected void objectListAdd(Object obj) {
		objectList.add(obj);
	}

	protected void objectListRemove(Object obj) {
		objectList.remove(obj);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.translate(offsetX * zoomLevel, offsetY * zoomLevel);
		for (int i = 0; i < GAME_SIZE; i++) {
			for (int i1 = 0; i1 < GAME_SIZE; i1++) {
				terrainMap[i][i1].paint(g);
			}
		}

		for (int i = 0; i < objectList.size(); i++) {
			objectList.get(i).paint(g);
		}
		
	}
}

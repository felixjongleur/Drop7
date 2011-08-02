package main;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MainWindow extends Applet implements MouseListener,
		MouseMotionListener, Runnable {

	private static final long serialVersionUID = -2776426312459574519L;

	private Image backBuffer;

	private Graphics backg;

	private int mx, my;

	private int width = 400;
	private int height = 400;

	static int maxDepth = 8;
	private boolean ai = true;
	private boolean newGame = true;
	private boolean loadSequence = false;
	private String loadFileName = "firstSequence92.txt";
	private boolean loadSequenceLoop = false;
	private String levelFileName = "depth92.txt";
	static boolean debug = false;

	private static Image blank;

	private Thread mainWindowThread;
	private static AIThread aiThread;

	private static Board currentGrid;

	@Override
	public void init() {
		setSize(width, height);

		setLayout(null);
		setBackground(Color.darkGray);

		addMouseListener(this);
		addMouseMotionListener(this);

		backBuffer = createImage(width, height);
		backg = backBuffer.getGraphics();

		try {
			currentGrid = new Board(newGame, loadSequence, loadSequenceLoop, loadFileName, levelFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (blank == null) {
			try {
				blank = ImageIO.read(new File("media", "blank.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		if(mainWindowThread == null) {
			mainWindowThread = new Thread(this);
			mainWindowThread.start();
		}
	}
	
	public void stop() {
		mainWindowThread = null;
	}
	
	public void run() {
		while(Thread.currentThread() == mainWindowThread) {
			repaint();
		}
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		mx = e.getX();
		my = e.getY();

		if (currentGrid.getCurrentTile() != null
				&& currentGrid.getCurrentTile().getDrawX() < mx
				&& mx < currentGrid.getCurrentTile().getDrawX() + 40
				&& currentGrid.getCurrentTile().getDrawY() < my
				&& my < currentGrid.getCurrentTile().getDrawY() + 40) {
			currentGrid.getCurrentTile().mousePressed(e);
		}
		e.consume();
	}

	public void mouseReleased(MouseEvent e) {
		if (currentGrid.getCurrentTile() != null && currentGrid.getCurrentTile().isInHand()) {
				currentGrid.pieceHasBeenReleased();
		}

		e.consume();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (currentGrid.getCurrentTile() != null
				&& currentGrid.getCurrentTile().isInHand()) {
			currentGrid.getCurrentTile().mouseDragged(e);
		}
		e.consume();
	}
	
	public Board getCurrentGrid() {
		return currentGrid;
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	public static void updateFromAI(int pos) {
		currentGrid.getCurrentTile().setX(pos);
		currentGrid.pieceHasBeenReleased();
		aiThread = null;
	}
	
	@Override
	public void paint(Graphics g) {
		backg.clearRect(0, 0, width, height);

		if(ai && aiThread == null) {
			aiThread = new AIThread(new Board(currentGrid));
			aiThread.start();
		}
		
		for (int y = 1; y < 8; y++) {
			for (int x = 1; x < 8; x++) {
				backg.drawImage(blank, x * 40, y * 40, this);
			}
		}

		for (NumberTile nt : currentGrid.getGrid()) {
			if (nt != null)
				nt.paint(backg);
		}

		if (currentGrid.getCurrentTile() != null)
			currentGrid.getCurrentTile().paint(backg);

		backg.setColor(Color.WHITE);
		backg.drawString("LEVEL " + currentGrid.getLevel(), 25, 350);
		backg.drawString("SCORE " + currentGrid.getScore(), 275, 350);
		backg.drawString("MAX MULT = " + currentGrid.getMaxMultiplier(), 50, 375);
		backg.drawString("BOARD CLEARED = " + currentGrid.getBoardCleared(), 175, 375);
		
		for(int pos = 0; pos < currentGrid.getTurnsLeft(); pos++) {
			backg.fillRect(40 + (pos * 9), 325, 6, 6);
		}		
		
		g.drawImage(backBuffer, 0, 0, this);
	}
}
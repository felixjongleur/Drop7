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
		MouseMotionListener {

	private static final long serialVersionUID = -2776426312459574519L;

	private Image backBuffer;

	private Graphics backg;

	private int mx, my; // the most recently recorded mouse coordinates

	private int width = 400; // width in # of cells
	private int height = 400; // height in # of cells

	static int maxDepth = 10;
	private boolean ai = false;
	private boolean aiLoop = false;
	private boolean newGame = true;
	private boolean loadSequence = false;
	private String loadFileName = "firstSequence91.txt";
	private boolean loadSequenceLoop = false;

	private static Image blank;

	private AIThread aiThread;

	private Board currentGrid;

	@Override
	public void init() {
		setSize(width, height); // set size of the sketch

		setLayout(null);
		setBackground(Color.darkGray);

		addMouseListener(this);
		addMouseMotionListener(this);

		backBuffer = createImage(width, height);
		backg = backBuffer.getGraphics();

		try {
			currentGrid = new Board(newGame, loadSequence, loadSequenceLoop, loadFileName);
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

		repaint();

		if (ai && !aiLoop) {
			aiThread = new AIThread(currentGrid);
			aiThread.start();
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
		if (currentGrid.getCurrentTile() != null
				&& currentGrid.getCurrentTile().isInHand()) {

			if (!aiLoop)
				currentGrid.pieceHasBeenReleased();
			repaint();

			if (ai) {
				aiThread = new AIThread(currentGrid);
				aiThread.start();
			}

			while (aiLoop) {
				if (aiThread.done) {
					aiThread = new AIThread(currentGrid);
					aiThread.start();
				}
				if (aiThread.getBestPos() != 0) {
					currentGrid.getCurrentTile().setX(aiThread.getBestPos());
					currentGrid.pieceHasBeenReleased();
					repaint();
					aiThread.done = true;
				}
			}
		}

		e.consume();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (currentGrid.getCurrentTile() != null
				&& currentGrid.getCurrentTile().isInHand()) {
			currentGrid.getCurrentTile().mouseDragged(e);
			repaint();
		}
		e.consume();
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Board getCurrentGrid() {
		return currentGrid;
	}

	@Override
	public void paint(Graphics g) {
		backg.clearRect(0, 0, width, height);

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
		backg.drawString("LEVEL " + currentGrid.getLevel(), 25, 375);
		backg.drawString("SCORE " + currentGrid.getScore(), 275, 375);
		
		for(int pos = 0; pos < currentGrid.getTurnsLeft(); pos++) {
			backg.fillRect(40 + (pos * 9), 325, 6, 6);
		}
		
		
		g.drawImage(backBuffer, 0, 0, this);
	}
}
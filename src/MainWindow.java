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
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class MainWindow extends Applet implements MouseListener, MouseMotionListener {

	Image backBuffer;

	Graphics backg;

	int mx, my; // the most recently recorded mouse coordinates

	int width = 400; // width in # of cells
	int height = 400; // height in # of cells
	
	static boolean ai = false;
	static boolean aiLoop = false;
	static boolean debug = true;
	static boolean newGame = true;
	static boolean loadSequence = true;
	static boolean loadSequenceLoop = true;
	
	static Image blank;
	
	static int sequenceIndex = 0;
	static ArrayList<Integer> sequence = new ArrayList<Integer>();
	
    AIThread aiThread;

	Grid currentGrid;
	
	Random generator = new Random();
	
	boolean done = false;

	public void init() {
		setSize(width, height); // set size of the sketch

		setLayout(null);
		setBackground(Color.darkGray);

		addMouseListener(this);
		addMouseMotionListener(this);

		backBuffer = createImage(width, height);
		backg = backBuffer.getGraphics();
		
		try {
			currentGrid = new Grid();
			currentGrid.loadAllNumberTiles();
			if(newGame)
				currentGrid.levelUp();
			else
				currentGrid.loadLevel();
			if(loadSequence)
				loadSequence();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
		
		if(ai && !aiLoop) {
			aiThread = new AIThread(currentGrid);
			aiThread.start();
		}
	/*	currentGrid.printOutBoard();
		Grid temp1 = new Grid(currentGrid);
		temp1.printOutBoard();
		
		NumberTile temp = currentGrid.getNumberTileAtLocation(1, 7);
		temp = currentGrid.getNumberTile(1, 7, 1, 0);
		currentGrid.printOutBoard();
		
		temp1.printOutBoard();*/
	}
	
	public void loadSequence() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("files", "sequence9.txt"));
		
		while (scanner.hasNext()) {
			String str = scanner.next();
			
			if(str.length() == 1) {
				sequence.add(Integer.parseInt(str));
			}
		}			
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		/*mx = e.getX();
		my = e.getY();
		
		if (currentGrid.currentTile.drawY < my && my < currentGrid.currentTile.drawY+40 ) {
			currentGrid.currentTile.mouseClicked(e);
			mouseReleased(e);
		}
		e.consume();*/
	}

	public void mousePressed(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		
		if (currentGrid.currentTile != null && currentGrid.currentTile.drawX < mx && mx < currentGrid.currentTile.drawX+40 && currentGrid.currentTile.drawY < my && my < currentGrid.currentTile.drawY+40 ) {
			currentGrid.currentTile.mousePressed(e);
		}
		e.consume();
	}
	
	public void doSequence() {
		if(sequenceIndex < sequence.size()) {
			System.out.println(sequence.get(sequenceIndex));
	//		System.out.println(currentGrid.currentTile);
	//		System.out.println(currentGrid.currentPieceIndex);
			currentGrid.currentTile.x = sequence.get(sequenceIndex);
			sequenceIndex++;
			currentGrid.pieceHasBeenReleased();
			repaint();
			if(loadSequenceLoop)
				doSequence();
		}
		System.out.println("#level "+currentGrid.level);
		System.out.println("#turnsLeft "+currentGrid.turnsLeft);
		System.out.println("#score "+currentGrid.score);
		System.out.println("#currentPieceIndex "+(currentGrid.currentPieceIndex - 1));
		System.out.println("#raisingIndex "+currentGrid.raisingIndex);
		System.out.println("#grid");
		currentGrid.printBoardForFile();
	}

	public void mouseReleased(MouseEvent e) {
		if(currentGrid.currentTile != null && currentGrid.currentTile.inHand) {
			if(loadSequence) {
				doSequence();
			}
			
			if(!aiLoop && !(sequenceIndex < sequence.size()))
				currentGrid.pieceHasBeenReleased();
		//	System.out.println("Actual Board Score = " + currentGrid.score);
		//	System.out.println("Actual Level = " + currentGrid.level);
		//	System.out.println("Actual Turns Left = " + currentGrid.turnsLeft);	
			repaint();

			if(ai) {
				aiThread = new AIThread(currentGrid);
				aiThread.start();
			}
			
			while(aiLoop) {
				if(aiThread.done) {
					aiThread = new AIThread(currentGrid);
					aiThread.start();
				}	
				if(aiThread.getBestPos() != 0) {
				//	System.out.println(aiThread.getBestPos());
					currentGrid.currentTile.x = aiThread.getBestPos();
					currentGrid.pieceHasBeenReleased();
					aiThread.done = true;
				}				
			}
		//	System.out.println("Turns Left = " + currentGrid.turnsLeft);
		}
		
		e.consume();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if(currentGrid.currentTile != null && currentGrid.currentTile.inHand) {
			currentGrid.currentTile.mouseDragged(e);
			repaint();
		}
		e.consume();
	}
	
	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		backg.clearRect(0, 0, width, height);

		if(blank == null) {
			try {
				blank = ImageIO.read(new File("media", "blank.png"));
			} catch (IOException e) { }
		}
		
		for(int y = 1; y < 8; y++) {
			for(int x = 1; x < 8; x++) {
				backg.drawImage(blank, x * 40, y * 40, this);
			}
		}

		for (NumberTile nt : currentGrid.grid) {
			if(nt != null)
				nt.paint(backg);
		}
		if(currentGrid.currentTile != null)
			currentGrid.currentTile.paint(backg);

		backg.setColor(Color.WHITE);
		backg.drawString("Score = " + currentGrid.score, 10, 350);
		
		g.drawImage(backBuffer, 0, 0, this);
	}
}
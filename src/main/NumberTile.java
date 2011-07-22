package main;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class NumberTile extends Applet {

	private int x, y;
	private int drawX, drawY;
	private int value, unknown;
	private boolean inHand;

	static Image image1, image2, image3, image4, image5, image6, image7;
	static Image[] images = { image1, image2, image3, image4, image5, image6,
			image7 };
	static Image unknown1, unknown2;

	public NumberTile(int x, int y, int value) {
		this.x = x;
		this.y = y;
		this.drawX = x * 40;
		this.drawY = y * 40;

		this.value = value;

		for (int pos = 0; pos < 7; pos++) {
			if (images[pos] == null) {
				try {
					images[pos] = ImageIO.read(new File("media", (pos + 1)
							+ ".png"));
				} catch (IOException e) {
				}
			}
		}
	}

	public NumberTile(int x, int y, int value, int unknown) {
		this(x, y, value);
		this.unknown = unknown;

		if (unknown1 == null) {
			try {
				unknown1 = ImageIO.read(new File("media", "unknown1.png"));
			} catch (IOException e) {
			}
		}

		if (unknown2 == null) {
			try {
				unknown2 = ImageIO.read(new File("media", "unknown2.png"));
			} catch (IOException e) {
			}
		}
	}

	public NumberTile(int value, int unknown, boolean inHand) {
		this(4, 0, value, unknown);
	}

	public void mousePressed(MouseEvent e) {
		inHand = true;
	}
	
	public void mouseDragged(MouseEvent e) {
		int mx = e.getX();

		mx = Math.min(mx, 280);
		mx = Math.max(mx, 40);

		int column = mx / 40;

		x = column;
		drawX = x * 40;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "] Value=>" + value + "| Unknown=>"
				+ unknown + "|";
	}

	@Override
	public void paint(Graphics g) {
		switch (unknown) {
		case 0: {
			g.drawImage(images[value - 1], drawX, drawY, this);
			break;
		}
		case 1: {
			g.drawImage(unknown1, drawX, drawY, this);
			break;
		}
		case 2: {
			g.drawImage(unknown2, drawX, drawY, this);
			break;
		}
		}
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getValue() {
		return value;
	}

	public int getUnknown() {
		return unknown;
	}

	public boolean isInHand() {
		return inHand;
	}

	public int getDrawX() {
		return drawX;
	}

	public int getDrawY() {
		return drawY;
	}
}

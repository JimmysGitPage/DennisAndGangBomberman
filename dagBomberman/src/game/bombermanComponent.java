package game;

import javax.swing.*;
import java.awt.*;
import java.util.AbstractMap;
import java.util.EnumMap;


public class bombermanComponent extends JComponent implements TempClass
{

    private final static int iSqrSize = 40;
    private final static int iAdjust = 15;
    private final static int iCenter = iSqrSize/2;
    private final static int iBombAdjust1 =5;
    private final static int iBombAdjust2 =10;

    private final Map map;
    private final AbstractMap<MapTile, Color> colorMap;

    public bombermanComponent(Map map) {
	this.map = map;

	colorMap = new EnumMap<>(MapTile.class);
	colorMap.put(MapTile.MAP, Color.GREEN);
	colorMap.put(MapTile.UNBREAKABLE, Color.BLACK);
	colorMap.put(MapTile.BREAKABLE, Color.RED);
    }


    public static int getSquareSize() {
	return iSqrSize;
    }


    public static int getSquareMiddle() {
	return iCenter;
    }

    public Dimension getPreferredSize() {
	super.getPreferredSize();
	return new Dimension(this.map.getWidth() * iSqrSize, this.map.getHeight() * iSqrSize);
    }

    public void mapChanged() {
	repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;

	for (int rowIndex = 0; rowIndex < map.getHeight(); rowIndex++) {
	    for (int colIndex = 0; colIndex < map.getWidth(); colIndex++) {
		g2d.setColor(colorMap.get(this.map.getMapTile(rowIndex, colIndex)));
		if(map.getMapTile(rowIndex, colIndex)==MapTile.BREAKABLE){
		    paintBreakableBlock(rowIndex, colIndex, g2d);
		}
		else if(map.getMapTile(rowIndex, colIndex)==MapTile.UNBREAKABLE){
		    paintUnbreakableBlock(rowIndex, colIndex, g2d);
		}
		else{
		    paintMap(rowIndex, colIndex, g2d);
		}
	    }
	}
	// Paint player:
		g2d.setColor(Color.BLUE);
		g2d.fillOval(map.getPlayer().getX()-iAdjust, map.getPlayer().getY()-iAdjust, map.getPlayer().getSize(), map.getPlayer().getSize());

	//Paint enemies
	for (Enemy e: map.getEnemyList()) {
		g2d.setColor(Color.RED);
		g2d.fillOval(e.getX()-iAdjust, e.getY()-iAdjust, e.getSize(), e.getSize());

		g2d.fillOval(e.getX()-iAdjust+4, e.getY()-iAdjust+9, 7, 7);
		g2d.fillOval(e.getX()-iAdjust+19, e.getY()-iAdjust+9, 7, 7);

		g2d.setColor(Color.RED);
		g2d.fillOval(e.getX()-iAdjust+5, e.getY()-iAdjust+10, 5, 5);
		g2d.fillOval(e.getX()-iAdjust+20, e.getY()-iAdjust+10, 5, 5);
	}

	//Paint p-ups
	for (PowerupParent p: map.getPowerupList()) {
	    if (p.getName().equals("PlusBomb")) {
		g2d.setColor(new Color(102, 0, 153));
	    }
		else if (p.getName().equals("BombRadius")) {
		g2d.setColor(new Color(0, 153, 0));
	    }
	    g2d.fillOval(p.getX()-iAdjust, p.getY()-iAdjust, p.getPowerupSize(), p.getPowerupSize());
	}

	//Paint bombs
	for (Bomb b: map.getBombList()) {
	    g2d.setColor(Color.GRAY);
	    int bombX = map.squareToPixel(b.getColIndex());
	    int bombY = map.squareToPixel(b.getRowIndex());
	    g2d.fillOval(bombX + iBombAdjust1, bombY + iBombAdjust1, Bomb.getBOMBSIZE(), Bomb.getBOMBSIZE());
	    g2d.setColor(Color.BLACK);
	    g2d.fillOval(bombX + iBombAdjust2, bombY + iBombAdjust1, iBombAdjust1, iBombAdjust2);
	}

	//Paint explosions
	g2d.setColor(Color.ORANGE);
	for (Explosion tup: map.getExplosionCoords()) {
	    g2d.fillOval(map.squareToPixel(tup.getColIndex()) + iBombAdjust1, map.squareToPixel(tup.getRowIndex()) +
										     iBombAdjust1, Bomb.getBOMBSIZE(), Bomb.getBOMBSIZE());
	}
    }

    private void paintBreakableBlock(int rowIndex, int colIndex, Graphics g2d){
	g2d.setColor(Color.lightGray);
	g2d.fillRect(colIndex * iSqrSize, rowIndex * iSqrSize, iSqrSize, iSqrSize);

    }

    private void paintUnbreakableBlock(int rowIndex, int colIndex, Graphics g2d){
	g2d.fillRect(colIndex * iSqrSize, rowIndex * iSqrSize, iSqrSize, iSqrSize);
    }

    private void paintMap(int rowIndex, int colIndex, Graphics g2d){
	g2d.setColor(Color.white);
	g2d.fillRect(colIndex * iSqrSize, rowIndex * iSqrSize, iSqrSize, iSqrSize);

    }
}

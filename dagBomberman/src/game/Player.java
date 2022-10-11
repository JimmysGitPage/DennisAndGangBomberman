package game;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class Player extends ImageCharacter
{

    private final static int iRight = 60;
    private final static int iTop = 60;
    private final static int PLAYER_PIXELS_BY_STEP = 5;
    private int explosionRadius;
    private int bombCount;
    private Map map;
 
    public Action up = new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	    movePlayer(Move.UP);

	}
    };
 
    public Action right = new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	    movePlayer(Move.RIGHT);

	}
    };
   
    public Action down = new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	    movePlayer(Move.DOWN);

	}
    };
  
    public Action left = new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	    movePlayer(Move.LEFT);

	}
    };

   
    public Action dropBomb = new AbstractAction()
    {
	public void actionPerformed(ActionEvent e) {
	    if(!map.squareHasBomb(getRowIndex(), getColIndex()) && map.getBombListSize() < getBombCount()){
		map.addToBombList(new Bomb(getRowIndex(), getColIndex(), getExplosionRadius()));
	    }
	    map.notifyListeners();
	}
    };

    public Player(bombermanComponent bombermanComponent, Map map) {
	super(iRight, iTop, PLAYER_PIXELS_BY_STEP);
	explosionRadius = 1;
	bombCount = 1;
	this.map = map;
	setPlayerButtons(bombermanComponent);
    }

    public void setPlayerButtons(bombermanComponent bombermanComponent){
	bombermanComponent.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
	bombermanComponent.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
	bombermanComponent.getInputMap().put(KeyStroke.getKeyStroke("UP"), "moveUp");
	bombermanComponent.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
	bombermanComponent.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "dropBomb");
	bombermanComponent.getActionMap().put("moveRight", right);
	bombermanComponent.getActionMap().put("moveLeft", left);
	bombermanComponent.getActionMap().put("moveUp", up);
	bombermanComponent.getActionMap().put("moveDown", down);
	bombermanComponent.getActionMap().put("dropBomb", dropBomb);
    }

    public int getBombCount() {
	return bombCount;
    }

    public void setBombCount(int bombCount) {
	this.bombCount = bombCount;
    }

    public int getExplosionRadius() {
	return explosionRadius;
    }

    public void setExplosionRadius(int explosionRadius) {
	this.explosionRadius = explosionRadius;
    }

    private void movePlayer(Move move) {
	move(move);
	if(map.collisionWithBlock(this)){
	    moveBack(move);
	}
	if(map.collisionWithBombs(this)){
	    moveBack(move);
	}
	if(map.collisionWithEnemies()){
	    map.setIsGameOver(true);
	}

	map.checkIfPlayerLeftBomb();
	map.collisionWithPowerup();
	map.notifyListeners();
    }

}
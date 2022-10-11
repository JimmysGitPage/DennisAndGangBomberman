package game;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class Main
{
    private static final int iIncTime = 30;
    private static int iWidth = 15;
    private static int iHeight = 15;
    private static int iNumEnemies = 7;
    private static Timer tClock = null;

	private static void tick(bombermanFrame frame, Map map) {
		if (map.getIsGameOver()) gameOver(frame, map);

		else {
			map.Enemies();
			map.bombCountdown();
			map.Explosion();
			map.modelExplode();
			map.notifyListeners();}

	}

    public static void Start() {
	Map Tiles = new Map(iWidth, iHeight, iNumEnemies);
	bombermanFrame frame = new bombermanFrame("Bomberman", Tiles);
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Tiles.addMapListener(frame.getBombermanComponent());

	Action doOneStep = new AbstractAction()
	{
	    public void actionPerformed(ActionEvent e) {
		tick(frame, Tiles);
	    }
	};

	tClock = new Timer(iIncTime, doOneStep);
	tClock.setCoalesce(true);
	tClock.start();
    }

    private static void gameOver(bombermanFrame frame, Map Tiles) {
	tClock.stop();
	frame.dispose();
	Start();
    }



	public static void main(String[] args) {
		Start();
	}
}

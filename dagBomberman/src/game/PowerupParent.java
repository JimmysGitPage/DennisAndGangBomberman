package game;


public class PowerupParent
{
    
    private final static int iPupSize = 30;
    private final int x;
    private final int y;
    private String name = null;

    public PowerupParent(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public void addToPlayer(Player player) {
    }

    public int getPowerupSize() {
	return iPupSize;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public String getName() {
	return name;
    }
}

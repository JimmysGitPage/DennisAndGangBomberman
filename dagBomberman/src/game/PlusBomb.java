package game;


public class PlusBomb extends PowerupParent
{

    public PlusBomb(int rowIndex, int colIndex) {
	super(colIndex, rowIndex);
    }

    public void addToPlayer(Player player) {
	int currentBombCount = player.getBombCount();
	player.setBombCount(currentBombCount + 1);
    }

    public String getName() {
	final String name = "PlusBomb";
	return name;
    }
}
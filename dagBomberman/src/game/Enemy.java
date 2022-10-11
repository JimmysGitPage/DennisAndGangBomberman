package game;


public class Enemy extends ImageCharacter
{
    private Move curDirection;

    public Enemy(int x, int y, boolean vertical) {
        super(x, y, 1);
        curDirection = randomDirection(vertical);
    }

    public void changeDirection() {
        if (curDirection == Move.DOWN) {
            curDirection = Move.UP;
        } else if (curDirection == Move.UP) {
            curDirection = Move.DOWN;
        } else if (curDirection == Move.LEFT) {
            curDirection = Move.RIGHT;
        } else {
            curDirection = Move.LEFT;
        }
    }

    public Move getCurrentDirection() {
        return curDirection;
    }

    private Move randomDirection(boolean vertical) {
        assert Move.values().length == 4;
        int pick = (int) (Math.random() * (Move.values().length-2));
        if(vertical) {
            return Move.values()[pick];
        }
        else{
            return Move.values()[pick+2];
        }

    }
}

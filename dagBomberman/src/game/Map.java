package game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Map {

    private final static double rBlockBREAKABLE = 0.4;
    private final static double RPowerUpInRadius = 0.2;
    private final static double rPowerupCounter = 0.8;
    private final MapTile[][] tiles;
    private int iWidth;
    private int iHeight;
    private Collection<TempClass> floorListeners = new ArrayList<>();
    private Player player = null;
    private Collection<Enemy> enemyList = new ArrayList<>();
    private List<Bomb> bombList= new ArrayList<>();
    private Collection<PowerupParent> powerupList = new ArrayList<>();
    private Collection<Bomb> explosionList= new ArrayList<>();
    private Collection<Explosion> explosionCoords= new ArrayList<>();
    private boolean isGameOver = false;

	public void Enemies() {
		if (enemyList.isEmpty()) {
			isGameOver = true;
		}
		for (Enemy e: enemyList){
			ImageCharacter.Move currentDirection = e.getCurrentDirection();

			if (currentDirection == ImageCharacter.Move.DOWN) {
				e.move(ImageCharacter.Move.DOWN);
			} else if (currentDirection == ImageCharacter.Move.UP) {
				e.move(ImageCharacter.Move.UP);
			} else if (currentDirection == ImageCharacter.Move.LEFT) {
				e.move(ImageCharacter.Move.LEFT);
			} else {
				e.move(ImageCharacter.Move.RIGHT);
			}

			if (collisionWithBlock(e)) {
				e.changeDirection();
			}

			if (collisionWithBombs(e)) {
				e.changeDirection();
			}

			if (collisionWithEnemies()) {
				isGameOver = true;
			}
		}
	}

    public static int pixelToSquare(int pixelCoord){
	return ((pixelCoord + bombermanComponent.getSquareSize()-1) / bombermanComponent.getSquareSize())-1;
    }

    public MapTile getMapTile(int rowIndex, int colIndex) {
	return tiles[rowIndex][colIndex];
    }

    public int getWidth() {
	return iWidth;
    }

    public int getHeight() {
	return iHeight;
    }

    public Player getPlayer() {
	return player;
    }

    public Collection<Enemy> getEnemyList() {
	return enemyList;
    }

    public Iterable<Bomb> getBombList() {
	return bombList;
    }

    public int getBombListSize() {
	return bombList.size();
    }

    public Iterable<PowerupParent> getPowerupList() {
	return powerupList;
    }

    public Iterable<Explosion> getExplosionCoords() {
	return explosionCoords;
    }

    public boolean getIsGameOver() {
	return isGameOver;
    }

    public void setIsGameOver(boolean value) {
	isGameOver = value;
    }

    public void addToBombList(Bomb bomb) {
	bombList.add(bomb);
    }

    public void createPlayer(bombermanComponent bombermanComponent, Map floor){
	player = new Player(bombermanComponent, floor);
    }

    public int squareToPixel(int squareCoord){
	return squareCoord * bombermanComponent.getSquareSize();
    }



    public void addMapListener(TempClass bl) {
	floorListeners.add(bl);
    }

    public void notifyListeners() {
	for (TempClass b : floorListeners) {
	    b.mapChanged();
	}
    }

    //Creates bomb
    public void bombCountdown(){
	Collection<Integer> bombIndexesToBeRemoved = new ArrayList<>();
	explosionList.clear();
	int index = 0;
	for (Bomb b: bombList) {
	    b.setTimeToExplosion(b.getTimeToExplosion() - 1);
	    if(b.getTimeToExplosion() == 0){
		bombIndexesToBeRemoved.add(index);
		explosionList.add(b);
	    }
	    index++;
	}
	for (int i: bombIndexesToBeRemoved){bombList.remove(i);}
    }

    public void Explosion(){
	Collection<Explosion> explosionsToBeRemoved = new ArrayList<>();
	for (Explosion e:explosionCoords) {
	    e.setDuration(e.getDuration()-1);
	    if(e.getDuration()==0){
		explosionsToBeRemoved.add(e);
	    }
	}
	for (Explosion e: explosionsToBeRemoved){explosionCoords.remove(e);}

	for (Bomb e: explosionList) {
	    int eRow = e.getRowIndex();
	    int eCol = e.getColIndex();
	    boolean northOpen = true;
	    boolean southOpen = true;
	    boolean westOpen = true;
	    boolean eastOpen = true;
	    explosionCoords.add(new Explosion(eRow, eCol));
	    for (int i = 1; i < e.getExplosionRadius()+1; i++) {
		if (eRow - i >= 0 && northOpen) {
		    northOpen = bombCoordinateCheck(eRow-i, eCol, northOpen);
		}
		if (eRow - i <= iHeight && southOpen) {
		    southOpen = bombCoordinateCheck(eRow+i, eCol, southOpen);
		}
		if (eCol - i >= 0 && westOpen) {
		    westOpen = bombCoordinateCheck(eRow, eCol-i, westOpen);
		}
		if (eCol + i <= iWidth && eastOpen) {
		    eastOpen = bombCoordinateCheck(eRow, eCol+i, eastOpen);
		}
	    }
	}
    }

    public void bmanExplode(){
	for (Explosion tup:explosionCoords) {
	    if(collidingCircles(player, squareToPixel(tup.getColIndex()), squareToPixel(tup.getRowIndex()))){
		isGameOver = true;
	    }
	}
    }

    public void enemyExplode(){
	for (Explosion tup:explosionCoords) {
	    Collection<Enemy> enemiesToBeRemoved = new ArrayList<>();
	    for (Enemy e : enemyList) {
		if(collidingCircles(e, squareToPixel(tup.getColIndex()), squareToPixel(tup.getRowIndex()))){
		    enemiesToBeRemoved.add(e);
		}
	    }
	    for (Enemy e: enemiesToBeRemoved ) {
		enemyList.remove(e);
	    }
	}
    }

    public void modelExplode(){
	bmanExplode();
	enemyExplode();
    }

    private void placeBREAKABLE () {
	for (int i = 0; i < iHeight; i++) {
	    for (int j = 0; j < iWidth; j++) {
		double r = Math.random();
		if (r < rBlockBREAKABLE) {
		    tiles[i][j] = MapTile.BREAKABLE;
		}
	    }
	}
	clearSpawn();
    }

    private void clearSpawn () {
	tiles[1][1] = MapTile.MAP;
	tiles[1][2] = MapTile.MAP;
	tiles[2][1] = MapTile.MAP;
    }

    private void spawnPowerup (int rowIndex, int colIndex) {
	double r = Math.random();
	if (r < RPowerUpInRadius) {
	    powerupList.add(new BombRadiusPU(squareToPixel(rowIndex) + bombermanComponent.getSquareMiddle(), squareToPixel(colIndex) + bombermanComponent.getSquareMiddle()));
	} else if (r > rPowerupCounter) {
	    powerupList.add(new PlusBomb(squareToPixel(rowIndex) + bombermanComponent.getSquareMiddle(), squareToPixel(colIndex) + bombermanComponent.getSquareMiddle()));
	}
    }

    private void placeUnbreakable () {
	for (int i = 0; i < iHeight; i++) {
	    for (int j = 0; j < iWidth; j++) {
		//Makes frame of unbreakable
		if ((i == 0) || (j == 0) || (i == iHeight - 1) || (j == iWidth - 1) || i % 2 == 0 && j % 2 == 0) {
		    tiles[i][j] = MapTile.UNBREAKABLE;
		    //Every-other unbreakable
		} else if (tiles[i][j] != MapTile.BREAKABLE) {
		    tiles[i][j] = MapTile.MAP;
		}
	    }
	}
    }

    private void spawnEnemies (int nrOfEnemies) {
	for (int e = 0; e < nrOfEnemies; e++){
	    while(true) {
		int randRowIndex = 1 + (int) (Math.random() * (iHeight - 2));
		int randColIndex = 1 + (int) (Math.random() * (iWidth - 2));
		if(getMapTile(randRowIndex, randColIndex) != MapTile.MAP){
		    continue;
		}
		if(randRowIndex==1&&randColIndex==1||randRowIndex==1&&randColIndex==2||randRowIndex==2&&randColIndex==1){
		    continue;
		}
		if((randRowIndex % 2)==0){
		    enemyList.add(new Enemy(squareToPixel(randColIndex) + bombermanComponent.getSquareMiddle(), squareToPixel(randRowIndex) + bombermanComponent.getSquareMiddle(), true));
		}
		else{
		    enemyList.add(new Enemy(squareToPixel(randColIndex) + bombermanComponent.getSquareMiddle(), squareToPixel(randRowIndex) + bombermanComponent.getSquareMiddle(), false));
		}
		break;
	    }
	}
    }



    public boolean collisionWithEnemies(){
	for (Enemy enemy : enemyList) {
	    if(collidingCircles(player, enemy.getX()-bombermanComponent.getSquareMiddle(), enemy.getY()-bombermanComponent.getSquareMiddle())){
		return true;
	    }
	}
	return false;
    }

    public boolean collisionWithBombs(ImageCharacter imageCharacter) {
	boolean playerLeftBomb = true;

	for (Bomb bomb : bombList) {
	    if (imageCharacter instanceof Player) {
		playerLeftBomb = bomb.isPlayerLeft();
	    }
	    if(playerLeftBomb && collidingCircles(imageCharacter, squareToPixel(bomb.getColIndex()), squareToPixel(bomb.getRowIndex()))){
		return true;
	    }
	}
	return false;
    }


    public boolean collisionWithBlock(ImageCharacter abstractCharacter){
	for (int i = 0; i < iHeight; i++) {
	    for (int j = 0; j < iWidth; j++) {
		if(getMapTile(i, j) != MapTile.MAP){
		    boolean isIntersecting = squareCircleInstersect(i, j, abstractCharacter);
		    if (isIntersecting) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    public void collisionWithPowerup() {
	for (PowerupParent powerup : powerupList) {
	    if(collidingCircles(player, powerup.getX()-bombermanComponent.getSquareMiddle(), powerup.getY()-bombermanComponent.getSquareMiddle())){
		powerup.addToPlayer(player);
		powerupList.remove(powerup);
		break;
	    }
	}
    }



    public boolean squareHasBomb(int rowIndex, int colIndex){
	for (Bomb b: bombList) {
	    if(b.getRowIndex()==rowIndex && b.getColIndex()==colIndex){
		return true;
	    }
	}
	return false;
    }


    public void checkIfPlayerLeftBomb(){
	for (Bomb bomb: bombList) {
	    if(!bomb.isPlayerLeft()){
		if(!collidingCircles(player, squareToPixel(bomb.getColIndex()), squareToPixel(bomb.getRowIndex()))){
		    bomb.setPlayerLeft(true);
		}
	    }
	}
    }

    private boolean bombCoordinateCheck(int eRow, int eCol, boolean open){
	if(tiles[eRow][eCol] != MapTile.MAP){open = false;}
	if(tiles[eRow][eCol] == MapTile.BREAKABLE){
	    tiles[eRow][eCol] = MapTile.MAP;
	    spawnPowerup(eRow, eCol);
	}
	if(tiles[eRow][eCol] != MapTile.UNBREAKABLE){explosionCoords.add(new Explosion(eRow, eCol));}
	return open;
    }

    private boolean collidingCircles(ImageCharacter abstractCharacter, int x, int y){
	int a = abstractCharacter.getX() - x - bombermanComponent.getSquareMiddle();
	int b = abstractCharacter.getY() - y - bombermanComponent.getSquareMiddle();
	int a2 = a * a;
	int b2 = b * b;
	double c = Math.sqrt(a2 + b2);
	return(abstractCharacter.getSize() > c);
    }

    private boolean squareCircleInstersect(int row, int col, ImageCharacter imgCharacter) {
	//http://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
		int characterX = imgCharacter.getX();
		int characterY = imgCharacter.getY();

		int circleRadius = imgCharacter.getSize() / 2;
		int squareSize = bombermanComponent.getSquareSize();
		int squareCenterX = (col*squareSize)+(squareSize/2);
		int squareCenterY = (row*squareSize)+(squareSize/2);

		int circleDistanceX = Math.abs(characterX - squareCenterX);
		int circleDistanceY = Math.abs(characterY - squareCenterY);

		if (circleDistanceX > (squareSize/2 + circleRadius)) { return false; }
		if (circleDistanceY > (squareSize/2 + circleRadius)) { return false; }

		if (circleDistanceX <= (squareSize/2)) { return true; }
		if (circleDistanceY <= (squareSize/2)) { return true; }

		int cornerDistance = (circleDistanceX - squareSize/2)^2 +
									  (circleDistanceY - squareSize/2)^2;

		return (cornerDistance <= (circleRadius^2));
    }

	public Map(int iWidth, int iHeight, int nrOfEnemies) {
		this.iWidth = iWidth;
		this.iHeight = iHeight;
		this.tiles = new MapTile[iHeight][iWidth];
		placeBREAKABLE();
		placeUnbreakable();
		spawnEnemies(nrOfEnemies);
	}
}
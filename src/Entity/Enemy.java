package Entity;

import TileMap.TileMap;

public class Enemy extends MapObject{
	
	protected  int health, maxHealth, damage;
	protected boolean dead, flinching;
	protected long flinchTimer;
	
	public Enemy(TileMap tm) {
		super(tm);
	}
	
	public boolean isDead() { return dead; }
	
	public int getDamage() { return damage; }
	
	public void hit(int damage) {
		if(dead || flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	public void update() {
		
	}

}

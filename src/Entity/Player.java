package Entity;

import java.awt.Graphics2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.image.BufferedImage;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.GameStateManager;
import GameState.Level1State;
import Main.GamePanel;
import TileMap.TileMap;

public class Player extends MapObject {
	
	private int health, maxHealth, fire, maxFire;
	private boolean dead, flinching;
	private long flinchTimer;
	
	private boolean firing;
	private int fireCost, fireBallDamage;
	private ArrayList<FireBall> fireBalls;

	private boolean gliding;
	
	private boolean restart;

	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
		4, 10, 4, 3, 1, 1
	};
	
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	
	public Player(TileMap tm) {
		
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		health = maxHealth = 5;
		fire = maxFire = 2500;
		
		fireCost = 200;
		fireBallDamage = 5;
		fireBalls = new ArrayList<FireBall>();
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/Player_Sprite.png"));
			
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 6; i++) {
				
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				
				for(int j = 0; j < numFrames[i]; j++) {
					bi[j] = spritesheet.getSubimage(j * width, i * height + 5, width, height - 5);								
				}		
				sprites.add(bi);		
			}	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
	}
	
	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	public int getFire() { return fire; }
	public int getMaxFire() { return maxFire; }
	
	public void setFiring() { 
		firing = true;
	}

	public void setGliding(boolean b) { 
		gliding = b;
	}
	
	public void checkAttack(ArrayList<Enemy> enemies) {	
		for(int i = 0; i < enemies.size(); i++) {		
			Enemy e = enemies.get(i);		
			for(int j = 0; j < fireBalls.size(); j++) {
				if(fireBalls.get(j).intersects(e)) {
					e.hit(fireBallDamage);
					fireBalls.get(j).setHit();
					break;
				}
			}
			if(intersects(e)) {
				hit(e.getDamage());
			}
		}
	}
	
	public void hit(int damage) {
		if(flinching) return;
		health -= damage;
		flinching = true;
		flinchTimer = System.nanoTime();
		if(health < 0) health = 0;
		if(health == 0) {dead = true;}
		
	}

	
	private void getNextPosition() {
		
		if(left) {
			dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		else if(right) {
			dx += moveSpeed;
			if(dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}
			else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}

		if(
		 currentAction == FIREBALL &&
		!(jumping || falling)) {
			dx = 0;
		}

		if(jumping && !falling) {
			dy = jumpStart;
			falling = true;	
		}

		if(falling) {
			
			if(dy > 0 && gliding) dy += fallSpeed * 0.1;
			else dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			
			if(dy > maxFallSpeed) dy = maxFallSpeed;
			
		}
		
	}
	
	public void update() {

		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(currentAction == FIREBALL) {
			if(animation.hasPlayedOnce()) firing = false;
		}
		
		fire += 1;
		if(fire > maxFire) fire = maxFire;
		if(firing && currentAction != FIREBALL) {
			if(fire > fireCost) {
				fire -= fireCost;
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
			}
		}
		
		for(int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).update();
			if(fireBalls.get(i).shouldRemove()) {
				fireBalls.remove(i);
				i--;
			}
		}
		
		if(flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 1000) {
				flinching = false;
			}
		}
		
		if(firing) {
			if(currentAction != FIREBALL) {
				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy > 0) {
			if(gliding) {
				if(currentAction != GLIDING) {
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;
				}
			}
			else if(currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy < 0) {
			if(currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		}
		else if(left || right) {
			if(currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		}
		else {
			if(currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}
		
		animation.update();

		if(currentAction != FIREBALL) {
			if(right) facingRight = true;
			if(left) facingRight = false;
		}
		
	   

		
	}
	
	 public boolean falldown() {
	    	return y > GamePanel.HEIGHT - 15;
	    }
	 public boolean lastscreen() {
	    	return x >= 3170;
	    }
	public void draw(Graphics2D g) {
		
		setMapPosition();
		
		for(int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).draw(g);
		}

		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		super.draw(g);
		
	}
	
}

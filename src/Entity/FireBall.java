package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import TileMap.TileMap;

public class FireBall extends MapObject{
	
	private boolean hit, remove;
	private BufferedImage[] sprites, hitSprites;
	
	public FireBall(TileMap tm, boolean right) {
		
		super(tm);	
		facingRight = right;
		moveSpeed = 3.8;
		if(right) dx = moveSpeed;
		else dx = -moveSpeed;
		width = 30;
		height = 30;
		cwidth = 14;
		cheight = 14;
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/00.png"));
			sprites = new BufferedImage[1];
			sprites[0] = spritesheet.getSubimage(0, 0, width, height);
			
			BufferedImage spritesheet1 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/38.png"));
			hitSprites = new BufferedImage[1];
			hitSprites[0] = spritesheet1.getSubimage(0, 0, width, height);
			
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(70);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void setHit() {
		if(hit) return;
		hit = true;
		animation.setFrames(hitSprites);
		animation.setDelay(70);
		dx = 0;
	}
	
	public boolean shouldRemove() { return remove; }
	
	public void update() {
		
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(dx == 0 && !hit) {
			setHit();
		}
		
		animation.update();
		if(hit && animation.hasPlayedOnce()) {
			remove = true;
		}
		
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g);
	}

}

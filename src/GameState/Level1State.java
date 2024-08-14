package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.sound.midi.VoiceStatus;

import Entity.Enemy;
import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Entity.Enemies.Slugger;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

public class Level1State extends GameState {
	
	private TileMap tileMap;
	private Background bg;
	
	private Player player;
	
	private ArrayList<Enemy> enemies;
	private ArrayList<Explosion> explosions;
	
	private HUD hud;
	
	public Level1State(GameStateManager gsm) {
		this.gsm = gsm;
		init();
	}
	
public void init() {
		
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1-1.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/menubg.jpg", 0.1);
		
		player = new Player(tileMap);
		player.setPosition(100, 100);
		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
		
		hud = new HUD(player);
		
	}
	
	private void populateEnemies() {
		
		enemies = new ArrayList<Enemy>();
		
		Slugger s;
		Point[] points = new Point[] {
			new Point(200, 100),
			new Point(860, 200),
			new Point(1525, 200),
			new Point(1680, 200),
			new Point(1800, 200)
		};
		for(int i = 0; i < points.length; i++) {
			s = new Slugger(tileMap);
			s.setPosition(points[i].x, points[i].y);
			enemies.add(s);
		}
		
	}
	
	public void update() {

		player.update();
		int health = player.getHealth();
		if(health == 0) {
			gsm.setState(GameStateManager.LEVEL1STATE);
		}
		tileMap.setPosition(
			GamePanel.WIDTH / 2 - player.getx(),
			GamePanel.HEIGHT / 2 - player.gety()
		);

		bg.setPosition(tileMap.getx(), tileMap.gety());

		player.checkAttack(enemies);

		for(int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			e.update();
			if(e.isDead()) {
				enemies.remove(i);
				i--;
				explosions.add(
					new Explosion(e.getx(), e.gety()));
			}
		}

		for(int i = 0; i < explosions.size(); i++) {
			explosions.get(i).update();
			if(explosions.get(i).shouldRemove()) {
				explosions.remove(i);
				i--;
			}
		}
		
		if(player.falldown() || player.lastscreen()) {
			gsm.setState(GameStateManager.LEVEL1STATE);
		}
		
	}
	
	public void draw(Graphics2D g) {

		bg.draw(g);
		tileMap.draw(g);
		player.draw(g);

		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}

		for(int i = 0; i < explosions.size(); i++) {
			explosions.get(i).setMapPosition(
				(int)tileMap.getx(), (int)tileMap.gety());
			explosions.get(i).draw(g);
		}
		
		hud.draw(g);
		
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_LEFT) player.setLeft(true);
		if(k == KeyEvent.VK_RIGHT) player.setRight(true);
		if(k == KeyEvent.VK_UP) player.setJumping(true);
		if(k == KeyEvent.VK_DOWN) player.setDown(true);
		if(k == KeyEvent.VK_W) player.setJumping(true);
		if(k == KeyEvent.VK_E) player.setGliding(true);
		if(k == KeyEvent.VK_F) player.setFiring();
	}
	
	public void keyReleased(int k) {
		if(k == KeyEvent.VK_LEFT) player.setLeft(false);
		if(k == KeyEvent.VK_RIGHT) player.setRight(false);
		if(k == KeyEvent.VK_UP) player.setJumping(false);
		if(k == KeyEvent.VK_DOWN) player.setDown(false);
		if(k == KeyEvent.VK_W) player.setJumping(false);
		if(k == KeyEvent.VK_E) player.setGliding(false);
	}
	
}

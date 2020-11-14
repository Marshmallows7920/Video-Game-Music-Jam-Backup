import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Arrays; 
import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Testo extends PApplet {

Game game;
float previousFrameTime;
HashMap<Integer, Boolean> inputMap;


public void setup() {
  
  System.out.println("Step 1 " + this);
  game = new Game(this);
  previousFrameTime = millis();
  inputMap = new HashMap();
  
}

public void draw() {
  
  float currentFrameTime = millis();
  float deltaTime = currentFrameTime - previousFrameTime;
  deltaTime /= 1000.0f;
  previousFrameTime = currentFrameTime;
  game.update(deltaTime);
  clear();
  game.draw();
  
}

  public boolean isKeyDown(char key) {
    if (inputMap.containsKey((int)key)) {
      return inputMap.get((int)key);
    }
    return false;
  }
  
  public void keyPressed() {
    char pressed = Character.toLowerCase(key);
    inputMap.put((int)pressed, true);
  }
  
  public void keyReleased() {
    char pressed = Character.toLowerCase(key);
    inputMap.put((int)pressed, false);
  }
class Background extends GameObject {
  private ArrayList<String> img;
  private PImage imagee;
  private int repeat = 0;
  public Background(Game game, ArrayList<String> bgd) {
    super(0.0f, 0.0f, 0.0f, 0.0f, new int[] {0, 255, 0}, game, bgd);
    img = bgd;
    
    imagee = loadImage(img.get(0)); 
    
  }
  
  public void draw() {
    image(imagee, x, y);
    image(imagee, x + imagee.width, y);
  }
}
class Camera {
  private Game game;
  private float dist = 0;
  private float speed = 1;
  private float range_max = 0;
  private float range_min = 0;
  private ArrayList<GameObject> objectList = new ArrayList<GameObject>();
  
  public Camera(Game game) {
    this.game = game;
    for (GameObject object: game.getObjects()) {
      objectList.add(object);
    }
  }
  
  public void update(float dt) {
    dist += speed;
    
    for (GameObject object : objectList) {
      if (object.toString() != "Player") { 
        if (object.getX() + 400 < -range_max) { 
          
          if (! (object instanceof Background)) {
            object.setX(object.getX() + 400 + range_max + width);
          object.getHitbox().update(object.getX(), object.getY(), object.getWidth(), object.getHeight(), object.getColor());
          }
        }
      }
    }
    
    for (GameObject object : objectList) {
      if (object.toString() != "Player") {
        object.setX(object.getX() - speed);
        if (! (object instanceof Background)) {
          object.getHitbox().update(object.getX(), object.getY(), object.getWidth(), object.getHeight(), object.getColor());
        }
      } else {
        if (object.getX() <= 0) {
          object.setX(0);
        } else if(object.getX() + object.getWidth() >= width) {
          object.setX(width - object.getWidth());
        }
      }
    }
  }
}
class Chi extends Player {
  private boolean detached = false;
  private Player parent;
  private boolean jumping = false;
  private boolean pressed_down = false;
  private PApplet testo;
  public Chi(float x, float y, Player parent, Game game, ArrayList<String> img, PApplet testo) {
    super(x - 10, y, 10.0f, 40.0f, game, img, testo);
    this.parent = parent;
  }
 
  
  public void update(float dt) {
    if (!detached) {
      if (isKeyDown('w')) {
        y = parent.getY() + 10;
      }
      if (isKeyDown('s')) {
       y = parent.getY() - 10;
      }
      if (isKeyDown('a')) {
        x = parent.getX() + 35;
      }
      if (isKeyDown('d')) {
        x = parent.getX() - 20;
      }
      if (jumping) {
        super.jump();
      }
    }
      if (chiTime == 2) {
      chiTime = 0;
      }
      if (isKeyDown('q') && !detached && !pressed_down) {
         //System.out.println("DE");
        pressed_down = true;
        detach();
      } else if (isKeyDown('q') && detached && !pressed_down) {
        //System.out.println("ATTACT");
        pressed_down = true;
        reattach();
      }
      else if (!isKeyDown('q')) {
        pressed_down = false;
      }
      super.update(dt);
    }
    public void detach() {
      detached = true;
      parent.stopSelf(detached);
     // super.update(dt);
    }
    public void reattach() {
      detached = false;
      parent.stopSelf(detached);
     // update(dt);
    }
    

    
   
}
class Door extends GameObject {

  protected Hitbox hitbox;
  private Game game;
  
  public Door(float x, float y, Game game, ArrayList<String> img) {
    super(x, y, 60.0f, 70.0f, new int[] {255, 0, 255}, game, img);
  }
  
  //let the player pass through the door if they have KEY
  public void open(float dt) {
    super.update(dt);
  }
  
  public String toString() {
    return "Door";
  }
  
    public void draw() {
   // this.image.get(0).resize(40, 0);
    image(this.image.get(0), x, y, w, h);
  }
  
}
class Game {
  Background background;
  Camera camera;
  Player player;
  Chi chi;
  int currentLevel;
  
  Platform startButton;
  
  Platform platforma1;
  Platform platforma2;
  Platform platforma3;
  Platform platforma4;
  Platform platformb1;
  Platform platformb2;
  
  Platform platform;
  Platform platform2;
  Platform platform3;
  Platform platform4;
  Platform platform5;
  
  Moving_Platform mvPlatform;
  Moving_Platform mvPlatform2;
  Moving_Platform mvPlatform3;
  Key memoryCore;
  Door portal;
  private ArrayList<Hitbox> allHitboxes;
  private ArrayList<GameObject> allObjects;
  private boolean gameOver = false;
  private boolean levelComplete = false;
  private ArrayList<String> playerImage;
  private ArrayList<String> chiImage;
  private ArrayList<String> backgroundImage;
  private ArrayList<String> coreImage;
  private ArrayList<String> portalImage;
  
  private PImage fall;
  private PImage bull;
  private PImage bull2;
  private PImage end;
  
  private PApplet testo;
  private int levelCompleteDelay = 0;
  
  public Game(PApplet testo) {
    allHitboxes = new ArrayList<Hitbox>();
    allObjects = new ArrayList<GameObject>();
    playerImage = new ArrayList<String>(Arrays.asList("images/Player1.png", "images/Player2.png", "images/Player3.png", "images/Player4.png", "images/Player5.png", "images/Player6.png", "images/Player7.png"));
    chiImage = new ArrayList<String>(Arrays.asList("images/chi1.png", "images/chi2.png", "images/chi3.png", "images/chi4.png", "images/chi5.png", "images/chi6.png", "images/chi7.png", "images/chi8.png", "images/chi9.png", "images/chi10.png", "images/chi11.png", "images/chi12.png", "images/chi13.png", "images/chi14.png", "images/chi15.png"));
    backgroundImage = new ArrayList<String>(Arrays.asList("images/background.png")); //"images/background.png", "images/background.png"));
    coreImage = new ArrayList<String>(Arrays.asList("images/memoryCore.png"));
    portalImage = new ArrayList<String>(Arrays.asList("images/portal.png"));
    bull = loadImage("images/bullying1.png");
    bull2 = loadImage("images/bullying2.png");
    end = loadImage("images/ending.png");

    fall = loadImage("images/falling.png");
    
    this.testo = testo;
    System.out.println("Step 2 " + this.testo);
    
    currentLevel = 0;
    startMenu();
    
    currentLevel = 1;
    levelOne();
    //testMovingPlatform();
  }
  
  public void startMenu() { 
    background = new Background(this, backgroundImage);
  }
  
  public void nextLevel() { 
    currentLevel += 1;
    allHitboxes.clear();
    allObjects.clear();
    
    
    
    if (currentLevel == 2) {
      levelTwo();
    } else if(currentLevel == 3) {
      image(end, 0, 0, width, height);
    }
    levelComplete = false;
  }
  
  public void update(float dt) {
    if (gameOver == false) {
      //if (currentLevel == 1) {
        camera.update(dt);
        player.update(dt);
        chi.update(dt);
        
        
      //  mvPlatform.update(dt);
     // }
     // mvPlatform.update(dt);
     // mvPlatform2.update(dt);
    //  mvPlatform3.update(dt);
    } 
  }
  
  public void addObject(GameObject object) {
    allObjects.add(object);
  }
  
  public void addHitbox(Hitbox hitbox) {
    allHitboxes.add(hitbox);
  }
  
  public ArrayList<GameObject> getObjects() {
    return allObjects;
  }
  
  public ArrayList<Hitbox> getHitboxes() {
    return allHitboxes;
  }
  
  public boolean collision(Hitbox box1, Hitbox box2) {
    boolean inX = box1.getX() + box1.getWidth() >= box2.getX() && box1.getX() <= box2.getX() + box2.getWidth();
    boolean inY = box1.getY() + box1.getHeight() >= box2.getY() && box1.getY() <= box2.getY() + box2.getHeight();
    if (inX && inY) {
      return true;
    } else {
      return false;
    }
  }
  
  public void draw() {
    if (gameOver == false) {
      background.draw();
      for (int i = 0; i < allHitboxes.size(); i += 1) {
        if (allHitboxes.get(i).getInvisible() == false) { 
          if (!levelComplete) {
          allHitboxes.get(i).draw();
          }
        }
      }
    } else {
      //clear();
    }
    
    if (gameOver && !levelComplete) {
      image(fall, 0, 0, width, height);
    }
    
    if (levelComplete) {
      if (currentLevel == 1) {
      image(bull, 0, 0, width, height);
      } else if (currentLevel == 2) {
        image(bull2, 0, 0, width, height);
      } else if (currentLevel == 3) {
        image(end, 0, 0, width, height);
      }
      levelCompleteDelay += 1;
      if (levelCompleteDelay > 100) {
        levelCompleteDelay = 0;
        nextLevel();
      }
    //image(fall, 0, 0);
    }
   
  }
  
  public void gameOver() {
    System.out.println("Game over");
    gameOver = true;
    
  }
  
  public void levelComplete() {
    levelComplete = true;
    
    System.out.println("level is complete");
   // nextLevel();
    
  }
  
  public void levelOne() { 
    
    background = new Background(this, backgroundImage);
    platforma1 = new Platform(width / 4, height / 1.3f, 400, 100, this);
    platforma2 = new Platform(width / 4 + 400, height / 1.3f, 400, 100, this);
    platforma3 = new Platform(width / 4 + 800, height / 1.3f, 400, 100, this);
    platforma4 = new Platform(width / 4 + 1200, height / 1.3f, 400, 100, this);
    
    platformb1 = new Platform(width / 1.5f, height / 1.7f, 300, 25.0f, this);
    platformb2 = new Platform(width / 1.5f + 300, height / 1.7f, 300, 25.0f, this);
    platformb2 = new Platform(width / 1.5f + 600, height / 1.7f, 300, 25.0f, this);
    
    Door door1 = new Door(width/1.5f, height/1.5f, this, portalImage);
    Key key1 = new Key(width/1.5f + 1200, height/1.5f, this, coreImage);
    
   // System.out.println("Step 3 " + this.testo);
    player = new Player(width / 2, height / 2, 30, 50, this, playerImage, this.testo); 
    chi = new Chi(width / 2, height / 2, player, this, chiImage, this.testo); 
    camera = new Camera(this);
  }
  
  public void levelTwo() {
    //allHitboxes = new ArrayList<Hitbox>();
    //allObjects = new ArrayList<GameObject>();
     
    
    platform = new Platform(width/4 + 200, height / 1.3f, 500, 100, this);
    platform2 = new Platform(width/4 + 600, height / 1.3f, 300, 100, this);
    platform3 = new Platform(width/4 + 1200, height / 1.3f, 300, 100, this);
    
    platform4 = new Platform(width / 1.5f + 1000, height / 1.7f, 200, 25.0f, this);
    platform5 = new Platform(width / 1.5f + 400, height / 1.7f, 200, 25.0f, this);
    Door door1 = new Door(width/1.5f, height/1.5f, this, portalImage);
    Key key1 = new Key(width/1, height/2.1f, this, coreImage);
    player = new Player(width / 2, height / 2, 30, 50, this, playerImage, testo);
    chi = new Chi(width / 2, height / 2, player, this, chiImage, this.testo); 
    camera = new Camera(this);
  }
  
  public void testMovingPlatform() { 
   /* camera = new Camera(this);
    background = new Background(this, backgroundImage);
    player = new Player(width / 2, height / 2, 30.0, 50.0, this, playerImage, testo); 
    chi = new Chi(width / 2, height / 2, player, this, chiImage, testo);
    platform = new Platform(width / 4, height / 1.3, 100000, 500, this);
    platform4 = new Platform(width / 1.5, height / 1.7, 10000, 25.0, this);
    Door door1 = new Door(width/0.75, height/1.5, this, portalImage);
    Key key1 = new Key(width/1, height/2.1, this, coreImage);
    mvPlatform = new Moving_Platform((float) width / 2, (float) height / 2, (float) 100, (float) 100, 4, 100.0, this);
  //  Lever lever1 = new Lever(width/1, height/1.8, mvPlatform, this);
    player = new Player(width / 2, height / 2, 30, 50, this, playerImage, testo); 

    
    platform = new Platform(width / 4, height / 1.3, 100000, 500, this);
    mvPlatform = new Moving_Platform(width / 1.5, height / 1.7, 100, 20, 2, 100, this);*/
  }
}
class GameObject {
  protected float x;
  protected float y;
  protected float w;
  protected float h;
  protected int[] colors;
  protected Game game;
  protected Hitbox hitbox;
  protected ArrayList<PImage> image;
  
  public GameObject(float x, float y, float w, float h, int[] colors, Game game, ArrayList<String> img) {
    this.image = new ArrayList<PImage>();
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.colors = colors;
    this.game = game;
    for (String image : img) {
      this.image.add(loadImage(image));
    }
    
    if (! (this instanceof Background)) {
      this.hitbox = new Hitbox(this.x, this.y, this.w, this.h, this.colors, this.game, this);
    }
    
    this.game.addObject(this);
  }
  
  public void update(float dt) {
    hitbox.update(x, y, w, h, colors);
  }
  
  public float getX() {
    return x;
  }
  public void setX(float x) {
    this.x = x;
  }
  public float getY() {
    return y;
  }
  public void setY(float y) {
    this.y = y;
  }
  public float getWidth() {
    return w;
  }
  public void setWidth(float w) {
    this.w = w;
  }
  public float getHeight() {
    return h;
  }
  public void setHeight(float h) {
    this.h = h;
  }
  public int[] getColor() {
    return colors;
  }
  public Hitbox getHitbox() {
    return hitbox;
  }
}
class Hitbox {
  private float x;
  private float y;
  private float w;
  private float h;
  private int[] colors;
  private Game game;
  private GameObject parent;
  boolean invisible = false;
  
  public Hitbox(float x, float y, float w, float h, int[] colors, Game game, GameObject parent) {
    this.game = game;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.colors = colors;
    this.parent = parent;
    
    this.game.addHitbox(this);
  }
  
  public Object getParent() {
    return parent;
  }
  
  public float getX() {
    return x;
  }
  public float getY() {
    return y;
  }
  public float getWidth() {
    return w;
  }
  public float getHeight() {
    return h;
  }
  public boolean getInvisible() {
    return invisible;
  }
  public void setInvisible(boolean b) { 
    invisible = b;
  } 
  
  public void draw() {
    fill(colors[0], colors[1], colors[2]);
    if (!(parent instanceof Key)) {
   // rect(x, y, w, h);
    }
    if (parent instanceof Platform) {
      ((Platform) parent).drawPlatform();
    } if (parent instanceof Player) {
      ((Player) parent).draw();
    } if (parent instanceof Key) {
      ((Key) parent).draw();
    } if (parent instanceof Door) {
      ((Door) parent).draw();
    }
  }
  
  public void update(float x, float y, float w, float h, int[] colors) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.colors = colors;
  }
}
class Key extends GameObject {

  protected Hitbox hitbox;
  private Game game;
  
  public Key(float x, float y, Game game, ArrayList<String> img) {
    super(x, y, 20, 20, new int[] {0, 255, 255}, game, img);
  }

  public String toString() {
    return "Key";
  }
  
  public void draw() {
  //  this.image.get(0).resize(17, 0);
    image(this.image.get(0), x, y, w, h);   
  }
  
}
class Lever extends GameObject {

  protected Hitbox hitbox; 
  private Game game;
  protected Moving_Platform child;
  private boolean on;
  
  public Lever(float x, float y, Moving_Platform child, Game game) {
    super(x, y, 10, 10, new int[] {100, 100, 255}, game, new ArrayList<String>(Arrays.asList("i")));
    this.child = child;
    this.on = false;
  }

  public String toString() {
    return "Lever";
  }
  
  public void flip() {
    this.on = !this.on;
  }
  
  public boolean status() {
    return this.on;
  }
  
  public Moving_Platform getChild() {
    return this.child;
  }
  
}
class Moving_Platform extends Platform {

  
  private float speed = 200.0f;
  //platform moves in a direction (0-7), 0 being north, 1 being north east, ... etc,  
  private int direction = 0;
  //# of calls to update before the platform turns around, 0 is default and signifies no turnback
  private float turnback = 0;
  private int timesteps = 0;

  private Game game;
  
  public Moving_Platform(float x, float y, float w, float h, int direction, float turnback, Game game) {
    super(x,y,w,h, game);
    this.direction = direction;
    this.turnback = turnback;
  }
  
  //override
  public void update(float dt) {
    timesteps += 1;
    if (turnback != 0 && timesteps == turnback) {
      speed = -speed;
      timesteps = 0;
    }
    if (direction == 0) {
      y += speed * dt;
    }
    if (direction == 1) {
      y += 0.5f * speed * dt;
      x += 0.5f * speed * dt;
    }
    if (direction == 2) {
      x += speed * dt;
    }
    if (direction == 3) {
      x += speed * dt;
      y -= 0.5f * speed * dt;
    }
    if (direction == 4) {
      y -= speed * dt;
    }
    if (direction == 5) {
      y -= 0.5f * speed * dt;
      x -= 0.5f * speed * dt;
    }
    if (direction == 6) {
      x -= speed * dt;
    }
    if (direction == 7) {
      x -= 0.5f * speed * dt;
      y += 0.5f * speed * dt;
    }
    super.update(dt);
  }
  
  public String toString() {
    return "Moving_Platform";
  }
  
}
class Platform extends GameObject {

  protected Hitbox hitbox;
  private Game game;
  
  public Platform(float x, float y, float w, float h, Game game) {
    super(x, y, w, h, new int[] {0, 0, 255}, game, new ArrayList<String>(Arrays.asList("images/platform.png")));
  }
  
  //update the position of the platform over time (but only implemented for moving platform...)
  public void update(float dt) {
    super.update(dt);
  }
  
  public void drawPlatform() {
   for (PImage img : image) {
     image(img, x, y - h / 2, w, h);
   }
  }
  
  public String toString() {
    return "Platform";
  }
  
}



class Player extends GameObject {
  private boolean hasKey = false;
  private float accely_scale = 0.6f;     //speed of falling
  private float accely = accely_scale;
  private float max_accel = 150;        //max accel of gravity (?)
  private float xvelo = 200.0f;
  private float yvelo = 11.0f;           //jumping speed
  private float time = 0;
  public float chiTime = 0;
  private boolean touches = false;
  private boolean jumping = false;
  private boolean ready_to_jump = false;
  private boolean hasChi = true;
  private boolean isWalking = false;
  private boolean walkingLeft = false;
  private boolean justLanded = false;
  private float diff_x = 0;
  private float diff_y = 0;
  private int frame = 0;
  private int frameRate = 0;
  private SoundFile walking_sound;
  private SoundFile jumping_sound;
  private SoundFile landing_sound;
  private SoundFile core_sound;
  private SoundFile portal_sound;
  private PApplet testo;
  private int frameRateChi = 0;
  private boolean landed = false;
  ArrayList<Hitbox> hitboxList = new ArrayList<Hitbox>();
  boolean constructChi = true;
  public Player(float x, float y, float w, float h, Game game, ArrayList<String> img, PApplet testo) { 
    super(x, y, w, h, new int[] {0, 255, 0}, game, img);
    this.hasKey = false;
    this.testo = testo;
    for (Hitbox hitbox: game.getHitboxes()) {
      hitboxList.add(hitbox);
    }
    System.out.println("Step 5 " + testo);
    walking_sound = new SoundFile(testo, "Sounds/footstep2.mp3");
    walking_sound.amp(0.2f);
    jumping_sound = new SoundFile(testo, "Sounds/jump.mp3");
    jumping_sound.amp(0.2f);
    landing_sound = new SoundFile(testo, "Sounds/landing.mp3");
    landing_sound.amp(0.1f);
    core_sound = new SoundFile(testo, "Sounds/memorycore.mp3");
    core_sound.amp(0.07f);
    portal_sound = new SoundFile(testo, "Sounds/portal.mp3");
    portal_sound.amp(0.07f);
  }
  
  public void update(float dt) {
    isWalking = false;
    walkingLeft = false;
    chiTime += 1;
    touches = false;
    ready_to_jump = false;
   // System.out.println(y);
    if (y >= height) {
      game.gameOver();
    }
    
    if (hasChi) {
      
      if (isKeyDown('w')) {
       // y -= accely * dt;
      }
      if (isKeyDown('s')) {
       // y += accely * dt;
      }
      if (isKeyDown('a')) {
        x -= xvelo * dt;
        isWalking = true;
        walkingLeft = true;
      }
      if (isKeyDown('d')) {
        x += xvelo * dt;
        isWalking = true;
      }
    } 
    
    
    

    for (Hitbox hitbox: hitboxList) {
      if (!(hitbox.getParent() instanceof Player)) {
        if (game.collision(this.hitbox, hitbox)) {
          if (hitbox.getParent() instanceof Key) {
            this.hasKey = true;
            core_sound.play();
            hitbox.setInvisible(true);
            System.out.println(hasKey);
          } 
          else if (hitbox.getParent() instanceof Door) { 
            if (hasKey == true) {
              portal_sound.play();
              game.levelComplete();
            }
          } 
          else if (hitbox.getParent() instanceof Moving_Platform && whichOrientation(this.hitbox, hitbox) == "top") {   
            touches = true;
            String o = whichOrientation(this.hitbox, hitbox);
            stopPlayer(o, dt, hitbox);
            if (justLanded == false || isWalking) {
              float curr_x = this.getX();
              float curr_y = this.getY();
              diff_x = curr_x - hitbox.getX();
              diff_y = curr_y - hitbox.getY();
            }
            justLanded = true;
            // System.out.println(justLanded);
            this.setX(hitbox.getX() + diff_x);
            this.setY(hitbox.getY() + diff_y);
          }
          else {
            if (!landed && !(this instanceof Chi)) {
              landing_sound.play();
            }
            landed = true;
            touches = true;
            // gravity(dt);
            String o = whichOrientation(this.hitbox, hitbox);
            stopPlayer(o, dt, hitbox);
          }
        }
        }
      }
    if (!touches) {
      landed = false;
      justLanded = false;
      xvelo = 200.0f;
      accely = accely_scale;
    }
    if (!ready_to_jump) {
      time += 1;
    } 
    if (!hasChi) {
      if (this instanceof Player) {
       // System.out.println("MainPlayer stopped");
      }
    }
    if (hasChi) {
      if (this instanceof Player) {
        //System.out.println("MainPlayer can move");
      }
    }
    if (jumping) {
      jump();
    }
    if (chiTime == 2) {
    chiTime = 0;
    }
    gravity(dt);
    super.update(dt);
  }

  
  public void pickup() {
    this.hasKey = true;
  }   
  
  public void stopSelf(boolean detached) {
    if (detached) {
      hasChi = false;
    } else {
      hasChi = true;
    }
    //gravity(dt);
    //super.update(dt);
  }
  
  private String whichOrientation(Hitbox box1, Hitbox box2) {
    //player on top
    if (box1.getY() < box2.getY() && box1.getX() + box1.getWidth() >= box2.getX() 
    && box1.getX() <= box2.getX() + box2.getWidth()) {
      return "top";
    }
    //player on bottom
    else if (box1.getY() + box1.getHeight() > box2.getY() + box2.getHeight() 
    && box1.getX() >= box2.getX() && box1.getX() + box1.getWidth() <= box2.getX() + box2.getWidth()) {
      return "bottom"; 
    } 
    //player on left
    else if (box1.getX() < box2.getX()) {
      return "left";
    }
    //player on right
    else {
      return "right";
    }
  }
  
  private void gravity(float dt) {
    if (accely / 2 * time < max_accel) {
       y += accely / 2 * time;
    } else {
      y += max_accel;
    }
  }
  
  private void stopPlayer(String orientation, float dt, Hitbox object) {
    if (orientation.equals("top")) {
      accely = 0;
      time = 0;
      jumping = false;
      ready_to_jump = true;
      y = object.getY() - h;
      if (isKeyDown('w') && hasChi) {
        jumping_sound.play();
        accely = accely_scale;
        jumping = true;
      }
    } if (orientation.equals("left")) {
      if (x <= 0) {
        game.gameOver();
      }
     // xvelo = 0;
      x = object.getX() - w;
      if (isKeyDown('a')) {
        xvelo = 200;
      }
    } if (orientation.equals("right")) {
      x = object.getX() + object.getWidth();
      if (x + w >= width) {
        game.gameOver();
      }
      //xvelo = 0;
      if (isKeyDown('d')) {
        xvelo = 200;
      }
    } else if (orientation.equals("bottom")){
      jumping = false;
    } else {
      //System.out.println("YOU DIE.... (please try again! : ) )");
    }
  }
  
  private void jump() {
    // This is the full formula, which is correct, but since you're doing += and not =, this is actually y = y - (yInitial - yvelo + (.5 * accely * dt)),
    //which isn't what we want. We could solve this 2 ways: One, only update y one time in this whole program, using the formula you wrote below but with = and not -=, or
    //The second way, which is to just add each component to y separately, is what I'll do below. It's simpliest since you are already adding the gravity to y separately,
    //so all you'll need to modify is this line below:
    //old way: y -= yInitial - yvelo + (.5 * accely * dt);
    y -= yvelo;
  }
  
  
  public String toString() {
    return "Player";
  }
  
  public void draw() {
    if (walkingLeft) {
      pushMatrix();
      scale(-1.0f, 1.0f);
      image(this.image.get(frame % this.image.size()), -x - w, y, w, h);
      popMatrix();
    } else {
      image(this.image.get(frame % this.image.size()), x, y, w, h);
    }
    if (isWalking || !(this instanceof Chi)) { 
      frameRate += 1;
      if (frameRate >= 10) {
        if (!walking_sound.isPlaying() && !(this instanceof Chi)) {
          if (touches) {
            walking_sound.play();
          }
        }
        frameRate = 0;
        frame += 1;
      }
    } if (this instanceof Chi) {
      frameRateChi += 1;
      if (frameRateChi >= 5) {
        frameRateChi = 0;
        frame += 1;
      }
    }
  }
}
  
  
  public void settings() {  size(1280, 720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Testo" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

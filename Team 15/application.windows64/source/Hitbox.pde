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

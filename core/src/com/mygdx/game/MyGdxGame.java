package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.RenderHelper;
import com.mygdx.game.ResourceManager;


//useful info here
//https://www.badlogicgames.com/forum/viewtopic.php?f=11&t=10173
//http://www.pixnbgames.com/blog/libgdx/how-to-use-libgdx-tiled-several-layers/

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
    //Texture img;
    TiledMap tiledMap;
    OrthographicCamera camera;
    OrthographicCamera screen;
    
    TiledMapRenderer tiledMapRenderer;
    
    Viewport view;
    
    SpriteBatch _spriteBatch;
        
    
    Sprite sp;
    Sprite window; 
    Sprite tab;
    
    
    int winx;
    int winy;
    
    BitmapFont font;         
    //TiledMapImageLayer obj;
    
    int sx;
    int sy;
    
    int TARGET_WIDTH=480;
    int TARGET_HEIGHT=270;
    //int TARGET_WIDTH=384;
    //int TARGET_HEIGHT=216;    
    //int TARGET_WIDTH=320;
    //int TARGET_HEIGHT=180;
    
    int mapWidth;
    int mapHeight;
    
    float plrx, plry;
    ShapeRenderer debugRenderer;
    
    Polyline polyline;
    
    @Override
    public void create () {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        Gdx.graphics.setWindowedMode(TARGET_WIDTH*3, TARGET_HEIGHT*3);
        
        debugRenderer = new ShapeRenderer();
        
        //_spriteBatch = ResourceManager.GetSpriteBatch();
        _spriteBatch = new SpriteBatch();
 
        //sp = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        window = new Sprite(new Texture(Gdx.files.internal("mapwindow.png")));
        tab = new Sprite(new Texture(Gdx.files.internal("tab.png")));
        font = new BitmapFont(); 
        
        sx=0;
        sy=0;
        winx=0;
        winy=0;
        
        plrx=176;
        plry=96;
        
        camera = new OrthographicCamera();
        //camera.setToOrtho(false,w,h);
        camera.setToOrtho(false,TARGET_WIDTH,TARGET_HEIGHT);
        camera.update();
        
        screen = new OrthographicCamera();
        //camera.setToOrtho(false,w,h);
        screen.setToOrtho(false,TARGET_WIDTH,TARGET_HEIGHT);
        screen.update();
        

        view = new FitViewport(TARGET_WIDTH, TARGET_HEIGHT);
        

        
        tiledMap = new TmxMapLoader().load("dockingbay2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        MapLayers layers = tiledMap.getLayers();     
        Iterator<MapLayer> layersIter = layers.iterator(); 
        
        
        //polyline = ((PolylineMapObject)o).getPolyline();
        
        while(layersIter.hasNext()) {
            MapLayer layer = layersIter.next();
            if(layer.getName().equals("Obstructions")) {
                MapObjects os = layer.getObjects();
                Iterator<MapObject> osIter = os.iterator();
                while(osIter.hasNext()) {
                    MapObject o = osIter.next();
                    polyline = ((PolylineMapObject)o).getPolyline();
                }
            }
        }
        
        
        
        
        MapProperties prop = tiledMap.getProperties();
        
        
        mapWidth = prop.get("width", Integer.class) * 16;
        mapHeight = prop.get("height", Integer.class) * 16;
        
        
        //MapLayers mapLayers = tiledMap.getLayers();

        //obj =  (TiledMapImageLayer) mapLayers.get("BG2");
        
        //bg = ResourceManager.GetTexture("bg_docking_bay.png");
        
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {
        view.update(width, height);
    }
    
    @Override
    public void render () {
    	
    	
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        	plrx-=4;        	       
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        	plrx+=4;        	        
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
        	plry+=4;        	       
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
        	plry-=4;        	        
        }
        
        tab.setPosition(plrx, plry);
        
        float camerax=plrx+32;
        float cameray=plry+32;
        if(camerax - (TARGET_WIDTH/2) < 0) camerax=TARGET_WIDTH/2;
        if(camerax +(TARGET_WIDTH/2) > mapWidth) camerax=mapWidth - (TARGET_WIDTH/2);
        
        if(cameray - (TARGET_HEIGHT/2) < 0) cameray=TARGET_HEIGHT/2;
        if(cameray +(TARGET_HEIGHT/2) > mapHeight) cameray=mapHeight - (TARGET_HEIGHT/2);
        
        
        
        camera.position.set(camerax, cameray, 0);
        camera.update();
        tiledMapRenderer.setView(camera);
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        

        
                
        
        tiledMapRenderer.render(); //todo - proper render order of all the layers with sprites in between
             
        _spriteBatch.begin();
        _spriteBatch.setProjectionMatrix(camera.combined);
		        
     
        
        tab.draw(_spriteBatch);
        //float fh=font.getLineHeight();
        
        
        _spriteBatch.end();

        //last layer, everything here drawn in screen coords. UI elements, etc
        _spriteBatch.begin();
        _spriteBatch.setProjectionMatrix(screen.combined);
        
        
		window.setPosition(TARGET_WIDTH-window.getWidth()+winx, TARGET_HEIGHT-window.getHeight()+winy);
        
        window.draw(_spriteBatch);
        
        font.draw(_spriteBatch, "Screen w: "+Float.toString(w) + " h: " + Float.toString(h), 0, TARGET_HEIGHT+winy);
        font.draw(_spriteBatch, "Target w: "+Integer.toString(TARGET_WIDTH) + " h: " + Integer.toString(TARGET_HEIGHT), 0, TARGET_HEIGHT-20+winy);
        font.draw(_spriteBatch, "camerax: "+Float.toString(camerax) + " y: " + Float.toString(cameray), 0, TARGET_HEIGHT-40+winy);
        
        float[] verts = polyline.getVertices();
        
        DrawLine(new Vector2(10, 10), new Vector2(20, 20), 3, Color.YELLOW, screen.combined);
        
        _spriteBatch.end();
        
        
    }

    @Override
    public boolean keyDown(int keycode) {
    	/*
        if(keycode == Input.Keys.LEFT & winx>0) {
        	
            camera.translate(-16,0);
            winx -= 16;
        }
        if(keycode == Input.Keys.RIGHT & winx<mapWidth-TARGET_WIDTH) {
            camera.translate(16,0);
            winx += 16;
        }
        if(keycode == Input.Keys.UP & winy<mapHeight-TARGET_HEIGHT) {
            camera.translate(0,16);
            winy += 16;
        }
        if(keycode == Input.Keys.DOWN & winy>0) {
            camera.translate(0,-16);
            winy -= 16;
        }
        */
        if(keycode == Input.Keys.NUM_1)
            tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
        if(keycode == Input.Keys.NUM_2)
            tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
        if(keycode == Input.Keys.NUM_3) {
        	Gdx.graphics.setWindowedMode(TARGET_WIDTH*3, TARGET_HEIGHT*3);
        }
        if(keycode == Input.Keys.NUM_4) {
            Gdx.graphics.setWindowedMode(TARGET_WIDTH*2, TARGET_HEIGHT*2);
            }
        if(keycode == Input.Keys.NUM_5) {

        	   Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
               Gdx.graphics.setFullscreenMode(displayMode);
            }
        if(keycode == Input.Keys.NUM_6) {

        	Gdx.graphics.setWindowedMode(1280, 720);
         }
        if(keycode == Input.Keys.PLUS) {
        	   sx-=16;
        	   sy-=9;
  
               camera.setToOrtho(false,TARGET_WIDTH+sx,TARGET_HEIGHT+sy);
               camera.update();
        	   
          }
         if(keycode == Input.Keys.ESCAPE) {
        	 
    	   dispose();
         }

        return false;
    }

	@Override
	public void dispose () {
	   
       tiledMap.dispose();   	 
       
       font.dispose();
       //_spriteBatch.dispose();
       
       
  	   Gdx.app.exit(); 
	}     
    
    @Override
    public boolean keyUp(int keycode) {

        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
    

    public void DrawLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public void DrawLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(2);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
    
}



/*
 * 
 * public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {

public OrthogonalTiledMapRendererWithSprites(TiledMap map) {
    super(map);
}
@Override
public void renderObject(MapObject object) {
    if(object instanceof TextureMapObject) {
        TextureMapObject textureObj = (TextureMapObject) object;
        batch.draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY(),
                textureObj.getOriginX(), textureObj.getOriginY(), textureObj.getTextureRegion().getRegionWidth(), textureObj.getTextureRegion().getRegionHeight(),
                textureObj.getScaleX(), textureObj.getScaleY(), textureObj.getRotation());
       if(textureObj.getProperties().containsKey("this")) System.out.println(textureObj.getRotation());
    } else if(object instanceof RectangleMapObject){
        RectangleMapObject rectObject = (RectangleMapObject) object;
        Rectangle rect = rectObject.getRectangle();
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.rect(rect.x, rect.y, rect.width, rect.height);
        sr.end();
    }
}
 * 
 */


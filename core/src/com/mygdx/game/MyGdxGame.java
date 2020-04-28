package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.RenderHelper;
import com.mygdx.game.ResourceManager;


//useful info here
//https://www.badlogicgames.com/forum/viewtopic.php?f=11&t=10173
//http://www.pixnbgames.com/blog/libgdx/how-to-use-libgdx-tiled-several-layers/

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
    Texture img;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    
    Viewport view;
    
    SpriteBatch _spriteBatch;
        
    
    Sprite sp;
    Sprite window; 
    Sprite tab;
    
    
    int winx;
    int winy;
    
    BitmapFont font;         
    TiledMapImageLayer obj;
    
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
    
    @Override
    public void create () {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
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
        
        view = new FitViewport(TARGET_WIDTH, TARGET_HEIGHT);
        

        
        tiledMap = new TmxMapLoader().load("dockingbay2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        
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
        tab.setPosition(plrx, plry);
        
        float camerax=plrx+32;
        float cameray=plry;
        if(camerax - (TARGET_WIDTH/2) < 0) camerax=TARGET_WIDTH/2;
        if(camerax +(TARGET_WIDTH/2) > mapWidth) camerax=mapWidth - (TARGET_WIDTH/2);
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        
        
        camera.position.set(camerax, cameray, 0);
        camera.update();
        
        //_spriteBatch.begin();
        //sp.setPosition(208*2-winx*2, 180*2-winy*2);
        //sp.setScale(2);
        //sp.draw(_spriteBatch);
        //_spriteBatch.end();
        
        //RenderHelper.RenderAtPosition(_spriteBatch, bg, winx, winy);
        tiledMapRenderer.setView(camera);                
        
        tiledMapRenderer.render(); //todo - proper render order of all the layers with sprites in between
     
        
        

        
        //MapObjects colobj = tiledMap.getLayers().get("Obstructions").getObjects();
        

        
        
        _spriteBatch.begin();
        _spriteBatch.setProjectionMatrix(camera.combined);
		        
		window.setPosition(TARGET_WIDTH-window.getWidth()+winx, TARGET_HEIGHT-window.getHeight()+winy);
        
        window.draw(_spriteBatch);
        
        
        tab.draw(_spriteBatch);
        //float fh=font.getLineHeight();
        font.draw(_spriteBatch, "Screen w: "+Float.toString(w) + " h: " + Float.toString(h), 0, TARGET_HEIGHT+winy);
        font.draw(_spriteBatch, "winx: "+Integer.toString(winx) + " winy: " + Integer.toString(winy), 0, TARGET_HEIGHT-20+winy);
        font.draw(_spriteBatch, "camerax: "+Float.toString(camerax) + " y: " + Float.toString(cameray), 0, TARGET_HEIGHT-40+winy);
        
        
        /*
        font.draw(_spriteBatch, obj.getName(), 0, h+20);        
        
        Iterator<MapObject> i = colobj.iterator();
        int y=0;
        while(i.hasNext()) {
        	MapProperties o = i.next().getProperties();
        
        	//System.out.println("value= " + i.next().toString());
        	font.draw(_spriteBatch, o.toString()  , 0, 440-y);
        	y+=20;
        	
        }
        */
        
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
        if(keycode == Input.Keys.MINUS) {
     	   sx+=16;
     	   sy+=9;
     	  
           camera.setToOrtho(false,TARGET_WIDTH+sx,TARGET_HEIGHT+sy);
           camera.update();
     	   
       }
     
        
        return false;
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


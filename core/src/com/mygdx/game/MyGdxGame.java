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
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.RenderHelper;
import com.mygdx.game.ResourceManager;



public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
    Texture img;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    
    Viewport view;
    
    SpriteBatch _spriteBatch;
    
    //Texture bg;       
    
    Sprite sp;
    Sprite window; 
    
    int winx;
    int winy;
    
    BitmapFont font;         
    TiledMapImageLayer obj;
    
    
    @Override
    public void create () {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        //_spriteBatch = ResourceManager.GetSpriteBatch();
        _spriteBatch = new SpriteBatch();
 
        //sp = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        window = new Sprite(new Texture(Gdx.files.internal("mapwindow.png")));
        
        font = new BitmapFont(); 
        
        
        winx=0;
        winy=0;
        
        camera = new OrthographicCamera();
        //camera.setToOrtho(false,w,h);
        camera.setToOrtho(false,320,240);
        camera.update();
        
        view = new FitViewport(320, 240, camera);
        
        tiledMap = new TmxMapLoader().load("dockingbay2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        MapLayers mapLayers = tiledMap.getLayers();

        obj =  (TiledMapImageLayer) mapLayers.get("BG2");
        
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
    	
        
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        
        //_spriteBatch.begin();
        //sp.setPosition(208*2-winx*2, 180*2-winy*2);
        //sp.setScale(2);
        //sp.draw(_spriteBatch);
        //_spriteBatch.end();
        
        //RenderHelper.RenderAtPosition(_spriteBatch, bg, winx, winy);
        tiledMapRenderer.setView(camera);                
        
        tiledMapRenderer.render();
        
        
        //https://www.badlogicgames.com/forum/viewtopic.php?f=11&t=10173

        
MapObjects colobj = tiledMap.getLayers().get("Obstructions").getObjects();
        
        
        _spriteBatch.begin();
        window.setPosition(560, 420);
        window.setScale(2);
        window.draw(_spriteBatch);
        
        font.draw(_spriteBatch, "w: "+Integer.toString(Gdx.graphics.getWidth()) + " h: " + Integer.toString(Gdx.graphics.getHeight()), 0, 480);
        font.draw(_spriteBatch, obj.getName(), 0, 460);        
        
        Iterator<MapObject> i = colobj.iterator();
        int y=0;
        while(i.hasNext()) {
        	MapProperties o = i.next().getProperties();
        
        	//System.out.println("value= " + i.next().toString());
        	font.draw(_spriteBatch, o.toString()  , 0, 440-y);
        	y+=20;
        	
        }
        
        _spriteBatch.end();
        
        
        
        
    }

    @Override
    public boolean keyDown(int keycode) {
    	
        if(keycode == Input.Keys.LEFT) {
            camera.translate(-16,0);
            winx -= 16;
        }
        if(keycode == Input.Keys.RIGHT) {
            camera.translate(16,0);
            winx += 16;
        }
        if(keycode == Input.Keys.UP) {
            camera.translate(0,16);
            winy += 16;
        }
        if(keycode == Input.Keys.DOWN) {
            camera.translate(0,-16);
            winy -= 16;
        }
        if(keycode == Input.Keys.NUM_1)
            tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
        if(keycode == Input.Keys.NUM_2)
            tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
        if(keycode == Input.Keys.NUM_3) {
        Gdx.graphics.setWindowedMode(960, 720);
        }
        if(keycode == Input.Keys.NUM_4) {
            Gdx.graphics.setWindowedMode(640, 480);
            }
        if(keycode == Input.Keys.NUM_5) {
        	  //DisplayMode m = new DisplayMode(32, 640, 60, 480);
              //Gdx.graphics.setFullscreenMode(m);
        	   Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
               Gdx.graphics.setFullscreenMode(displayMode);
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


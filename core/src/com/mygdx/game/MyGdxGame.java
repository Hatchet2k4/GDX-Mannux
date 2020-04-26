package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.mygdx.game.RenderHelper;
import com.mygdx.game.ResourceManager;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
    Texture img;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    
    SpriteBatch _spriteBatch;
    
    //Texture bg;       
    
    Sprite sp;
    Sprite window; 
    
    int winx;
    int winy;
    
    
    
    @Override
    public void create () {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        //_spriteBatch = ResourceManager.GetSpriteBatch();
        _spriteBatch = new SpriteBatch();
        //bg = 
        sp = new Sprite(new Texture(Gdx.files.internal("bg_docking_bay.png")));
        window = new Sprite(new Texture(Gdx.files.internal("mapwindow.png")));
        
        
        
        
        winx=0;
        winy=0;
        
        camera = new OrthographicCamera();
        //camera.setToOrtho(false,w,h);
        camera.setToOrtho(false,320,240);
        camera.update();
        tiledMap = new TmxMapLoader().load("dockingbay2.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        //bg = ResourceManager.GetTexture("bg_docking_bay.png");
        
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render () {
    	
    	
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
    	
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        
        _spriteBatch.begin();
        sp.setPosition(208*2-winx*2, 180*2-winy*2);
        sp.setScale(2);
        sp.draw(_spriteBatch);
        _spriteBatch.end();
        
        //RenderHelper.RenderAtPosition(_spriteBatch, bg, winx, winy);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        
        
        _spriteBatch.begin();
        window.setPosition(560, 520);
        window.setScale(2);
        window.draw(_spriteBatch);
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

package com.mygdx.game;

import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.RenderHelper;
import com.mygdx.game.ResourceManager;
import java.util.ArrayList;
import java.util.Arrays;
//useful info here
//https://www.badlogicgames.com/forum/viewtopic.php?f=11&t=10173
//http://www.pixnbgames.com/blog/libgdx/how-to-use-libgdx-tiled-several-layers/

/*
mgambrell
it begins to get complicated but if you can plan ahead, you can make games which function okay with different viewable areas. then choose the base resolution to be the largest of those, and crop the gameplay on the smaller one. as an example (not saying it's a good one) you can use a base resolution of 428x240, put a 384x216 window over the world while designing the game and keep the important content in there, and then do this:
428x240 x3 = 1280x720
384x216 x5 = 1920x1080
 */

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
    // Texture img;
    TiledMap tiledMap;
    OrthographicCamera camera;
    OrthographicCamera screen;

    Music music;

    TiledMapRenderer tiledMapRenderer;

    Viewport view;

    SpriteBatch _spriteBatch;
    SpriteBatch batch;

    Sprite sp;
    Sprite window;
    Sprite tab;

    int winx;
    int winy;
    
    

    BitmapFont font;
    // TiledMapImageLayer obj;

    int sx;
    int sy;

    int TARGET_WIDTH = 480;
    int TARGET_HEIGHT = 270;
    // int TARGET_WIDTH=384;
    // int TARGET_HEIGHT=216;
    // int TARGET_WIDTH=320;
    // int TARGET_HEIGHT=180;

    int numobj;

    int mapWidth;
    int mapHeight;

    float plrx, plry;
    ShapeRenderer debugRenderer;

    Polyline polyline;

    Rectangle scissor;
    Rectangle clipBounds;


    FrameBuffer lightBuffer;
    TextureRegion lightBufferRegion;
    Texture lightSprite;

    ArrayList<ChainShape> obs;
    
    float[] vertices;
    
    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.graphics.setWindowedMode(TARGET_WIDTH * 3, TARGET_HEIGHT * 3);

        debugRenderer = new ShapeRenderer();

        // _spriteBatch = ResourceManager.GetSpriteBatch();
        _spriteBatch = new SpriteBatch();
        batch = new SpriteBatch();

        music = Gdx.audio.newMusic(Gdx.files.internal("00_-_zaril_-_close_to_the_core.mp3"));
        music.setLooping(true);
        music.play();

        // sp = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        window = new Sprite(new Texture(Gdx.files.internal("mapwindow.png")));
        tab = new Sprite(new Texture(Gdx.files.internal("tab.png")));


        lightSprite = new Texture(Gdx.files.internal("light.png"));
        
        
        font = new BitmapFont();

        scissor = new Rectangle();
        clipBounds = new Rectangle(0, TARGET_HEIGHT, 50, TARGET_HEIGHT - 50);

        sx = 0;
        sy = 0;
        winx = 0;
        winy = 0;

        plrx = 176;
        plry = 96;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, TARGET_WIDTH, TARGET_HEIGHT);
        camera.update();

        screen = new OrthographicCamera();

        screen.setToOrtho(false, TARGET_WIDTH, TARGET_HEIGHT);
        screen.update();

        view = new FitViewport(TARGET_WIDTH, TARGET_HEIGHT);

        tiledMap = new TmxMapLoader().load("dockingbay2.tmx");
        //tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, _spriteBatch);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tiledMapRenderer.setView(camera);

        MapLayers layers = tiledMap.getLayers();
        Iterator<MapLayer> layersIter = layers.iterator();

        // polyline = ((PolylineMapObject)o).getPolyline();
        //obs = new ArrayList<ChainShape>();
        //List<Float> obslist = new ArrayList<Float>();
        
        //float[] v = polyline.getTransformedVertices();
        
        while (layersIter.hasNext()) {
            MapLayer layer = layersIter.next();
            if (layer.getName().equals("Obstructions")) {
                MapObjects os = layer.getObjects();
                numobj = os.getCount();

                Iterator<MapObject> osIter = os.iterator();
                while (osIter.hasNext()) {
                    MapObject o = osIter.next();
                    //bad, assuming polylines!
                    
                    polyline = ((PolylineMapObject) o).getPolyline();
                    //float[] v = polyline.getTransformedVertices();
                    //obslist.addAll(Arrays.asList((Float[])v));
                    
                    
                }
            }
            if (layer.getName().equals("Walls") || layer.getName().equals("Background")) {
             //   layer.setOpacity(0.5f);
             //   layer.setOffsetX(50);
            }

        }

        MapProperties prop = tiledMap.getProperties();

        mapWidth = prop.get("width", Integer.class) * 16;
        mapHeight = prop.get("height", Integer.class) * 16;

        // MapLayers mapLayers = tiledMap.getLayers();

        // obj = (TiledMapImageLayer) mapLayers.get("BG2");

        // bg = ResourceManager.GetTexture("bg_docking_bay.png");

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {
        view.update(width, height);

        // Fakedlight system (alpha blending)

        // if lightBuffer was created before, dispose, we recreate a new one
        if (lightBuffer!=null) lightBuffer.dispose();
        lightBuffer = new FrameBuffer(Format.RGBA8888, TARGET_WIDTH, TARGET_HEIGHT, false);

        lightBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture(),0,lightBuffer.getHeight()-TARGET_HEIGHT,TARGET_WIDTH,TARGET_HEIGHT);

        lightBufferRegion.flip(false, false);
    }

    public int PowerOf2(int x) {
        int power = 1;
        while(power < x) { power*=2; }        
        return power;
    }

    @Override
    public void render() {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            plrx -= 4;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            plrx += 4;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            plry += 4;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            plry -= 4;
        }

        tab.setPosition(plrx, plry);

        float camerax = plrx + 32;
        float cameray = plry + 32;
        if (camerax - (TARGET_WIDTH / 2) < 0)
            camerax = TARGET_WIDTH / 2;
        if (camerax + (TARGET_WIDTH / 2) > mapWidth)
            camerax = mapWidth - (TARGET_WIDTH / 2);

        if (cameray - (TARGET_HEIGHT / 2) < 0)
            cameray = TARGET_HEIGHT / 2;
        if (cameray + (TARGET_HEIGHT / 2) > mapHeight)
            cameray = mapHeight - (TARGET_HEIGHT / 2);

        camera.position.set(camerax, cameray, 0);
        camera.update();
        
        // tiledMapRenderer.setView(camera.combined, 64, 64, TARGET_WIDTH - 64,
        // TARGET_HEIGHT - 64);
        //tiledMapRenderer.setView(camera.combined, 64, 64, TARGET_WIDTH, TARGET_HEIGHT);
        tiledMapRenderer.setView(camera);

        Gdx.gl.glClearColor(100, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _spriteBatch.setProjectionMatrix(camera.combined);

        // ScissorStack.calculateScissors(screen, 0, 0, TARGET_WIDTH, TARGET_HEIGHT,
        // _spriteBatch.getTransformMatrix(), clipBounds, scissor);
        // ScissorStack.pushScissors(scissor);

        tiledMapRenderer.render(); // todo - proper render order of all the layers with sprites in between
        _spriteBatch.flush();
        // ScissorStack.popScissors();

        _spriteBatch.begin();
        tab.draw(_spriteBatch);
        // float fh=font.getLineHeight();

        
/*
        
        float lastx=-9999f;
        float lasty=-9999f;
        float curx=-9999f;
        float cury=-9999f;
        
        if (v.length>=4) {
        	lastx=v[0];
        	lasty=v[1];
        	
            for(int i=2; i<v.length; i+=2) {
            	curx=v[i];
            	cury=v[i+1];
            	
            	DrawLine(new Vector2(lastx, lasty), new Vector2(curx, cury), 3, Color.YELLOW, camera.combined); 	
            	
            	lastx=curx;
            	lasty=cury;
            }        	
        }        
  */      
        _spriteBatch.end();


        

        // last layer, everything here drawn in target screen coords. UI elements, etc
        
        

        //RunLights();



        
        _spriteBatch.begin();
        _spriteBatch.setProjectionMatrix(screen.combined);
        // some reason, font needs to be first..?
        font.draw(_spriteBatch, "Screen w: " + Float.toString(w) + " h: " + Float.toString(h), 0, TARGET_HEIGHT);
        //font.draw(_spriteBatch, "Target w: " + Integer.toString(TARGET_WIDTH) + " h: " + Integer.toString(TARGET_HEIGHT), 0, TARGET_HEIGHT - 20);
        //font.draw(_spriteBatch, "camerax: " + Float.toString(camerax) + " y: " + Float.toString(cameray), 0, TARGET_HEIGHT - 40);
       // font.draw(_spriteBatch, "num obj: " + Integer.toString(numobj), 0,   TARGET_HEIGHT - 60);
        
        
        
        //font.draw(_spriteBatch, "Polyline: " + Float.toString(w) + " h: " + Float.toString(h), 0, TARGET_HEIGHT);
        
        window.setPosition(TARGET_WIDTH - window.getWidth() + winx, TARGET_HEIGHT - window.getHeight() + winy);

        window.draw(_spriteBatch);

        // float[] verts = polyline.getVertices();

        // DrawLine(new Vector2(10, 10), new Vector2(20, 20), 3, Color.YELLOW,
        // screen.combined);

        

        
        
        _spriteBatch.end();

    }

    public void RunLights() {
        // start rendering to the lightBuffer
        lightBuffer.begin();

        // setup the right blending
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        Gdx.gl.glEnable(GL20.GL_BLEND);
                        
        // set the ambient color values, this is the "global" light of your scene
        // imagine it being the sun.  Usually the alpha value is just 1, and you change the darkness/brightness with the Red, Green and Blue values for best effect

        Gdx.gl.glClearColor(0.0f,0.0f,0.0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                            
        // start rendering the lights to our spriteBatch
        batch.begin();


        // set the color of your light (red,green,blue,alpha values)
        batch.setColor(0.2f, 0.2f, 0.4f, 1.2f);

        // tx and ty contain the center of the light source
        //float tx= (TARGET_WIDTH/2);
        float ty= (TARGET_HEIGHT/2);
        //float tx=0;
        //float ty=TARGET_HEIGHT;
        float tx=plrx+32;
        //float ty=plry+32;



        // tw will be the size of the light source based on the "distance"
        // (the light image is 128x128)
        // and 96 is the "distance"  
        // Experiment with this value between based on your game resolution 
        // my lights are 8 up to 128 in distance
        
        //float tw=(512/100f)*128;
        float tw=1024;
        float th=1024;

        // make sure the center is still the center based on the "distance"
        //tx-=(tw/2);
        //ty-=(tw/2);

        // and render the sprite
        batch.draw(lightSprite, tx,ty,512,512,0,0,1024,1024,false,false);

        batch.end();
        lightBuffer.end();


        // now we render the lightBuffer to the default "frame buffer"
        // with the right blending !

        Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
        batch.begin();
        //batch.draw(lightBufferRegion, 0, 0,TARGET_WIDTH,TARGET_HEIGHT);               
        batch.draw(lightBufferRegion, 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());               
        batch.end();

        // post light-rendering
        // you might want to render your statusbar stuff here
        
    }

    @Override
    public boolean keyDown(int keycode) {
        /*
         * if(keycode == Input.Keys.LEFT & winx>0) {
         * 
         * camera.translate(-16,0); winx -= 16; } if(keycode == Input.Keys.RIGHT &
         * winx<mapWidth-TARGET_WIDTH) { camera.translate(16,0); winx += 16; }
         * if(keycode == Input.Keys.UP & winy<mapHeight-TARGET_HEIGHT) {
         * camera.translate(0,16); winy += 16; } if(keycode == Input.Keys.DOWN & winy>0)
         * { camera.translate(0,-16); winy -= 16; }
         */
        if (keycode == Input.Keys.NUM_1)
            tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
        if (keycode == Input.Keys.NUM_2)
            tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
        if (keycode == Input.Keys.NUM_3) {
            Gdx.graphics.setWindowedMode(TARGET_WIDTH * 3, TARGET_HEIGHT * 3);
        }
        if (keycode == Input.Keys.NUM_4) {
            Gdx.graphics.setWindowedMode(TARGET_WIDTH * 2, TARGET_HEIGHT * 2);
        }
        if (keycode == Input.Keys.NUM_5) {

            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        }
        if (keycode == Input.Keys.NUM_6) {

            Gdx.graphics.setWindowedMode(1280, 720);
        }
        if (keycode == Input.Keys.PLUS) {
            sx -= 16;
            sy -= 9;

            camera.setToOrtho(false, TARGET_WIDTH + sx, TARGET_HEIGHT + sy);
            camera.update();

        }
        if (keycode == Input.Keys.ESCAPE) {

            dispose();
        }

        return false;
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        font.dispose();
        music.dispose();
        tab.getTexture().dispose();
        window.getTexture().dispose();
        lightSprite.dispose();
        lightBuffer.dispose();


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
/*

    private void ParseObstructions(TiledMap tiledMap)
    {
        for(var tileset in _tiledMap.Tilesets)
        {
            var texture = tileset.Texture;
            foreach (var tile in tileset.Tiles)
            {
                string obs = "";
                if (tile.Properties.TryGetValue("obs",out obs) && obs == "true")
                {
                    var tr = tileset.GetTileRegion(tile.LocalTileIdentifier);
                    Color[] rawData = new Color[tr.Width * tr.Height];
                    texture.GetData<Color>(0, tr, rawData, 0,tr.Width*tr.Height);
                    var obdata = new bool[tr.Width, tr.Height];
                    for (var x = 0; x < tr.Width; x++)
                    {
                        for (var y = 0; y < tr.Height; y++)
                        {
                            obdata[x, y] = rawData[x + y * tr.Height].A > 0;
                        }
                    }
                    _obsData.Add(tile.LocalTileIdentifier+1, obdata);
                }
            }
        }
    }    
    */
    
    public void DrawLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix) {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public void DrawLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix) {
        Gdx.gl.glLineWidth(2);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

}

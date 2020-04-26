package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class ResourceManager {

    private static int TARGET_RESOLUTION_WIDTH = 320;
    private static int TARGET_RESOLUTION_HEIGHT = 240;

    private static ResourceManager _instance;

    private float _musicVolume;

    private Music _music;
    private TiledMap _tiledMap;
    private TmxMapLoader _mapLoader;
    private ScalingViewport _viewport;
    private SpriteBatch _spriteBatch;
    private OrthographicCamera _camera;
    private HashMap<String, BitmapFont> _fonts;
    private HashMap<String, Texture> _textures;
    private ArrayList<TiledMapTileLayer> _mapLayers;
    private int _mapWidth;
    private int _mapHeight;

    public ResourceManager()
    {
        _instance = this;
        _musicVolume = 1f;
        _mapLoader = new TmxMapLoader();
        _spriteBatch = new SpriteBatch();
        _fonts = new HashMap<String, BitmapFont>();
        _textures = new HashMap<String, Texture>();
        LoadAssets();
        SetupCamera();
    }

    public static SpriteBatch GetSpriteBatch()
    {
        return _instance._spriteBatch;
    }

    public static Texture GetTexture(String assetName)
    {
        return _instance._getTexture(assetName);
    }

    public static BitmapFont GetFont(String assetName)
    {
        return _instance._getFont(assetName);
    }

    public static OrthographicCamera GetCamera()
    {
        return _instance._camera;
    }

    public static void PlayMusic(String musicFileName)
    {
        _instance._playMusic(musicFileName);
    }

    public static int[][][] GetCurrentMapData()
    {
        return _instance.GetMapData();
    }

    public static int GetCurrentMapWidth()
    {
        return _instance._mapWidth;
    }

    public static int GetCurrentMapHeight()
    {
        return _instance._mapHeight;
    }

    public static int GetCurrentMapLayerCount()
    {
        return _instance._mapLayers.size();
    }

    public static void ToggleFullScreen() {
        Boolean fullScreen = Gdx.graphics.isFullscreen();
        DisplayMode currentMode = Gdx.graphics.getDisplayMode();
        if (fullScreen == true)
        {
            Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
        }
        else
        {
            Gdx.graphics.setFullscreenMode(currentMode);
        }
    }

    public void Resize(int width, int height)
    {
        _viewport.update(width, height);
        _camera.update();
    }

    private void SetupCamera()
    {
		int w = TARGET_RESOLUTION_WIDTH;
        int h = TARGET_RESOLUTION_HEIGHT;
		_camera = new OrthographicCamera(w, h);
        _camera.setToOrtho(true, w, h);
        _camera.update();
        _viewport = new ScalingViewport(Scaling.fit, w, h);
        _viewport.update(w, h);
    }

    private void LoadMap(String mapFileName)
    {
        _tiledMap = _mapLoader.load(mapFileName);
        _mapLayers = new ArrayList<TiledMapTileLayer>();
        MapLayers mapLayers = _tiledMap.getLayers();
        for (int i = 0; i < mapLayers.getCount(); i++)
        {
            TiledMapTileLayer layer = (TiledMapTileLayer) _tiledMap.getLayers().get(i);
            _mapLayers.add(layer);
            _mapWidth = layer.getWidth();
            _mapHeight = layer.getHeight();
        }
        Gdx.app.log("map","Load Map");
    }

    private int[][][] GetMapData()
    {
        int[][][] mapData = new int[_mapWidth][_mapHeight][_mapLayers.size()];
        Gdx.app.log("width", ""+_mapWidth);
        Gdx.app.log("height", ""+_mapHeight);
        Gdx.app.log("layer", ""+_mapLayers.size());
        for (int l = 0; l < _mapLayers.size(); l++)
        {
            TiledMapTileLayer layer = _mapLayers.get(l);
            for (int x = 0; x < _mapWidth; x++)
            {
                for (int y = 0; y < _mapHeight; y++)
                {
                    int t = 0;
                    Cell cell = layer.getCell(x, y);
                    if (cell != null)
                    {
                        TiledMapTile tile = cell.getTile();
                        if (tile != null)
                        {
                            t = tile.getId();
                        }
                    }
                    mapData[x][y][l] = t;
                }
            }
        }
        return mapData;
    }

    private void LoadAssets()
    {
        LoadTexture("images/ui/hudmain.png");
        LoadTexture("images/title/finaleclipse_glow.png");
        LoadFont("fonts/ubuntu.fnt", "fonts/ubuntu.png");
        LoadFont("fonts/pixel.fnt", "fonts/pixel.png");
        LoadTexture("images/backgrounds/medsci1.png");

        LoadPerspectiveTexture("walls", "bed");
        LoadPerspectiveTexture("walls", "decal_door");
        LoadPerspectiveTexture("walls", "final");
        LoadPerspectiveTexture("walls", "finalspace");
        LoadPerspectiveTexture("walls", "med1");
        LoadPerspectiveTexture("walls", "med1door");
        LoadPerspectiveTexture("walls", "med1win");
        LoadPerspectiveTexture("walls", "med2");
        LoadPerspectiveTexture("walls", "med2door");
        LoadPerspectiveTexture("walls", "military");
        LoadPerspectiveTexture("walls", "militarydoor");
        LoadPerspectiveTexture("walls", "militaryplain");
        LoadPerspectiveTexture("decals", "blood_big");
        LoadPerspectiveTexture("decals", "blood_sm");
        LoadPerspectiveTexture("decals", "dcode_g");
        LoadPerspectiveTexture("decals", "dcode_r");
        LoadPerspectiveTexture("decals", "decal_door");
        LoadPerspectiveTexture("decals", "dkey_g");
        LoadPerspectiveTexture("decals", "dkey_r");
        LoadPerspectiveTexture("decals", "door");
        LoadPerspectiveTexture("decals", "switch1");
        LoadPerspectiveTexture("decals", "switch2");
        LoadMap("maps/testmap1.tmx");
    }

    private void LoadPerspectiveTexture(String category, String key)
    {
        String imagePath = "images/" + category + "/" + key + "/";
        for (int i = 1; i <= 3; i++)
        {
            LoadTexture(imagePath + "flat" + i + "left.png");
            LoadTexture(imagePath + "flat" + i + "mid.png");
            LoadTexture(imagePath + "flat" + i + "right.png");
        }
        for (int i = 1; i <= 4; i++)
        {
            LoadTexture(imagePath+ "per" + i + "left.png");
            LoadTexture(imagePath + "per" + i + "right.png");
        }
        LoadTexture(imagePath + "per4farleft.png");
        LoadTexture(imagePath + "per4farright.png");
    }

    private void LoadTexture(String fileName)
    {
        String assetName = fileName.replace("images/", "").replace(".png", "");
        _textures.put(assetName, new Texture(fileName));
    }

    private void LoadFont(String fntFileName, String pngFileName)
    {
        String assetName = fntFileName.replace("fonts/", "").replace(".fnt", "");
        BitmapFont font = new BitmapFont(Gdx.files.internal(fntFileName), Gdx.files.internal(pngFileName), true);
        _fonts.put(assetName, font);
    }

    private void _playMusic(String musicFileName)
    {
        if (_music != null)
        {
            _music.dispose();
        }
        _music = Gdx.audio.newMusic(Gdx.files.internal(musicFileName));
        _music.play();
        _music.setVolume(_musicVolume);
        _music.setLooping(true);
    }

    private Texture _getTexture(String assetName)
    {
        if (_textures.containsKey(assetName))
        {
            return _textures.get(assetName);
        }
        return null;
    }

    private BitmapFont _getFont(String assetName)
    {
        if (_fonts.containsKey(assetName))
        {
            return _fonts.get(assetName);
        }
        return null;
    }

    public void Dispose()
    {
        if (_music != null)
        {
            _music.dispose();
        }
        for (Texture texture : _textures.values())
        {
            texture.dispose();
        }
        for (BitmapFont font : _fonts.values())
        {
            font.dispose();
        }
    }
}
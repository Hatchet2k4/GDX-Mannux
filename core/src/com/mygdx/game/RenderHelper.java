package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderHelper
{
    public static void RenderAtPosition(SpriteBatch batch, Texture texture, int x, int y)
    {
        int w = texture.getWidth();
        int h = texture.getHeight();
        batch.draw(texture, x, y, 0, 0, texture.getWidth(), texture.getHeight(), 1f, 1f, 0f, 0, 0, w, h, false, true);
    }
}
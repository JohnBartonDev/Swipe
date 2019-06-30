package com.vabrant.swipe.lwjgl3;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.vabrant.swipe.Swipe;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }
    
    private static LwjglApplication createApplication() {
        return new LwjglApplication(new Swipe(), getDefaultConfiguration());
    }

    private static LwjglApplicationConfiguration getDefaultConfiguration() {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.title = "SnakeAttack";
        configuration.width = 960;
        configuration.height = 640;
        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
        }
        return configuration;
    }

//    private static Lwjgl3Application createApplication() {
//    	return new Lwjgl3Application(new Swipe(), getDefaultConfiguration());
//    }
//
//    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
//        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
//        configuration.setTitle("Swipe");
//        configuration.setResizable(false);
//        configuration.setWindowedMode(960, 640);
//        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
//        return configuration;
//    }
}
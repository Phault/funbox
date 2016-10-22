package com.phault.funbox.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.phault.funbox.Funbox;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Funbox";
		config.width = 1280;
		config.height = 720;
		config.addIcon("icon_128.png", Files.FileType.Internal);
		config.addIcon("icon_64.png", Files.FileType.Internal);
		config.addIcon("icon_32.png", Files.FileType.Internal);
		new LwjglApplication(new Funbox(), config);
	}
}

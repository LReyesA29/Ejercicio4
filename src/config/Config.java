package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	private static Config instance;
	private final Properties properties;

	private String pathFiles;
	private String nameFileParametros;
	private String nameFileTXT;
	private String nameFileJson;
	private String nameFileCSV;


	private Config() {
		properties = new Properties();
		loadConfig();
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	private void loadConfig() {
		try (InputStream input = new FileInputStream("resources/config/appconfig.properties")) {
			properties.load(input);

			this.nameFileParametros = properties.getProperty("app.config.path.file.name.parametros.xml");
			this.pathFiles = properties.getProperty("app.config.path.files");
			this.nameFileTXT = properties.getProperty("app.config.path.file.name.txt");
			this.nameFileJson = properties.getProperty("app.config.path.file.name.json");
			this.nameFileCSV = properties.getProperty("app.config.path.file.name.csv");


		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Getters


	public String getPathFiles() {
		return pathFiles;
	}

	public String getNameFileParametros() {
		return nameFileParametros;
	}

	public String getNameFileTXT() {
		return nameFileTXT;
	}
	public String getNameFileJson() {
		return nameFileJson;
	}

	public String getNameFileCSV() {
		return nameFileCSV;
	}

}

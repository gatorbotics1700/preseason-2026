// Copyright 2021-2025 FRC 6328
// http://github.com/Mechanical-Advantage
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// version 3 as published by the Free Software Foundation or
// available in the root directory of this project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

package frc.robot.util;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotController;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading robot configuration based on roboRIO serial number. This allows the
 * same codebase to work with different robot configurations.
 */
public class RobotConfigLoader {
  private static Properties config = new Properties();
  private static boolean configLoaded = false;

  /**
   * Loads the appropriate configuration file based on the roboRIO serial number. Configuration
   * files are located in the deploy directory.
   */
  public static void loadConfig() {
    if (configLoaded) {
      return;
    }

    String serialNumber = RobotController.getSerialNumber();
    String configFileName;

    // Map serial numbers to configuration files
    switch (serialNumber) {
      case "032398EA":
        configFileName = "config_robot1.properties";
        break;
      case "032398EB":
        configFileName = "config_robot2.properties";
        break;
      default:
        throw new RuntimeException("Unknown roboRIO serial number: " + serialNumber);
    }

    try {
      String configPath = Filesystem.getDeployDirectory().getPath() + "/" + configFileName;
      config.load(new FileInputStream(configPath));
      configLoaded = true;
      System.out.println("Loaded configuration for roboRIO serial: " + serialNumber);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load configuration file: " + configFileName, e);
    }
  }

  /**
   * Gets a string property from the loaded configuration.
   *
   * @param key The property key
   * @return The property value
   */
  public static String getString(String key) {
    if (!configLoaded) {
      loadConfig();
    }
    return config.getProperty(key);
  }

  /**
   * Gets a double property from the loaded configuration.
   *
   * @param key The property key
   * @return The property value as a double
   */
  public static double getDouble(String key) {
    return Double.parseDouble(getString(key));
  }

  /**
   * Gets an integer property from the loaded configuration.
   *
   * @param key The property key
   * @return The property value as an integer
   */
  public static int getInt(String key) {
    return Integer.parseInt(getString(key));
  }

  /**
   * Gets a boolean property from the loaded configuration.
   *
   * @param key The property key
   * @return The property value as a boolean
   */
  public static boolean getBoolean(String key) {
    return Boolean.parseBoolean(getString(key));
  }

  /**
   * Gets the roboRIO serial number.
   *
   * @return The serial number as a string
   */
  public static String getSerialNumber() {
    return RobotController.getSerialNumber();
  }
}


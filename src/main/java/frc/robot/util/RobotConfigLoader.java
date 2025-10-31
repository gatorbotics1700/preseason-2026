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
 * Simple config loader that reads properties files based on roboRIO serial number.
 */
public class RobotConfigLoader {
  private static Properties config = null;

  static Properties load() {
    if (config != null) {
      return config;
    }

    String serialNumber = RobotController.getSerialNumber();
    String fileName =
        switch (serialNumber) {
          case "032398EA" -> "config_robot1.properties";
          case "032398EB" -> "config_robot2.properties";
          default -> throw new RuntimeException("Unknown roboRIO serial: " + serialNumber);
        };

    config = new Properties();
    try {
      config.load(
          new FileInputStream(Filesystem.getDeployDirectory().getPath() + "/" + fileName));
      System.out.println("Loaded config: " + fileName + " (Serial: " + serialNumber + ")");
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config: " + fileName, e);
    }

    return config;
  }

  public static String getString(String key) {
    return load().getProperty(key);
  }

  public static double getDouble(String key) {
    return Double.parseDouble(getString(key));
  }

  public static int getInt(String key) {
    return Integer.parseInt(getString(key));
  }

  public static String getSerialNumber() {
    return RobotController.getSerialNumber();
  }
}


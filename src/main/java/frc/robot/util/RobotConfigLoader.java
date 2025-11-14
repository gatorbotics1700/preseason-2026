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

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** Simple config loader that reads properties files based on roboRIO serial number. */
public class RobotConfigLoader {
  private static final String shenandoahSerialNumber = "03223852";
  private static final String huangHeSerialNumber = "032D20FA";
  private static Properties config = null;

  static Properties load() {
    if (config != null) {
      return config;
    }

    String serialNumber = RobotController.getSerialNumber();
    System.out.println("serialNumber: " + serialNumber);
    String fileName =
        switch (serialNumber) {
          case shenandoahSerialNumber -> "config_robot1.properties";
          case huangHeSerialNumber -> "config_robot2.properties";
          default -> throw new RuntimeException("Unknown roboRIO serial: " + serialNumber);
        };

    config = new Properties();
    try {
      config.load(new FileInputStream(Filesystem.getDeployDirectory().getPath() + "/" + fileName));
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

  public static Transform3d createRobotToCamera0Transform() {
    return new Transform3d(
        Constants.LIMELIGHT_ROBOT_TO_CAMERA0_X,
        Constants.LIMELIGHT_ROBOT_TO_CAMERA0_Y,
        Constants.LIMELIGHT_ROBOT_TO_CAMERA0_Z,
        new Rotation3d(
            Math.toRadians(Constants.LIMELIGHT_ROBOT_TO_CAMERA0_ROLL),
            Math.toRadians(Constants.LIMELIGHT_ROBOT_TO_CAMERA0_PITCH),
            Math.toRadians(Constants.LIMELIGHT_ROBOT_TO_CAMERA0_YAW)));
  }

  /** Creates array of camera std dev factors from config values. */
  public static double[] createCameraStdDevFactors() {
    return new double[] {
      Constants.LIMELIGHT_CAMERA0_STD_DEV_FACTOR, Constants.LIMELIGHT_CAMERA1_STD_DEV_FACTOR
    };
  }
}

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
import frc.robot.subsystems.vision.VisionConstants;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** Simple config loader that reads properties files based on roboRIO serial number. */
public final class RobotConfigLoader {
  private static final String SHENANDOAH_SERIAL = "03223852";
  private static final String HUANG_HE_SERIAL = "032D20FA";
  private static final String MISSISSIPPI_SERIAL = "032D2198";
  private static final String DEFAULT_SIM_SERIAL = "SIMULATION";
  private static final String DEFAULT_CONFIG_FILE = "config_sting.properties";
  private static final String SERIAL_PROPERTY_KEY = "robot.serial";
  private static final String SERIAL_ENV_KEY = "ROBOT_SERIAL";
  private static final String CONFIG_PROPERTY_KEY = "robot.config";
  private static final String CONFIG_ENV_KEY = "ROBOT_CONFIG";

  private static Properties config;
  private static String serialOverride;
  private static String configOverride;
  private static String resolvedSerial;

  private RobotConfigLoader() {}

  public static synchronized void setSerialNumberOverride(String overrideSerial) {
    serialOverride = normalize(overrideSerial);
    resolvedSerial = null;
    config = null;
  }

  public static synchronized void setConfigFileOverride(String overrideFile) {
    configOverride = normalize(overrideFile);
    config = null;
  }

  public static synchronized void clearCache() {
    config = null;
    resolvedSerial = null;
  }

  static synchronized Properties load() {
    if (config != null) {
      return config;
    }

    String serial = getSerialNumber();
    String configFile = resolveConfigFile(serial);

    config = new Properties();
    try (FileInputStream stream =
        new FileInputStream(Filesystem.getDeployDirectory().getPath() + "/" + configFile)) {
      config.load(stream);
      System.out.println(
          "RobotConfigLoader: Loaded config '" + configFile + "' (Serial: " + serial + ")");
    } catch (IOException e) {
      throw new RuntimeException("RobotConfigLoader: Failed to load config: " + configFile, e);
    }

    return config;
  }

  public static String getString(String key) {
    String value = load().getProperty(key);
    if (value == null) {
      throw new IllegalArgumentException("Missing config key: " + key);
    }
    return value;
  }

  public static double getDouble(String key) {
    return Double.parseDouble(getString(key));
  }

  public static int getInt(String key) {
    return Integer.parseInt(getString(key));
  }

  public static Boolean getBoolean(String key) {
    return Boolean.parseBoolean(getString(key));
  }

  public static synchronized String getSerialNumber() {
    if (resolvedSerial == null) {
      resolvedSerial = computeSerialNumber();
    }
    return resolvedSerial;
  }

  private static String computeSerialNumber() {
    String fromOverride = normalize(serialOverride);
    if (fromOverride != null) {
      return fromOverride;
    }

    String fromProperty = normalize(System.getProperty(SERIAL_PROPERTY_KEY));
    if (fromProperty != null) {
      return fromProperty;
    }

    String fromEnv = normalize(System.getenv(SERIAL_ENV_KEY));
    if (fromEnv != null) {
      return fromEnv;
    }

    try {
      String serial = normalize(RobotController.getSerialNumber());
      if (serial != null) {
        return serial;
      }
    } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
      // Desktop unit tests (no HAL loaded)
    } catch (Throwable t) {
      System.out.println(
          "RobotConfigLoader: Unable to read roboRIO serial (" + t.getMessage() + ")");
    }

    return DEFAULT_SIM_SERIAL;
  }

  private static String resolveConfigFile(String serial) {
    String fromOverride = normalize(configOverride);
    if (fromOverride != null) {
      return fromOverride;
    }

    String fromProperty = normalize(System.getProperty(CONFIG_PROPERTY_KEY));
    if (fromProperty != null) {
      return fromProperty;
    }

    String fromEnv = normalize(System.getenv(CONFIG_ENV_KEY));
    if (fromEnv != null) {
      return fromEnv;
    }

    return switch (serial) {
      case SHENANDOAH_SERIAL -> "config_hulk.properties";
      case MISSISSIPPI_SERIAL -> "config_sting.properties";
      default -> {
        System.out.println(
            "RobotConfigLoader: Unknown serial '"
                + serial
                + "', falling back to '"
                + DEFAULT_CONFIG_FILE
                + "'. Set -D"
                + CONFIG_PROPERTY_KEY
                + "=<file> or call setConfigFileOverride() to pick a specific config.");
        yield DEFAULT_CONFIG_FILE;
      }
    };
  }

  private static String normalize(String text) {
    if (text == null) {
      return null;
    }
    String trimmed = text.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  public static Transform3d createRobotToCamera0Transform() {
    return new Transform3d(
        VisionConstants.LIMELIGHT_ROBOT_TO_CAMERA0_X,
        VisionConstants.LIMELIGHT_ROBOT_TO_CAMERA0_Y,
        VisionConstants.LIMELIGHT_ROBOT_TO_CAMERA0_Z,
        new Rotation3d(
            Math.toRadians(VisionConstants.LIMELIGHT_ROBOT_TO_CAMERA0_ROLL),
            Math.toRadians(VisionConstants.LIMELIGHT_ROBOT_TO_CAMERA0_PITCH),
            Math.toRadians(VisionConstants.LIMELIGHT_ROBOT_TO_CAMERA0_YAW)));
  }

  /** Creates array of camera std dev factors from config values. */
  public static double[] createCameraStdDevFactors() {
    return new double[] {
      Constants.LIMELIGHT_CAMERA0_STD_DEV_FACTOR, Constants.LIMELIGHT_CAMERA1_STD_DEV_FACTOR
    };
  }
}

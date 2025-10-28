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
import frc.robot.Constants;

/**
 * Helper class for working with configuration values. Provides convenient methods to create objects
 * from configuration constants.
 */
public class ConfigHelper {

  /**
   * Creates a Transform3d for the robot to camera 0 transform using configuration values.
   *
   * @return Transform3d representing the robot to camera 0 transform
   */
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

  /**
   * Creates an array of camera standard deviation factors using configuration values.
   *
   * @return Array of camera standard deviation factors
   */
  public static double[] createCameraStdDevFactors() {
    return new double[] {
      Constants.LIMELIGHT_CAMERA0_STD_DEV_FACTOR, Constants.LIMELIGHT_CAMERA1_STD_DEV_FACTOR
    };
  }

  /**
   * Prints the current robot configuration to the console. Useful for debugging and verification.
   */
  public static void printRobotConfiguration() {
    System.out.println("=== Robot Configuration ===");
    System.out.println("Serial Number: " + Constants.ROBOT_SERIAL_NUMBER);
    System.out.println("Camera 0 Name: " + Constants.LIMELIGHT_CAMERA_0_NAME);
    System.out.println("Camera 1 Name: " + Constants.LIMELIGHT_CAMERA_1_NAME);
    System.out.println("Pigeon ID: " + Constants.TUNER_PIGEON_ID);
    System.out.println("Drive KP: " + Constants.TUNER_DRIVE_KP);
    System.out.println("Steer KP: " + Constants.TUNER_STEER_KP);
    System.out.println("Front Left Encoder Offset: " + Constants.TUNER_FRONT_LEFT_ENCODER_OFFSET);
    System.out.println("Front Right Encoder Offset: " + Constants.TUNER_FRONT_RIGHT_ENCODER_OFFSET);
    System.out.println("Back Left Encoder Offset: " + Constants.TUNER_BACK_LEFT_ENCODER_OFFSET);
    System.out.println("Back Right Encoder Offset: " + Constants.TUNER_BACK_RIGHT_ENCODER_OFFSET);
    System.out.println("==========================");
  }

  /**
   * Gets the robot identifier based on the serial number.
   *
   * @return String identifier for the robot (e.g., "Robot 1", "Robot 2")
   */
  public static String getRobotIdentifier() {
    switch (Constants.ROBOT_SERIAL_NUMBER) {
      case "032398EA":
        return "Robot 1";
      case "032398EB":
        return "Robot 2";
      default:
        return "Unknown Robot (" + Constants.ROBOT_SERIAL_NUMBER + ")";
    }
  }
}


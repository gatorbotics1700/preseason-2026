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
 * Simple helper methods for creating objects from config constants.
 */
public class ConfigHelper {
  /** Creates Transform3d for robot to camera 0 from config values. */
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


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

package frc.robot.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.util.RobotConfigLoader;

public class VisionConstants {
  // AprilTag layout
  public static final AprilTagFieldLayout APRIL_TAG_LAYOUT =
      AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

  // Camera names, must match names configured on coprocessor
  public static final String LIMELIGHT_0_NAME = RobotConfigLoader.getString("limelight.0.name");

  public static final double ROBOT_TO_LIMELIGHT_0_X_METERS =
      RobotConfigLoader.getDouble("limelight.robot_to_limelight_0.x_meters");
  public static final double ROBOT_TO_LIMELIGHT_0_Y_METERS =
      RobotConfigLoader.getDouble("limelight.robot_to_limelight_0.y_meters");
  public static final double ROBOT_TO_LIMELIGHT_0_Z_METERS =
      RobotConfigLoader.getDouble("limelight.robot_to_limelight_0.z_meters");
  public static final double ROBOT_TO_LIMELIGHT_0_ROLL_DEGREES =
      RobotConfigLoader.getDouble("limelight.robot_to_limelight_0.roll_degrees");
  public static final double ROBOT_TO_LIMELIGHT_0_PITCH_DEGREES =
      RobotConfigLoader.getDouble("limelight.robot_to_limelight_0.pitch_degrees");
  public static final double ROBOT_TO_LIMELIGHT_0_YAW_DEGREES =
      RobotConfigLoader.getDouble("limelight.robot_to_limelight_0.yaw_degrees");

  public static Transform3d ROBOT_TO_LIMELIGHT_0 = createRobotToCamera0Transform();

  // Basic filtering thresholds
  public static double MAX_AMBIGUITY = RobotConfigLoader.getDouble("limelight.max_ambiguity");
  public static double MAX_Z_ERROR = RobotConfigLoader.getDouble("limelight.max_z_error");

  // Standard deviation baselines, for 1 meter distance and 1 tag
  // (Adjusted automatically based on distance and # of tags)
  public static double LINEAR_STD_DEV_BASELINE =
      RobotConfigLoader.getDouble("limelight.linear_std_dev_baseline"); // Meters
  public static double ANGULAR_STD_DEV_BASELINE =
      RobotConfigLoader.getDouble("limelight.angular_std_dev_baseline"); // Radians

  // Standard deviation multipliers for each camera
  // (Adjust to trust some cameras more than others)

  public static double LIMELIGHT_0_STD_DEV_FACTOR =
      RobotConfigLoader.getDouble("limelight.camera0_std_dev_factor");

  public static double[] CAMERA_STD_DEV_FACTORS = createCameraStdDevFactors();

  // Multipliers to apply for MegaTag 2 observations
  public static double LINEAR_STD_DEV_MEGATGAG_2_FACTOR =
      RobotConfigLoader.getDouble(
          "limelight.linear_std_dev_megatag2_factor"); // More stable than full 3D solve
  public static double ANGULAR_STD_DEV_MEGATAG_2_FACTOR =
      RobotConfigLoader.getDouble("limelight.angular_std_dev_megatag2_factor");

  public static Transform3d createRobotToCamera0Transform() {
    return new Transform3d(
        ROBOT_TO_LIMELIGHT_0_X_METERS,
        ROBOT_TO_LIMELIGHT_0_Y_METERS,
        ROBOT_TO_LIMELIGHT_0_Z_METERS,
        new Rotation3d(
            Math.toRadians(ROBOT_TO_LIMELIGHT_0_ROLL_DEGREES),
            Math.toRadians(ROBOT_TO_LIMELIGHT_0_PITCH_DEGREES),
            Math.toRadians(ROBOT_TO_LIMELIGHT_0_YAW_DEGREES)));
  }

  /** Creates array of camera std dev factors from config values. */
  public static double[]
      createCameraStdDevFactors() { // can add more constants if we have more cameras
    return new double[] {LIMELIGHT_0_STD_DEV_FACTOR};
  }
}

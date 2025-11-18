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
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.util.RobotConfigLoader;

public class VisionConstants {
  // AprilTag layout
  public static final AprilTagFieldLayout APRIL_TAG_LAYOUT =
      AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

  // Camera names, must match names configured on coprocessor
  public static final String CAMERA_0_NAME = "limelight";
  public static Transform3d ROBOT_TO_CAMERA_0 = RobotConfigLoader.createRobotToCamera0Transform();

  public static final String LIMELIGHT_CAMERA_0_NAME =
      RobotConfigLoader.getString("limelight.camera0.name");
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_X =
      RobotConfigLoader.getDouble("limelight.robot_to_camera0.x");
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_Y =
      RobotConfigLoader.getDouble("limelight.robot_to_camera0.y");
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_Z =
      RobotConfigLoader.getDouble("limelight.robot_to_camera0.z");
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_ROLL =
      RobotConfigLoader.getDouble("limelight.robot_to_camera0.roll");
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_PITCH =
      RobotConfigLoader.getDouble("limelight.robot_to_camera0.pitch");
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_YAW =
      RobotConfigLoader.getDouble("limelight.robot_to_camera0.yaw");

  // public static final double LIMELIGHT_MAX_AMBIGUITY;
  // public static final double LIMELIGHT_MAX_Z_ERROR;
  // public static final double LIMELIGHT_LINEAR_STD_DEV_BASELINE;
  // public static final double LIMELIGHT_ANGULAR_STD_DEV_BASELINE;
  // public static final double LIMELIGHT_CAMERA0_STD_DEV_FACTOR;
  // public static final double LIMELIGHT_CAMERA1_STD_DEV_FACTOR;
  // public static final double LIMELIGHT_LINEAR_STD_DEV_MEGATAG2_FACTOR;
  // public static final double LIMELIGHT_ANGULAR_STD_DEV_MEGATAG2_FACTOR;

  // LIMELIGHT_MAX_AMBIGUITY = RobotConfigLoader.getDouble("limelight.max_ambiguity");
  // LIMELIGHT_MAX_Z_ERROR = RobotConfigLoader.getDouble("limelight.max_z_error");
  // LIMELIGHT_LINEAR_STD_DEV_BASELINE =
  //     RobotConfigLoader.getDouble("limelight.linear_std_dev_baseline");
  // LIMELIGHT_ANGULAR_STD_DEV_BASELINE =
  //     RobotConfigLoader.getDouble("limelight.angular_std_dev_baseline");
  // LIMELIGHT_CAMERA0_STD_DEV_FACTOR =
  //     RobotConfigLoader.getDouble("limelight.camera0_std_dev_factor");
  // LIMELIGHT_CAMERA1_STD_DEV_FACTOR =
  //     RobotConfigLoader.getDouble("limelight.camera1_std_dev_factor");
  // LIMELIGHT_LINEAR_STD_DEV_MEGATAG2_FACTOR =
  //     RobotConfigLoader.getDouble("limelight.linear_std_dev_megatag2_factor");

  // Basic filtering thresholds
  public static double MAX_AMBIGUITY = 0.3;
  public static double MAX_Z_ERROR = 0.75;

  // Standard deviation baselines, for 1 meter distance and 1 tag
  // (Adjusted automatically based on distance and # of tags)
  public static double LINEAR_STD_DEV_BASELINE = 0.02; // Meters
  public static double ANGULAR_STD_DEV_BASELINE = 0.06; // Radians

  // Standard deviation multipliers for each camera
  // (Adjust to trust some cameras more than others)
  public static double[] CAMERA_STD_DEV_FACTORS =
      new double[] {
        1.0, // Camera 0
        1.0 // Camera 1
      };

  // Multipliers to apply for MegaTag 2 observations
  public static double LINEAR_STD_DEV_MEGATGAG_2_FACTOR = 0.5; // More stable than full 3D solve
  public static double ANGULAR_STD_DEV_MEGATAG_2_FACTOR =
      Double.POSITIVE_INFINITY; // No rotation data available
}

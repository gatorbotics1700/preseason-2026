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

public class VisionConstants {
  // AprilTag layout
  public static final AprilTagFieldLayout APRIL_TAG_LAYOUT =
      AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

  // Camera names, must match names configured on coprocessor
  public static final String CAMERA_0_NAME = "limelight";

  // Robot to camera transforms
  public static Transform3d ROBOT_TO_CAMERA_0 =
      new Transform3d(
          0.24, // 0.2159,
          0.263,
          0.193,
          new Rotation3d(Math.toRadians(1), Math.toRadians(23.3), Math.toRadians(-21)));

  // its -24.4 in the middle and -24 on the right thx

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
      };

  // Multipliers to apply for MegaTag 2 observations
  public static double LINEAR_STD_DEV_MEGATGAG_2_FACTOR = 0.5; // More stable than full 3D solve
  public static double ANGULAR_STD_DEV_MEGATAG_2_FACTOR =
      Double.POSITIVE_INFINITY; // No rotation data available
}

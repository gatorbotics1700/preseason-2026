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

package frc.robot;

import static edu.wpi.first.units.Units.Centimeters;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.util.RobotConfigLoader;

/**
 * This class defines the runtime mode used by AdvantageKit and loads robot-specific configuration
 * based on the roboRIO serial number. The mode is always "real" when running on a roboRIO. Change
 * the value of "simMode" to switch between "sim" (physics sim) and "replay" (log replay from a
 * file).
 */
public final class Constants {
  public static final Mode simMode = Mode.SIM;
  public static final Mode currentMode = RobotBase.isReal() ? Mode.REAL : simMode;

  // Robot identification
  public static final String ROBOT_SERIAL_NUMBER;

  // Vision Constants (loaded from config)
  public static final String LIMELIGHT_CAMERA_0_NAME;
  public static final String LIMELIGHT_CAMERA_1_NAME;
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_X;
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_Y;
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_Z;
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_ROLL;
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_PITCH;
  public static final double LIMELIGHT_ROBOT_TO_CAMERA0_YAW;
  public static final double LIMELIGHT_MAX_AMBIGUITY;
  public static final double LIMELIGHT_MAX_Z_ERROR;
  public static final double LIMELIGHT_LINEAR_STD_DEV_BASELINE;
  public static final double LIMELIGHT_ANGULAR_STD_DEV_BASELINE;
  public static final double LIMELIGHT_CAMERA0_STD_DEV_FACTOR;
  public static final double LIMELIGHT_CAMERA1_STD_DEV_FACTOR;
  public static final double LIMELIGHT_LINEAR_STD_DEV_MEGATAG2_FACTOR;
  public static final double LIMELIGHT_ANGULAR_STD_DEV_MEGATAG2_FACTOR;

  // Tuner Constants (loaded from config)
  public static final double TUNER_STEER_KP;
  public static final double TUNER_STEER_KI;
  public static final double TUNER_STEER_KD;
  public static final double TUNER_STEER_KS;
  public static final double TUNER_STEER_KV;
  public static final double TUNER_STEER_KA;
  public static final double TUNER_DRIVE_KP;
  public static final double TUNER_DRIVE_KI;
  public static final double TUNER_DRIVE_KD;
  public static final double TUNER_DRIVE_KS;
  public static final double TUNER_DRIVE_KV;
  public static final double TUNER_SLIP_CURRENT;
  public static final double TUNER_STATOR_CURRENT_LIMIT;
  public static final double TUNER_SPEED_AT_12_VOLTS;
  public static final double TUNER_DRIVE_GEAR_RATIO;
  public static final double TUNER_STEER_GEAR_RATIO;
  public static final double TUNER_COUPLE_RATIO;
  public static final double TUNER_WHEEL_RADIUS;
  public static final int TUNER_PIGEON_ID;

  // Encoder Offsets (loaded from config)
  // Easy to replace - just copy-paste new offsets here:
  // TUNER_FRONT_LEFT_ENCODER_OFFSET = <value>;
  // TUNER_FRONT_RIGHT_ENCODER_OFFSET = <value>;
  // TUNER_BACK_LEFT_ENCODER_OFFSET = <value>;
  // TUNER_BACK_RIGHT_ENCODER_OFFSET = <value>;
  public static final double TUNER_FRONT_LEFT_ENCODER_OFFSET;
  public static final double TUNER_FRONT_RIGHT_ENCODER_OFFSET;
  public static final double TUNER_BACK_LEFT_ENCODER_OFFSET;
  public static final double TUNER_BACK_RIGHT_ENCODER_OFFSET;

  // Motor IDs (loaded from config)
  public static final int TUNER_FRONT_LEFT_DRIVE_MOTOR_ID;
  public static final int TUNER_FRONT_LEFT_STEER_MOTOR_ID;
  public static final int TUNER_FRONT_LEFT_ENCODER_ID;
  public static final int TUNER_FRONT_RIGHT_DRIVE_MOTOR_ID;
  public static final int TUNER_FRONT_RIGHT_STEER_MOTOR_ID;
  public static final int TUNER_FRONT_RIGHT_ENCODER_ID;
  public static final int TUNER_BACK_LEFT_DRIVE_MOTOR_ID;
  public static final int TUNER_BACK_LEFT_STEER_MOTOR_ID;
  public static final int TUNER_BACK_LEFT_ENCODER_ID;
  public static final int TUNER_BACK_RIGHT_DRIVE_MOTOR_ID;
  public static final int TUNER_BACK_RIGHT_STEER_MOTOR_ID;
  public static final int TUNER_BACK_RIGHT_ENCODER_ID;

  // Module Positions (loaded from config)
  public static final double TUNER_FRONT_LEFT_X_POS;
  public static final double TUNER_FRONT_LEFT_Y_POS;
  public static final double TUNER_FRONT_RIGHT_X_POS;
  public static final double TUNER_FRONT_RIGHT_Y_POS;
  public static final double TUNER_BACK_LEFT_X_POS;
  public static final double TUNER_BACK_LEFT_Y_POS;
  public static final double TUNER_BACK_RIGHT_X_POS;
  public static final double TUNER_BACK_RIGHT_Y_POS;

  static {
    // Load configuration based on roboRIO serial number (auto-loads on first access)
    ROBOT_SERIAL_NUMBER = RobotConfigLoader.getSerialNumber();

    // Load Vision Constants
    LIMELIGHT_CAMERA_0_NAME = RobotConfigLoader.getString("limelight.camera0.name");
    LIMELIGHT_CAMERA_1_NAME = RobotConfigLoader.getString("limelight.camera1.name");
    LIMELIGHT_ROBOT_TO_CAMERA0_X = RobotConfigLoader.getDouble("limelight.robot_to_camera0.x");
    LIMELIGHT_ROBOT_TO_CAMERA0_Y = RobotConfigLoader.getDouble("limelight.robot_to_camera0.y");
    LIMELIGHT_ROBOT_TO_CAMERA0_Z = RobotConfigLoader.getDouble("limelight.robot_to_camera0.z");
    LIMELIGHT_ROBOT_TO_CAMERA0_ROLL =
        RobotConfigLoader.getDouble("limelight.robot_to_camera0.roll");
    LIMELIGHT_ROBOT_TO_CAMERA0_PITCH =
        RobotConfigLoader.getDouble("limelight.robot_to_camera0.pitch");
    LIMELIGHT_ROBOT_TO_CAMERA0_YAW = RobotConfigLoader.getDouble("limelight.robot_to_camera0.yaw");
    LIMELIGHT_MAX_AMBIGUITY = RobotConfigLoader.getDouble("limelight.max_ambiguity");
    LIMELIGHT_MAX_Z_ERROR = RobotConfigLoader.getDouble("limelight.max_z_error");
    LIMELIGHT_LINEAR_STD_DEV_BASELINE =
        RobotConfigLoader.getDouble("limelight.linear_std_dev_baseline");
    LIMELIGHT_ANGULAR_STD_DEV_BASELINE =
        RobotConfigLoader.getDouble("limelight.angular_std_dev_baseline");
    LIMELIGHT_CAMERA0_STD_DEV_FACTOR =
        RobotConfigLoader.getDouble("limelight.camera0_std_dev_factor");
    LIMELIGHT_CAMERA1_STD_DEV_FACTOR =
        RobotConfigLoader.getDouble("limelight.camera1_std_dev_factor");
    LIMELIGHT_LINEAR_STD_DEV_MEGATAG2_FACTOR =
        RobotConfigLoader.getDouble("limelight.linear_std_dev_megatag2_factor");

    // Handle Infinity value for angular std dev megatag2 factor
    String angularStdDevMegatag2Factor =
        RobotConfigLoader.getString("limelight.angular_std_dev_megatag2_factor");
    LIMELIGHT_ANGULAR_STD_DEV_MEGATAG2_FACTOR =
        "Infinity".equals(angularStdDevMegatag2Factor)
            ? Double.POSITIVE_INFINITY
            : Double.parseDouble(angularStdDevMegatag2Factor);

    // Load Tuner Constants
    TUNER_STEER_KP = RobotConfigLoader.getDouble("tuner.steer_kp");
    TUNER_STEER_KI = RobotConfigLoader.getDouble("tuner.steer_ki");
    TUNER_STEER_KD = RobotConfigLoader.getDouble("tuner.steer_kd");
    TUNER_STEER_KS = RobotConfigLoader.getDouble("tuner.steer_ks");
    TUNER_STEER_KV = RobotConfigLoader.getDouble("tuner.steer_kv");
    TUNER_STEER_KA = RobotConfigLoader.getDouble("tuner.steer_ka");
    TUNER_DRIVE_KP = RobotConfigLoader.getDouble("tuner.drive_kp");
    TUNER_DRIVE_KI = RobotConfigLoader.getDouble("tuner.drive_ki");
    TUNER_DRIVE_KD = RobotConfigLoader.getDouble("tuner.drive_kd");
    TUNER_DRIVE_KS = RobotConfigLoader.getDouble("tuner.drive_ks");
    TUNER_DRIVE_KV = RobotConfigLoader.getDouble("tuner.drive_kv");
    TUNER_SLIP_CURRENT = RobotConfigLoader.getDouble("tuner.slip_current");
    TUNER_STATOR_CURRENT_LIMIT = RobotConfigLoader.getDouble("tuner.stator_current_limit");
    TUNER_SPEED_AT_12_VOLTS = RobotConfigLoader.getDouble("tuner.speed_at_12_volts");
    TUNER_COUPLE_RATIO = RobotConfigLoader.getDouble("tuner.couple_ratio");
    TUNER_DRIVE_GEAR_RATIO = RobotConfigLoader.getDouble("tuner.drive_gear_ratio");
    TUNER_STEER_GEAR_RATIO = RobotConfigLoader.getDouble("tuner.steer_gear_ratio");
    TUNER_WHEEL_RADIUS = RobotConfigLoader.getDouble("tuner.wheel_radius");
    TUNER_PIGEON_ID = RobotConfigLoader.getInt("tuner.pigeon_id");

    // Load Encoder Offsets
    // To quickly update offsets, you can replace the RobotConfigLoader calls with direct values:
    // TUNER_FRONT_LEFT_ENCODER_OFFSET = -4.87;
    // TUNER_FRONT_RIGHT_ENCODER_OFFSET = -12.808;
    // TUNER_BACK_LEFT_ENCODER_OFFSET = -8.863;
    // TUNER_BACK_RIGHT_ENCODER_OFFSET = -10.491;
    TUNER_FRONT_LEFT_ENCODER_OFFSET =
        RobotConfigLoader.getDouble("tuner.front_left_encoder_offset");
    TUNER_FRONT_RIGHT_ENCODER_OFFSET =
        RobotConfigLoader.getDouble("tuner.front_right_encoder_offset");
    TUNER_BACK_LEFT_ENCODER_OFFSET = RobotConfigLoader.getDouble("tuner.back_left_encoder_offset");
    TUNER_BACK_RIGHT_ENCODER_OFFSET =
        RobotConfigLoader.getDouble("tuner.back_right_encoder_offset");

    // Load Motor IDs
    TUNER_FRONT_LEFT_DRIVE_MOTOR_ID = RobotConfigLoader.getInt("tuner.front_left_drive_motor_id");
    TUNER_FRONT_LEFT_STEER_MOTOR_ID = RobotConfigLoader.getInt("tuner.front_left_steer_motor_id");
    TUNER_FRONT_LEFT_ENCODER_ID = RobotConfigLoader.getInt("tuner.front_left_encoder_id");
    TUNER_FRONT_RIGHT_DRIVE_MOTOR_ID = RobotConfigLoader.getInt("tuner.front_right_drive_motor_id");
    TUNER_FRONT_RIGHT_STEER_MOTOR_ID = RobotConfigLoader.getInt("tuner.front_right_steer_motor_id");
    TUNER_FRONT_RIGHT_ENCODER_ID = RobotConfigLoader.getInt("tuner.front_right_encoder_id");
    TUNER_BACK_LEFT_DRIVE_MOTOR_ID = RobotConfigLoader.getInt("tuner.back_left_drive_motor_id");
    TUNER_BACK_LEFT_STEER_MOTOR_ID = RobotConfigLoader.getInt("tuner.back_left_steer_motor_id");
    TUNER_BACK_LEFT_ENCODER_ID = RobotConfigLoader.getInt("tuner.back_left_encoder_id");
    TUNER_BACK_RIGHT_DRIVE_MOTOR_ID = RobotConfigLoader.getInt("tuner.back_right_drive_motor_id");
    TUNER_BACK_RIGHT_STEER_MOTOR_ID = RobotConfigLoader.getInt("tuner.back_right_steer_motor_id");
    TUNER_BACK_RIGHT_ENCODER_ID = RobotConfigLoader.getInt("tuner.back_right_encoder_id");

    // Load Module Positions
    TUNER_FRONT_LEFT_X_POS = RobotConfigLoader.getDouble("tuner.front_left_x_pos");
    TUNER_FRONT_LEFT_Y_POS = RobotConfigLoader.getDouble("tuner.front_left_y_pos");
    TUNER_FRONT_RIGHT_X_POS = RobotConfigLoader.getDouble("tuner.front_right_x_pos");
    TUNER_FRONT_RIGHT_Y_POS = RobotConfigLoader.getDouble("tuner.front_right_y_pos");
    TUNER_BACK_LEFT_X_POS = RobotConfigLoader.getDouble("tuner.back_left_x_pos");
    TUNER_BACK_LEFT_Y_POS = RobotConfigLoader.getDouble("tuner.back_left_y_pos");
    TUNER_BACK_RIGHT_X_POS = RobotConfigLoader.getDouble("tuner.back_right_x_pos");
    TUNER_BACK_RIGHT_Y_POS = RobotConfigLoader.getDouble("tuner.back_right_y_pos");
  }

  public static enum Mode {
    /** Running on a real robot. */
    REAL,

    /** Running a physics simulator. */
    SIM,

    /** Replaying from a log file. */
    REPLAY
  }

  public static final int KRAKEN_TICKS_PER_REV = 2048;

  public static final Distance CENTER_TO_BUMPER_OFFSET = Centimeters.of(40);
  // left and right offsets for the poles on the reef
  public static final Distance CENTER_TO_POLE_OFFSET = Centimeters.of(16.5);
  public static final Distance ROBOT_RADIUS_WITH_BUMPERS = Centimeters.of(57);
}

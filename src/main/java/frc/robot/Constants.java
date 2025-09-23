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

import edu.wpi.first.wpilibj.RobotBase;

/**
 * This class defines the runtime mode used by AdvantageKit. The mode is always "real" when running
 * on a roboRIO. Change the value of "simMode" to switch between "sim" (physics sim) and "replay"
 * (log replay from a file).
 */
public final class Constants {
  public static final Mode simMode = Mode.SIM;
  public static final Mode currentMode = RobotBase.isReal() ? Mode.REAL : simMode;

  public static enum Mode {
    /** Running on a real robot. */
    REAL,

    /** Running a physics simulator. */
    SIM,

    /** Replaying from a log file. */
    REPLAY
  }

  public static final int KRAKEN_TICKS_PER_REV = 2048;

  // elevator mechanisms
  public static final int ELEVATOR_CAN_ID = 17;
  public static final double ELEVATOR_SPROCKET_TEETH = 16; // inches
  public static final double ELEVATOR_GEAR_RATIO = 27.0;
  public static final double ELEVATOR_INCHES_PER_SPROCKET_TOOTH = 0.25;
  public static final double ELEVATOR_TICKS_PER_INCH =
      KRAKEN_TICKS_PER_REV
          * ELEVATOR_GEAR_RATIO
          / ELEVATOR_SPROCKET_TEETH
          / ELEVATOR_INCHES_PER_SPROCKET_TOOTH;
  public static final double ELEVATOR_INCHES_PER_TICK = 1 / ELEVATOR_TICKS_PER_INCH;
  public static final int TOP_LIMIT_SWITCH_PORT = 5;
  public static final int BOTTOM_LIMIT_SWITCH_PORT = 1;

  // pass through mechanism
  public static final int PASS_THROUGH_MOTOR_1_CAN_ID = 0; // TODO: set
  public static final int PASS_THROUGH_MOTOR_2_CAN_ID = 0; // TODO: set

  // beambreak ports
  public static final int RECEIVER_PORT = 3;
  public static final int TRANSMITTER_PORT = 2;

  // in inches
  public static final double INTAKE_HEIGHT = 22.6;
  public static final double L1_HEIGHT = 23.75;
  public static final double L2_HEIGHT = 34.14;
  public static final double L3_HEIGHT =
      49; // should be max height of elevator, maybe more than 49 so it caps off limitswitch

  public static final double OUT_IN_VOLTAGE = 0; // TODO: replace 0 with actual value
}

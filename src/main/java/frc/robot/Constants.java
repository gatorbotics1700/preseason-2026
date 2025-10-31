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

/**
 * This class defines the runtime mode used by AdvantageKit. The mode is always "real" when running
 * on a roboRIO. Change the value of "simMode" to switch between "sim" (physics sim) and "replay"
 * (log replay from a file).
 */
public final class Constants {

  public static final int SHOOTER_MOTOR_TOP_LEFT_CAN_ID = 30;
  public static final int SHOOTER_MOTOR_TOP_RIGHT_CAN_ID = 32;
  public static final double CORAL_L4_SHOOTING_VOLTAGE = -6;
  public static final double CORAL_TROUGH_SHOOTING_VOLTAGE = -4;
  public static final String CANIVORE_BUS_NAME = "TRex";
  public static final double VOLTAGE_BC_IM_ANGRY = 2;
  public static final double VOLTAGE_VOMIT = -1;
}

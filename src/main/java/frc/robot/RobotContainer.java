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

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.CoralShooterCommand;
import frc.robot.subsystems.CoralShooterSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Subsystems
  // private final Drive drive;

  // Controller
  private final XboxController controller = new XboxController(0);

  private final CoralShooterSubsystem m_coralShooterSub = new CoralShooterSubsystem();

  public RobotContainer() {
    new Trigger(controller::getAButtonPressed)
        .onTrue(new CoralShooterCommand(m_coralShooterSub, Constants.CORAL_L4_SHOOTING_VOLTAGE));
    new Trigger(controller::getBButtonPressed)
        .onTrue(new CoralShooterCommand(m_coralShooterSub, 0));
    new Trigger(controller::getXButtonPressed)
        .onTrue(new CoralShooterCommand(m_coralShooterSub, Constants.VOLTAGE_BC_IM_ANGRY));
  }
}

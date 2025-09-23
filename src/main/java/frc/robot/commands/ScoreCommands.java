package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.PassThroughSubsystem;

public class ScoreCommands {
  public static Command Level(
      int level, ElevatorSubsystem m_elevatorSub, PassThroughSubsystem m_passThroughSubsystem) {
    System.out.println("IN SCORE COMMAND!!!");
    if (level == 1) {
      return Trough(m_elevatorSub, m_passThroughSubsystem);
    } else if (level == 2) {
      return LevelTwo(m_elevatorSub, m_passThroughSubsystem);
    } else if (level == 3) {
      return LevelThree(m_elevatorSub, m_passThroughSubsystem);
    }

    return Commands.waitSeconds(2); // if all else fails, return a wait command
  }

  public static Command Trough(
      ElevatorSubsystem elevatorSubsystem, PassThroughSubsystem passThroughSubsystem) {
    System.out.println("TROUGH!");
    return new ElevatorCommand(elevatorSubsystem, Constants.L1_HEIGHT)
        .andThen(
            new PassThroughCommand(
                passThroughSubsystem, Constants.OUT_IN_VOLTAGE, passThroughSubsystem.OUTTAKING));
  }

  public static Command LevelTwo(
      ElevatorSubsystem elevatorSubsystem, PassThroughSubsystem passThroughSubsystem) {
    System.out.println("LEVEL 2!");
    return new ElevatorCommand(elevatorSubsystem, Constants.L2_HEIGHT)
        .andThen(
            new PassThroughCommand(
                passThroughSubsystem, Constants.OUT_IN_VOLTAGE, passThroughSubsystem.OUTTAKING));
  }

  public static Command LevelThree(
      ElevatorSubsystem elevatorSubsystem, PassThroughSubsystem passThroughSubsystem) {
    System.out.println("LEVEL 3!");
    return new ElevatorCommand(elevatorSubsystem, Constants.L3_HEIGHT)
        .andThen(
            new PassThroughCommand(
                passThroughSubsystem, Constants.OUT_IN_VOLTAGE, passThroughSubsystem.OUTTAKING));
  }

  public static Command Intake(
      ElevatorSubsystem elevatorSubsystem, PassThroughSubsystem passThroughSubsystem) {
    System.out.println("INTAKING!");
    return new ElevatorCommand(elevatorSubsystem, Constants.L1_HEIGHT)
        .andThen(
            new PassThroughCommand(
                passThroughSubsystem, Constants.OUT_IN_VOLTAGE, passThroughSubsystem.INTAKING));
  }
}

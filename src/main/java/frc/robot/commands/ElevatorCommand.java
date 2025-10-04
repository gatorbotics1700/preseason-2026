package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorCommand extends Command {

  private ElevatorSubsystem elevatorSubsystem;
  // private double desiredHeight; // in inches!
  private double desiredHeight;

  private double deadband = 1; // 1 inch in ticks; TODO: change this value

  public ElevatorCommand(
      ElevatorSubsystem elevatorSubsystem, double desiredHeight /*double desiredHeight*/) {
    this.elevatorSubsystem = elevatorSubsystem;
    // this.desiredHeight = desiredHeight;
    this.desiredHeight = desiredHeight;
    addRequirements(elevatorSubsystem);
  }

  @Override
  public void initialize() {
    // desiredTicks = elevatorSubsystem.determineInchesToTicks(desiredHeight);
    // System.out.println("setting isUsingPos to " + isUsingPos);
    // desiredInches = elevatorSubsystem.getCurrentInches() + desiredChange;
    elevatorSubsystem.setSetPoint(desiredHeight);
  }

  @Override
  public void execute() {
    System.out.println("ELEVATOR COMMAND TRIGGERED");
  }

  @Override
  public boolean isFinished() {
    // CHECK TO SEE IF CURRENT POSITION IS AT
    if (DriverStation.isDisabled()) {
      return true;
    }

    System.out.println(Math.abs(desiredHeight - elevatorSubsystem.getCurrentInches()));
    if (Math.abs(desiredHeight - elevatorSubsystem.getCurrentInches()) <= deadband) {
      System.out.println("FINISHING COMMAND");
      return true;
    }
    if (elevatorSubsystem.atTopLimitSwitch()) {
      return true;
    }
    return false;
  }
}

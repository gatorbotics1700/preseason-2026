package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorCommand extends Command {

  private ElevatorSubsystem elevatorSubsystem;
  // private double desiredHeight; // in inches!
  private double desiredChange;
  private double desiredInches;

  private double deadband = 1; // 1 inch in ticks; TODO: change this value

  public ElevatorCommand(
      ElevatorSubsystem elevatorSubsystem, double desiredChange /*double desiredHeight*/) {
    this.elevatorSubsystem = elevatorSubsystem;
    // this.desiredHeight = desiredHeight;
    this.desiredChange = desiredChange;
    addRequirements(elevatorSubsystem);
  }

  @Override
  public void initialize() {
    // desiredTicks = elevatorSubsystem.determineInchesToTicks(desiredHeight);
    // System.out.println("setting isUsingPos to " + isUsingPos);
    desiredInches = elevatorSubsystem.getCurrentInches() + desiredChange;
    elevatorSubsystem.setSetPoint(desiredInches);
  }

  @Override
  public void execute() {
    System.out.println("ELEVATOR COMMAND TRIGGERED");
  }

  @Override
  public boolean isFinished() {
    // CHECK TO SEE IF CURRENT POSITION IS AT
    if (DriverStation.isDisabled()) {
      System.out.println("FINISHGINC COMMAND");
      return true;
    }
    if (Math.abs(desiredInches - elevatorSubsystem.getCurrentInches()) <= deadband) {
      System.out.println("FINISHING COMMAND");
      return true;
    }
    return false;
  }
}

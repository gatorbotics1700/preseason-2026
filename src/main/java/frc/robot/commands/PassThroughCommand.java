package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.PassThroughSubsystem;

public class PassThroughCommand extends Command {
  private PassThroughSubsystem passThroughSubsystem;
  private final double voltage;

  private final boolean state;

  public PassThroughCommand(
      PassThroughSubsystem passThroughSubsystem, double voltage, boolean state) {
    this.passThroughSubsystem = passThroughSubsystem;
    this.voltage = voltage;
    this.state = state;
    addRequirements(passThroughSubsystem);
  }

  @Override
  public void execute() {
    passThroughSubsystem.setVoltage(voltage);
    System.out.println("VOLTAGE: " + voltage);
  }

  @Override
  public boolean isFinished() {
    if (state == passThroughSubsystem.INTAKING) {
      if (passThroughSubsystem.isBeamBroken() == true) { // if coral is in beambreak
        passThroughSubsystem.setVoltage(0);
        return true;
      }
    } else if (state == passThroughSubsystem.OUTTAKING) {
      if (passThroughSubsystem.isBeamBroken() == false) { // if coral is out of beambreak
        //TODO: add a delay here
        passThroughSubsystem.setVoltage(0);
        return true;
      }
    }
    return false;
  }
}

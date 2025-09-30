package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.Drive;

public class LineupCommand extends Command {
  private Drive drivetrainSubsystem;

  public LineupCommand() {}

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return true;
  }
}

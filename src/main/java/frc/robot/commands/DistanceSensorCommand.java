package frc.robot.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DistanceSensor;
import frc.robot.subsystems.drive.Drive;

public class DistanceSensorCommand extends Command {
  private DistanceSensor distanceSensor;
  private Drive drivetrainSubsystem;
  Rotation2d rot = new Rotation2d(0);

  // private Drive drive1;

  /*private final double stopDrive; */

  public DistanceSensorCommand(Drive drive1, DistanceSensor distanceSensor1) {
    distanceSensor = distanceSensor1;
    drivetrainSubsystem = drive1;
    addRequirements(drivetrainSubsystem, distanceSensor);
  }

  @Override
  public boolean isFinished() {
    if ((distanceSensor).getDistance() < 0.9) {
      drivetrainSubsystem.runVelocity(ChassisSpeeds.fromFieldRelativeSpeeds(0.0, 0.0, 0.0, rot));

      return true;
    }
    return false;
  }
}

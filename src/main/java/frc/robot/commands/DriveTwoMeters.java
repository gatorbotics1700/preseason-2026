package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.Drive;

public class DriveTwoMeters extends Command {
  private Drive drivetrainSubsystem;

  public DriveTwoMeters(Drive drivetrainSubsystem) {
    this.drivetrainSubsystem = drivetrainSubsystem;
    addRequirements(drivetrainSubsystem);
    double currentX = drivetrainSubsystem.getPose().getX();
    double targetX = currentX + 2.0;
    Pose2d targetLocation =
        new Pose2d(
            targetX, drivetrainSubsystem.getPose().getY(), drivetrainSubsystem.getRotation());
    drivetrainSubsystem.setTargetPose(targetLocation);

    System.out.println("moved two meters");
  }

  @Override
  public void execute() {
    System.out.println("XXXXXX-actually moving two meters-XXXXXX");
    System.out.println(drivetrainSubsystem.getPose());
    System.out.println("target: " + drivetrainSubsystem.getTargetPose());
    drivetrainSubsystem.runVelocity(
        ChassisSpeeds.fromFieldRelativeSpeeds(1.5, 0, 0, drivetrainSubsystem.getRotation()));
  }

  @Override
  public boolean isFinished() {
    if (Math.abs(
            drivetrainSubsystem.getPose().getX() - (drivetrainSubsystem.getTargetPose().getX()))
        < 0.02) {
      drivetrainSubsystem.runVelocity(new ChassisSpeeds(0, 0, 0));
      return true;
    }
    return false;
  }
}

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import frc.robot.Constants;

public interface DrivetrainSubsystemInterface extends Subsystem {

  double MAX_VELOCITY_METERS_PER_SECOND =
      6380
          / 60
          * SdsModuleConfigurations.MK4_L2.getDriveReduction()
          * SdsModuleConfigurations.MK4_L2.getWheelDiameter()
          * Math.PI;
  double MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND =
      MAX_VELOCITY_METERS_PER_SECOND
          / Math.hypot(
              Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
              Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0);
  double MAX_ACCELERATION = 11.0;

  void setSlowDrive();

  boolean getSlowDrive();

  void zeroGyroscope();

  void robotRelativeHeading(double offsetAngle);

  Pose2d getPose();

  void resetPose(Pose2d pose);

  SwerveModulePosition[] getModulePositionArray();

  Rotation2d getRotation();

  double getRobotRotationDegrees();

  ChassisSpeeds getRobotRelativeSpeeds();

  SwerveModuleState[] getModuleStates();

  void setStates(SwerveModuleState[] targetStates);

  void drive(ChassisSpeeds chassisSpeeds);

  void driveRobotRelative(ChassisSpeeds robotRelativeSpeeds);

  void periodic();

  void driveToPose(Pose2d desiredPose);

  void turnToAngle(Rotation2d desiredAngle);

  // starts out pointing at apriltag, then turns to be parallel with the tag once it's close enough
  void driveToPoseWithInitialAngle(Pose2d desiredPose, Rotation2d pointingToTagAngle);

  boolean getAtDesiredPose();

  void setNotAtDesiredPose();

  Rotation2d angleToPoint(double deltaX, double deltaY);

  void facePoint(Translation2d target);

  void toggleRobotRelativeDrive();

  void driveADirection(double direction);
}

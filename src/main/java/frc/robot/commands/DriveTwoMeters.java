package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.Drive;

/**
 * A simple PID-based drive command that drives the robot 2 meters forward.
 *
 * <p>This command demonstrates basic PID control tuning for new FRC team members.
 *
 * <p>PID stands for: - P (Proportional): How strongly to respond to the current error - I
 * (Integral): How strongly to respond to accumulated error over time - D (Derivative): How strongly
 * to respond to the rate of change of error
 *
 * <p>Tuning Process: 1. Start with only P (set Ki and Kd to 0) 2. Increase P until the robot
 * oscillates, then reduce by ~50% 3. Add D to reduce overshoot and oscillations 4. Add I only if
 * there's steady-state error (robot doesn't reach target)
 *
 * <p>Current values are manually tuned starting points. Adjust as needed!
 */
public class DriveTwoMeters extends Command {
  private final Drive drivetrainSubsystem;

  // Manually tuned PID constants
  private static final double PID_KP = 25.0; // Proportional gain - controls response strength
  private static final double PID_KI = 0.0; // Integral gain - handles steady-state error
  private static final double PID_KD = 0.0; // Derivative gain - reduces overshoot

  // Tolerance for reaching the target (in meters)
  private static final double DEADBAND = 0.05;

  // Maximum output speed (in meters per second) to prevent overshooting
  private static final double MAX_OUTPUT_SPEED = 6.0;

  private final PIDController pidController;
  private final double startX;
  private final double targetX;

  public DriveTwoMeters(Drive drivetrainSubsystem) {
    this.drivetrainSubsystem = drivetrainSubsystem;
    addRequirements(drivetrainSubsystem);

    // Get starting position
    Pose2d startPose = drivetrainSubsystem.getPose();
    startX = startPose.getX();

    // Calculate target position (2 meters forward in the X direction)
    targetX = startX + 2.0;

    // Create PID controller with manually tuned values
    pidController = new PIDController(PID_KP, PID_KI, PID_KD);

    // Set tolerance - command will finish when within this distance
    pidController.setTolerance(DEADBAND);

    System.out.println("DriveTwoMeters: Starting at X=" + startX + ", Target X=" + targetX);
    System.out.println("PID Values - Kp: " + PID_KP + ", Ki: " + PID_KI + ", Kd: " + PID_KD);
  }

  @Override
  public void initialize() {
    // Reset PID controller when command starts
    // Setpoint is the target X position
    pidController.reset();
    pidController.setSetpoint(targetX);

    System.out.println("DriveTwoMeters: Command initialized");
  }

  @Override
  public void execute() {
    // Get current position
    double currentX = drivetrainSubsystem.getPose().getX();

    // Calculate PID output (this is the speed we want to drive)
    double pidOutput = pidController.calculate(currentX);

    // Limit the output to prevent overshooting
    double outputSpeed = Math.max(-MAX_OUTPUT_SPEED, Math.min(MAX_OUTPUT_SPEED, pidOutput));

    // Drive forward in the X direction (robot's current heading)
    // We use field-relative speeds to maintain the robot's orientation
    drivetrainSubsystem.runVelocity(
        ChassisSpeeds.fromFieldRelativeSpeeds(
            outputSpeed,
            0.0, // No Y movement
            0.0, // No rotation
            drivetrainSubsystem.getRotation()));

    // Optional: Print debug info for tuning
    // System.out.println("Current: " + currentX + ", Target: " + targetX +
    //                    ", Output: " + outputSpeed);
  }

  @Override
  public boolean isFinished() {
    // Command finishes when PID controller is at setpoint (within tolerance)
    boolean atTarget = pidController.atSetpoint();

    if (atTarget) {
      System.out.println(
          "DriveTwoMeters: Reached target! Final X=" + drivetrainSubsystem.getPose().getX());
    }

    return atTarget;
  }

  @Override
  public void end(boolean interrupted) {
    // Stop the robot when command ends
    drivetrainSubsystem.runVelocity(new ChassisSpeeds(0, 0, 0));

    if (interrupted) {
      System.out.println("DriveTwoMeters: Command interrupted");
    } else {
      System.out.println("DriveTwoMeters: Command completed successfully");
    }
  }
}

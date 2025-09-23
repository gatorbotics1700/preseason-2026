package frc.robot.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.TurretSubsystem;

public class TurretCommand extends Command {

  private TurretSubsystem turretSubsystem;
  private Rotation2d desiredAngle;
  private Translation2d desiredPoint;

  public TurretCommand(TurretSubsystem turretSubsystem, Rotation2d desiredAngle) {
    this.turretSubsystem = turretSubsystem;
    this.desiredAngle = desiredAngle;
    addRequirements(turretSubsystem);
  }

  public TurretCommand(TurretSubsystem turretSubsystem, Translation2d desiredPoint) {
    this.turretSubsystem = turretSubsystem;
    this.desiredPoint = desiredPoint;
    addRequirements(turretSubsystem);
  }

  @Override
  public void initialize() {
    if (desiredAngle != null) {
      turretSubsystem.setUseAngle(true);
    } else {
      turretSubsystem.setUseAngle(false);
    }
  }

  @Override
  public void execute() {
    if (turretSubsystem.getUseAngle()) {
      turretSubsystem.turnToAngle(desiredAngle);
    } else {
      turretSubsystem.setTargetPoint(desiredPoint);
    }
  }

  @Override
  public boolean isFinished() {
    Rotation2d currentAngle = turretSubsystem.getTurretAngle();

    Rotation2d error = desiredAngle.minus(currentAngle);
    if (Math.abs(error.getDegrees()) < Constants.TURRET_DEADBAND) {
      System.out.println("REACHED TARGET");
      turretSubsystem.setSpeed(0);
      turretSubsystem.setUseAngle(false);
      return true;
    }
    return false;
  }
}

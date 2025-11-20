package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystem;

public class TurretCommand extends Command {
  private TurretSubsystem turretSubsystem;
  private final double degreesToTurn;
  private double setPoint;
  private PIDController pidController;
  private static final double kP = 0.004;
  private static final double kI = 0;
  private static final double kD = 0;

  public TurretCommand(TurretSubsystem turretSubsystem, double degreesToTurn) {
    this.turretSubsystem = turretSubsystem;
    this.degreesToTurn = degreesToTurn;
    addRequirements(turretSubsystem);

    pidController = new PIDController(kP, kI, kD);
  }

  @Override
  public void initialize() {
    System.out.println("INITIALIZING");
    setPoint = turretSubsystem.getPosition() + degreesToTurn;
    System.out.println("STARTING POSITION:" + turretSubsystem.getPosition());
    System.out.println("SET POINT =" + setPoint);
  }

  @Override
  public void execute() {
    if (Math.abs(turretSubsystem.getPosition() - setPoint) > 2) {
      double output = pidController.calculate(turretSubsystem.getPosition() - setPoint);
      System.out.println("OUTPUT VOLTAGE:" + output);
      System.out.println("CURRENT POSITION:" + turretSubsystem.getPosition());
      turretSubsystem.setSpeed(output);
    } else {
      turretSubsystem.setMotorVoltage(0);
    }
  }

  @Override
  public boolean isFinished() {
    if (Math.abs(turretSubsystem.getPosition() - setPoint) < 2) {
      System.out.println("IS FINISHED STOPPING");
      turretSubsystem.setMotorVoltage(0);
      return true;
    }
    return false;
  }
}

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.TurretSubSystem;
import edu.wpi.first.math.controller.PIDController;

public class TurretCommand extends Command {
  private TurretSubSystem turretSubSystem;
  private PIDController pidController;
  private double startTime;
private double endPosition;
private double degrees;
    private static final double kP = 0.2;
    private static final double kI = 0;
    private static final double kD = 0;

  public TurretCommand(TurretSubSystem turretSubSystem, double degrees) {
    this.turretSubSystem = turretSubSystem;
    this.degrees = degrees;
    addRequirements(turretSubSystem);
    System.out.println("NEW TURRET COMMAND :D");
    pidController = new PIDController(kP, kI, kD);
  }

  @Override
  public void initialize() {
    endPosition = turretSubSystem.getTurretDegrees() + degrees;
  }

  @Override
  public void execute() {
    if (Math.abs(turretSubSystem.getTurretDegrees() - endPosition)>=2){
        double output=pidController.calculate(turretSubSystem.getTurretDegrees()- endPosition);
        turretSubSystem.setSpeed(output);
    }
    else{
        turretSubSystem.setMotorVoltage(0);
    }
}

  @Override
  public boolean isFinished() {
    if (Math.abs(turretSubSystem.getTurretDegrees()- endPosition)<=2){
        turretSubSystem.setMotorVoltage(0);
        return true; 
    }
    else {
        return false;
    }
      }
}
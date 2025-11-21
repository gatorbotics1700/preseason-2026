package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.generated.TunerConstants;

public class TurretSubSystem extends SubsystemBase {
  private final TalonFX turretMotor;

  private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);

  public TurretSubSystem() {
    turretMotor = new TalonFX(Constants.TURRET_MOTOR_CAN_ID, TunerConstants.kCANBus);
  }

  public void setMotorVoltage(double voltage) {
    turretMotor.setVoltage(voltage);
  }

  public double getTurretDegrees() {
    return (turretMotor.getPosition().getValueAsDouble() / Constants.TURRET_GEAR_RATIO * 360) % 360;
  }

  public void setSpeed(double speed) {
    turretMotor.setControl(dutyCycleOut.withOutput(speed));
  }
}

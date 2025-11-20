package frc.robot.subsystems;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.generated.TunerConstants;

public class TurretSubsystem extends SubsystemBase {

  public final TalonFX motor;
  private static double voltage;

  private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);

  public TurretSubsystem() {
    motor = new TalonFX(Constants.TURRET_MOTOR_CAN_ID, TunerConstants.kCANBus);

    motor
        .getConfigurator()
        .apply(
            new TalonFXConfiguration()
                .withMotorOutput(
                    new MotorOutputConfigs().withInverted(InvertedValue.Clockwise_Positive)));

    voltage = 0;
  }

  @Override
  public void periodic() {}

  public void setMotorVoltage(double voltage) {
    motor.setVoltage(voltage);
  }

  public void setSpeed(double speed) {
    motor.setControl(dutyCycleOut.withOutput(speed));
  }

  public double getPosition() {
    return motor.getPosition().getValueAsDouble() * 360 / Constants.TURRET_GEAR_RATIO;
  }
}

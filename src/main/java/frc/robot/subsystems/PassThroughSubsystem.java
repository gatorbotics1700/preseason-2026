package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import org.littletonrobotics.junction.networktables.LoggedNetworkBoolean;

public class PassThroughSubsystem extends SubsystemBase {
  private TalonFX motor1;
  private TalonFX motor2;
  // private SparkMax motor1;
  // private SparkMax motor2;
  private double voltage;

  LoggedNetworkBoolean beamBreakState = new LoggedNetworkBoolean("/beamBreak/state", false);

  private final int receiverPort = Constants.RECEIVER_PORT;
  private final int transmitterPort = Constants.TRANSMITTER_PORT;

  public final boolean INTAKING = true;
  public final boolean OUTTAKING = false;

  DigitalInput receiver = new DigitalInput(receiverPort);
  DigitalOutput transmitter = new DigitalOutput(transmitterPort);

  public PassThroughSubsystem() {
    motor1 = new TalonFX(Constants.PASS_THROUGH_MOTOR_1_CAN_ID);
    motor2 = new TalonFX(Constants.PASS_THROUGH_MOTOR_2_CAN_ID);
    // motor1 = new SparkMax(Constants.PASS_THROUGH_MOTOR_1_CAN_ID, MotorType.kBrushless);
    // motor2 = new SparkMax(Constants.PASS_THROUGH_MOTOR_2_CAN_ID, MotorType.kBrushless);
  }

  @Override
  public void periodic() {
    transmitter.set(true);
    beamBreakState.set(receiver.get());
    System.out.println("beambreak value: " + receiver.get());
  }

  public void setVoltage(double voltage) {
    this.voltage = voltage;
    System.out.println("Setting motors to: " + this.voltage);
    motor1.setVoltage(this.voltage);
    motor2.setVoltage(
        -(this.voltage)); // TODO: check which motor needs to spin in the other direction
  }

  public double getVoltage() {
    return voltage;
  }

  public boolean
      isBeamBroken() { // TODO: Get beambreak code from the beambreak branch and fix this later
    if (receiver.get()) { // whatever lets us know if beambreak has coral in it
      return true;
    }
    return false;
  }
}

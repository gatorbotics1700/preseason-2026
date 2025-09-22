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
  private double voltage;

  LoggedNetworkBoolean beamBreakState = new LoggedNetworkBoolean("/beamBreak/state", false);

  private final int receiverPort = Constants.RECEIVER_PORT;
  private final int transmitterPort = Constants.TRANSMITTER_PORT;

  public final boolean INTAKING = true;
  public final boolean OUTTAKING = false;
  
  DigitalInput receiver = new DigitalInput(receiverPort);
  DigitalOutput transmitter = new DigitalOutput(transmitterPort);

  PassThroughSubsystem() {
    motor1 = new TalonFX(Constants.PASS_THROUGH_MOTOR_1_CAN_ID);
    motor2 = new TalonFX(Constants.PASS_THROUGH_MOTOR_2_CAN_ID);
  }

  @Override
  public void periodic(){
    transmitter.set(true);
    beamBreakState.set(receiver.get());
  }

  public void setMotorVoltage(double voltage){
    this.voltage = voltage;
    System.out.println("Setting motors to: " + this.voltage);
    motor1.setVoltage(this.voltage);
    motor2.setVoltage(-(this.voltage)); //TODO: check which motor needs to spin in the other direction
    
  }

  public double getVoltage(){
    return voltage;
  }

  public boolean isBeamBroken(){ //TODO: Get beambreak code from the beambreak branch and fix this later
    if(receiver.get()){
        return true;
    }
    return false;
  }
} 


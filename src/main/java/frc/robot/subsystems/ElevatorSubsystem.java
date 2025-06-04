package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ElevatorSubsystem extends SubsystemBase {
    private TalonFX motor;
    private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);
    
    public ElevatorSubsystem(){
        motor = new TalonFX(Constants.ELEVATOR_CAN_ID);
        motor.setNeutralMode(NeutralModeValue.Brake);
    }

    @Override
    public void periodic(){
        
    }

    public void setSpeed(double speed){
        motor.setControl(dutyCycleOut.withOutput(speed));
    }
}

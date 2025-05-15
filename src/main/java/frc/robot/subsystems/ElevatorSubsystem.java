package frc.robot.subsystems;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
// import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ElevatorSubsystem extends SubsystemBase {
    private TalonFX motor;
    private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);
    //private static PositionVoltage positionVoltage = new PositionVoltage(0);
    
    private final DigitalInput topLimitSwitch;
    private final DigitalInput bottomLimitSwitch;

    private final PIDController elevatorPIDController;

    // TODO: test and change these values
    private static final double kP = 0.0005;
    private static final double kI = 0.0;
    private static final double kD = 0.00002;

    private static final double DEADBAND = 0.5 * Constants.ELEVATOR_TICKS_PER_INCH; // 1 inch in ticks; TODO: test and change


    public ElevatorSubsystem(){
        motor = new TalonFX(Constants.ELEVATOR_CAN_ID);
        motor.setNeutralMode(NeutralModeValue.Brake);
        
        motor.getConfigurator().apply(new TalonFXConfiguration()
            .withMotorOutput(new MotorOutputConfigs()
                .withInverted(InvertedValue.Clockwise_Positive))); 

        elevatorPIDController = new PIDController(kP, kI, kD);

        topLimitSwitch = new DigitalInput(Constants.TOP_LIMIT_SWITCH_PORT);
        bottomLimitSwitch = new DigitalInput(Constants.BOTTOM_LIMIT_SWITCH_PORT);
    }

    @Override
    public void periodic(){
        SmartDashboard.putBoolean("top limit switch", topLimitSwitch.get());
        SmartDashboard.putBoolean("bottom limit switch", bottomLimitSwitch.get());
        SmartDashboard.putNumber("elevator current", getMotorStatorCurrent());
    }

    public void setPosition(double desiredTicks) {
        double currentTicks = getCurrentTicks(); // in case the motor's positive and negative is reversed due to invert
        System.out.println("CURRENT INCHES: " + currentTicks / Constants.ELEVATOR_TICKS_PER_INCH);
        double error = desiredTicks - currentTicks;
        System.out.println("ERROR: " + error / Constants.ELEVATOR_TICKS_PER_INCH);
        if(Math.abs(error) > DEADBAND) {
            double output = elevatorPIDController.calculate(currentTicks, desiredTicks);
            System.out.println("ELEVATOR CURRENT PEAKED");
            System.out.println("CALCULATED OUTPUT: " + output);
            motor.setControl(dutyCycleOut.withOutput(output/100));
        } else {
            motor.setControl(dutyCycleOut.withOutput(0));
        }
    }

    public void setSpeed(double speed){
        if(getMotorStatorCurrent() > 1000){ // TODO: set current limit value
            System.out.println("ELEVATOR CURRENT PEAKED");
            speed = 0;
        }
        motor.setControl(dutyCycleOut.withOutput(speed));
    }

    public double getCurrentTicks(){ //getPosition() is in rotations so rotations * ticks per rev should give position in ticks
        return motor.getPosition().getValueAsDouble() * Constants.KRAKEN_TICKS_PER_REV;
    }

    public double determineInchesToTicks(double desiredInches){
        return desiredInches * Constants.ELEVATOR_TICKS_PER_INCH;
    }

    public double getMotorStatorCurrent(){
        return motor.getStatorCurrent().getValueAsDouble();
    }

    public boolean atTopLimitSwitch(){
        return topLimitSwitch.get();
    }

    public boolean atBottomLimitSwitch(){
        return bottomLimitSwitch.get();
    }
}
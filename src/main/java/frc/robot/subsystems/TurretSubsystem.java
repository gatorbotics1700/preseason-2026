package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class TurretSubsystem extends SubsystemBase {
    
    public final TalonFX motor;

    private final PIDController pidController;

    private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);

    private static final double kP = 0.0;
    private static final double kI = 0.0;
    private static final double kD = 0.0;

    private double speed;

    public TurretSubsystem() {
        motor = new TalonFX(Constants.TURRET_MOTOR_CAN_ID);
        
        pidController = new PIDController(kP, kI, kD);

        speed = 0.0;
    }

    public void turnToAngle(double desiredAngle){
        desiredAngle = desiredAngle % 360;
        double currentAngle = getTurretAngle();
        double error = currentAngle - desiredAngle;

        if (Math.abs(error) > Constants.TURRET_DEADBAND) {
            double output = pidController.calculate(currentAngle, desiredAngle);
            System.out.println("CALCULATED OUTPUT: " + output);
            motor.setControl(dutyCycleOut.withOutput(output));
        } else {
            System.out.println("REACHED TARGET");
            motor.setControl(dutyCycleOut.withOutput(0));
        }
    }

    public double getTurretAngle(){
        return ((motor.getPosition().getValueAsDouble() / Constants.KRAKEN_TICKS_PER_REV) * 360) % 360;
    }

    public void setSpeed(double speed) {
        motor.setControl(dutyCycleOut.withOutput(speed));
    }
}

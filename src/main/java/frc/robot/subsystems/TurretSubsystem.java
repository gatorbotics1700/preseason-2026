package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class TurretSubsystem extends SubsystemBase {
    
    public final TalonFX motor;

    private final PIDController pidController;

    private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);

    private static final double kP = 0.005;
    private static final double kI = 0.0;
    private static final double kD = 0.0005;

    private double speed;

    public TurretSubsystem() {
        motor = new TalonFX(Constants.TURRET_MOTOR_CAN_ID);
        
        pidController = new PIDController(kP, kI, kD);

        speed = 0.0;
        System.out.println("STARTING ANGLE: " + getTurretAngle());
    }

    public void turnToAngle(double desiredAngle){
        desiredAngle = desiredAngle % 360;
        double currentAngle = getTurretAngle();
        System.out.println("CURRENT ANGLE: " + currentAngle);
        double error = currentAngle - desiredAngle;
        //if (Math.abs(error) > 180)
        error = MathUtil.inputModulus(error, -180, 180);
        System.out.println("ERROR: " + error);


        if (Math.abs(error) > Constants.TURRET_DEADBAND) {
            double output = pidController.calculate(error);
          //  double output = pidController.calculate(angleToTicks(currentAngle), angleToTicks(desiredAngle));
            System.out.println("CALCULATED OUTPUT: " + output);
            System.out.println("TURNING TO DESIRED ANGLE");
            setSpeed(output);
        } else {
            System.out.println("REACHED TARGET");
            setSpeed(0);
        }
    }

    

    public double getTurretAngle(){
        System.out.println("CURRENT POSITION (TICKS): " + motor.getPosition().getValueAsDouble());
        return (motor.getPosition().getValueAsDouble()/*  / Constants.KRAKEN_TICKS_PER_REV)*/ * 360 / Constants.TURRET_GEAR_RATIO) % 360;
    }

    public void setSpeed(double speed) {
        System.out.println("SETTING SPEED TO: " + speed);
        motor.setControl(dutyCycleOut.withOutput(speed));
    }

    public double angleToTicks(double degrees) {
        return ((degrees % 360) / 360) * Constants.TURRET_GEAR_RATIO; // * Constants.KRAKEN_TICKS_PER_REV;
    }
}
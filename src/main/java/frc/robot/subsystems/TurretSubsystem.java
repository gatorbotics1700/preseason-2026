package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import java.util.function.Supplier;

public class TurretSubsystem extends SubsystemBase {

  public final TalonFX motor;

  private final PIDController pidController;

  private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);

  private static final double kP = 0.025;
  private static final double kI = 0.0;
  private static final double kD = 0.0;
  private Translation2d target = new Translation2d(0, 0);
  private boolean useAngle;
  private double speed;

  private Supplier<Pose2d> robotPose;

  public TurretSubsystem(Supplier<Pose2d> robotPose) {
    motor = new TalonFX(Constants.TURRET_MOTOR_CAN_ID);
    this.robotPose = robotPose;
    pidController = new PIDController(kP, kI, kD);
    useAngle = false;
    // motor.getConfigurator().apply(new TalonFXConfiguration().withMotorOutput(new
    // MotorOutputConfigs().withInverted(InvertedValue.Clockwise_Positive)));

    speed = 0.0;
    // System.out.println("STARTING ANGLE: " + getTurretAngle());
  }

  public void setTargetPoint(Translation2d targetPoint) {
    target = targetPoint;
  }

  public void periodic() {
    if (useAngle == false) {
      turnToAngle(getTargetTurretAngle(target));
    }
  }

  public void turnToAngle(Rotation2d desiredAngle) {
    //  System.out.println("DESIRED ANGLE: " + desiredAngle);
    Rotation2d currentAngle = getTurretAngle();
    //  System.out.println("CURRENT ANGLE: " + currentAngle);
    Rotation2d error = desiredAngle.minus(currentAngle); // currentAngle.minus(desiredAngle);
    // if (Math.abs(error) > 180)
    error = new Rotation2d(MathUtil.inputModulus(error.getRadians(), -Math.PI, Math.PI));
    // error = MathUtil.inputModulus(error, -180, 180);
    // System.out.println("ERROR: " + error);

    if (Math.abs(error.getDegrees()) > Constants.TURRET_DEADBAND) {
      double output = pidController.calculate(error.getDegrees());
      //  double output = pidController.calculate(angleToTicks(currentAngle),
      // angleToTicks(desiredAngle));
      // System.out.println("CALCULATED OUTPUT: " + output);
      // System.out.println("TURNING TO DESIRED ANGLE");
      setSpeed(output);
    } else {
      // System.out.println("REACHED TARGET");
      setSpeed(0);
    }
  }

  public Rotation2d getTurretAngle() {
    // System.out.println("CURRENT TURRET ANGLE DEGREES " +
    // (-(motor.getPosition().getValueAsDouble()/*  / Constants.KRAKEN_TICKS_PER_REV)*/ * 360 /
    // Constants.TURRET_GEAR_RATIO) % 360));
    double angleInDegrees =
        -((motor.getPosition().getValueAsDouble() /*  / Constants.KRAKEN_TICKS_PER_REV)*/
                * 360
                / Constants.TURRET_GEAR_RATIO)
            % 360);
    return new Rotation2d(Math.toRadians(angleInDegrees));
  }

  public void setSpeed(double speed) {
    // System.out.println("SETTING SPEED TO: " + speed);
    motor.setControl(dutyCycleOut.withOutput(speed));
  }

  public double angleToTicks(double degrees) {
    return ((degrees % 360) / 360)
        * Constants.TURRET_GEAR_RATIO; // * Constants.KRAKEN_TICKS_PER_REV;
  }

  public Rotation2d getTargetTurretAngle(Translation2d target) {

    Pose2d currentRobotPose = robotPose.get();
    double deltaY = target.getY() - currentRobotPose.getY();
    double deltaX = target.getX() - currentRobotPose.getX();
    System.out.println(currentRobotPose);
    // LoggedNetworkNumber currentY = new LoggedNetworkNumber("/current/Y",
    // currentRobotPose.getY());
    // System.out.println("delta x: " + deltaX);
    // System.out.println("delta y" + deltaY);
    Rotation2d angleToTarget = new Rotation2d(Math.atan2(deltaY, deltaX));
    // System.out.println("ATAN2 : "+ angleToTarget.getDegrees());
    Rotation2d turretAngle = angleToTarget.minus(currentRobotPose.getRotation());
    //   System.out.println("TARGET TURRET ANGLE: " + turretAngle);
    return turretAngle;
  }

  public void setUseAngle(boolean useAngle) {
    this.useAngle = useAngle;
  }

  public boolean getUseAngle() {
    return useAngle;
  }
}

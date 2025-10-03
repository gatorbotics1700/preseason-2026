package frc.robot.subsystems;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
// import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.generated.TunerConstants;
import org.littletonrobotics.junction.networktables.LoggedNetworkNumber;

public class ElevatorSubsystem extends SubsystemBase {
  private TalonFX motor;
  private static DutyCycleOut dutyCycleOut = new DutyCycleOut(0);
  // private static PositionVoltage positionVoltage = new PositionVoltage(0);

  private final DigitalInput topLimitSwitch;
  private final DigitalInput bottomLimitSwitch;

  private double setPoint;

  // TODO: test and change these values
  // private static final double kP = 0; //0.0001
  LoggedNetworkNumber kP = new LoggedNetworkNumber("/Tuning/kP", 24 / 3); // 24
  // private static final double kI = 0; //0.0002
  LoggedNetworkNumber kI = new LoggedNetworkNumber("/Tuning/kI", 0.0);
  // private static final double kD = 0; //0.00002
  LoggedNetworkNumber kD = new LoggedNetworkNumber("/Tuning/kD", 0.15 / 3); // 0.15

  // private static double kDt = 0.02;
  LoggedNetworkNumber kDt = new LoggedNetworkNumber("/Tuning/kDt", 0.02); // 0.02

  LoggedNetworkNumber kMaxVelocity = new LoggedNetworkNumber("/Tuning/kMaxVelocity", 17);
  LoggedNetworkNumber kMaxAcceleration = new LoggedNetworkNumber("/Tuning/kMaxAcceleration", 17);

  // private static final double kS = 0;
  LoggedNetworkNumber kS = new LoggedNetworkNumber("/Tuning/kS", 0.0);
  // private static final double kG = 0.8;
  LoggedNetworkNumber kG = new LoggedNetworkNumber("/Tuning/kG", 0.55);
  // private static final double kV = 10.89;
  LoggedNetworkNumber kV = new LoggedNetworkNumber("/Tuning/kV", 0.8 / 3);
  // private static final double kA = 0; //0.01
  LoggedNetworkNumber kA =
      new LoggedNetworkNumber(
          "/Tuning/kA", 0.0); // 0.1 // TODO:actually figure out how to tune this

  LoggedNetworkNumber tunableSetPoint = new LoggedNetworkNumber("/Tuning/tunableSetPoint", 0);
  private TrapezoidProfile.Constraints m_constraints =
      new TrapezoidProfile.Constraints(kMaxVelocity.get(), kMaxAcceleration.get());
  private ProfiledPIDController m_controller =
      new ProfiledPIDController(kP.get(), kI.get(), kD.get(), m_constraints, kDt.get());
  private ElevatorFeedforward feedforward =
      new ElevatorFeedforward(kS.get(), kG.get(), kV.get(), kA.get());

  private double motorPositionOffset = 0;

  public ElevatorSubsystem() {
    motor = new TalonFX(Constants.ELEVATOR_CAN_ID, TunerConstants.kCANBus);
    motor
        .getConfigurator()
        .apply(
            new TalonFXConfiguration()
                .withMotorOutput(
                    new MotorOutputConfigs().withInverted(InvertedValue.Clockwise_Positive)));

    topLimitSwitch = new DigitalInput(Constants.TOP_LIMIT_SWITCH_PORT);
    bottomLimitSwitch = new DigitalInput(Constants.BOTTOM_LIMIT_SWITCH_PORT);
    setPoint = getCurrentInches();
  }

  @Override
  public void periodic() {
    // setSetPoint(tunableSetPoint.get());
    feedforward.setKs(kS.get());
    feedforward.setKg(kG.get());
    feedforward.setKv(kV.get());
    feedforward.setKa(kA.get());
    m_controller.setP(kP.get());
    m_controller.setI(kI.get());
    m_controller.setD(kD.get());

    // System.out.println("kP: " + kP.get());

    SmartDashboard.putBoolean("top limit switch", topLimitSwitch.get());
    SmartDashboard.putBoolean("bottom limit switch", bottomLimitSwitch.get());
    // SmartDashboard.putNumber("elevator current", getMotorStatorCurrent());
    SmartDashboard.putNumber("elevator speed", motor.get());

    double voltage = calculateVoltage();

    // System.out.println("top limit switch: " + topLimitSwitch.get());
    // System.out.println("bottom limit switch: " + bottomLimitSwitch.get());

    // System.out.println("CURRENT HEIGHT (IN): " + getCurrentInches());
    if (bottomLimitSwitch.get() && voltage <= 0) {
      motorPositionOffset = convertTicksToInches(getCurrentTicks());
      voltage = calculateVoltage();
      System.out.println("RESETTING POSITION OFFSET");
    }

    if ((voltage > 0 && topLimitSwitch.get())) {

      setSetPoint(getCurrentInches() - 1.0);

    } else if (voltage < 0 && bottomLimitSwitch.get()) {
      setSetPoint(getCurrentInches());
    } else {
      motor.setVoltage(voltage);
    }
  }

  public void setSetPoint(double desiredInches) {
    // System.out.println("SETTING THE SETPOINT");
    setPoint = desiredInches;
    m_controller.setGoal(setPoint);
    feedforward = new ElevatorFeedforward(kS.get(), kG.get(), kV.get(), kA.get());

    // System.out.println("NEW GOAL: " + m_controller.getSetpoint().getValueAsDouble());

  }

  private double calculateVoltage() {
    //   System.out.println("CALCULATING VOLTAGE");
    double currentInches =
        getCurrentInches(); // in case the motor's positive and negative is reversed due to invert

    double error = setPoint - currentInches;
    //   System.out.println("ERROR (in inches): " + error);

    // confusingly this not only calculates a voltage but also advances the trapezoid profile to the
    // next set point, so it needs to be before the feedforward calculate thing
    double pid =
        m_controller.calculate(
            currentInches); // confirmed this takes the current position not a goal or error

    double ff = feedforward.calculate(m_controller.getSetpoint().velocity);

    double output = ff + pid;
    SmartDashboard.putNumber("total output", output);
    SmartDashboard.putNumber("current inches", currentInches);
    SmartDashboard.putNumber("desiredInches", setPoint);
    SmartDashboard.putNumber(
        "trapezoid profile goal position", m_controller.getSetpoint().position);
    SmartDashboard.putNumber(
        "trapezoid profile goal velocity", m_controller.getSetpoint().velocity);
    SmartDashboard.putNumber("actual real motor velocity", motor.getVelocity().getValueAsDouble());
    SmartDashboard.putNumber("feedforward", ff);
    SmartDashboard.putNumber("pid", pid);
    return output;
  }

  public void setSpeed(double speed) {
    // if(getMotorStatorCurrent() > 1000){ // TODO: set current limit value
    //     System.out.println("ELEVATOR CURRENT PEAKED");
    //     speed = 0;
    // }
    motor.setControl(dutyCycleOut.withOutput(speed));
    if (speed == 0) {
      System.out.println("SET SPEED TO 0, MAINTAINING CURRENT POS");
      setSetPoint(getCurrentTicks());
      calculateVoltage();
    }
  }

  public double
      getCurrentTicks() { // getPosition() is in rotations so rotations * ticks per rev should give
    // position in ticks
    return motor.getPosition().getValueAsDouble() * Constants.KRAKEN_TICKS_PER_REV;
  }

  public double getCurrentInches() {
    return convertTicksToInches(getCurrentTicks()) - motorPositionOffset;
  }

  public double convertTicksToInches(double desiredTicks) {
    return desiredTicks * Constants.ELEVATOR_INCHES_PER_TICK;
  }

  public double convertInchesToTicks(double desiredInches) {
    return desiredInches * Constants.ELEVATOR_TICKS_PER_INCH;
  }

  public double getMotorStatorCurrent() {
    return motor.getStatorCurrent().getValueAsDouble();
  }

  public boolean atTopLimitSwitch() {
    return topLimitSwitch.get();
  }

  public boolean atBottomLimitSwitch() {
    return bottomLimitSwitch.get();
  }

  public void stop() {
    System.out.println("elevator stop");
    stop(getCurrentInches());
  }

  public void setBrakeMode() {
    motor.setNeutralMode(NeutralModeValue.Brake);

    motor
        .getConfigurator()
        .apply(
            new TalonFXConfiguration()
                .withMotorOutput(
                    new MotorOutputConfigs()
                        .withNeutralMode(NeutralModeValue.Brake)
                        .withInverted(InvertedValue.Clockwise_Positive)));
  }

  public void stop(double desiredInches) {
    m_constraints = new TrapezoidProfile.Constraints(kMaxVelocity.get(), kMaxAcceleration.get());
    m_controller =
        new ProfiledPIDController(kP.get(), kI.get(), kD.get(), m_constraints, kDt.get());
    // m_controller.setGoal(desiredInches);
    // feedforward = new ElevatorFeedforward(kS.get(), kG.get(), kV.get(), kA.get());
    //  feedforward.reset();
    if (bottomLimitSwitch.get()) {
      motorPositionOffset = convertTicksToInches(getCurrentTicks());
    }
    setSetPoint(desiredInches);
  }
}

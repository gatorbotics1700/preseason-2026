import com.ctre.phoenix6.hardware.TalonFX;

public class CoralShooterSubsystem{
    public final TalonFX topMotorLeft;
    public final TalonFX topMotorRight;
    private static double voltage;
    private final DigitalInput limitSwitch;
    public CoralShooterSubsystem(){}
    topMotorLeft = new TalonFX(Constants.SHOOTER_MOTOR_TOP_LEFT_CAN_ID, Constants.CANIVORE_BUS_NAME);
    topMotorRight = new TalonFX(Constants.SHOOTER_MOTOR_TOP_RIGHT_CAN_ID, Constants.CANIVORE_BUS_NAME);
    topMotorRight.getConfigurator().apply(new TalonFXConfiguration).withMotorOutput(new MotorOutputCOnfigs().withInverted(INvertedValue.Clockwise_Positive)));
    limitSwitch = new DigitalInput(9);
    voltage = Constants.CORAL_L4_SHOOTING_VOLTAGE;    
} 
public void setMotorVoltage(double voltage) {
    topMotorLeft.setVoltage(voltage);
    topMotorRight.setVoltage(voltage);
}
public double getTopMotorLeftStatorCurrent(){
    return topMotorLeft.getStatorCurrent().getValueAsDouble();
}
public double getTopMotorLeftSpeed(){
    return topMotorLeft.getVelocity().getValueAsDouble();
}

public boolean getLimitSwitch(){
    return limitSwitch.get();
}
}
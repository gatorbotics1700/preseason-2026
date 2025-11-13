package frc.robot.subsystems;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANrangeConfiguration;
import com.ctre.phoenix6.hardware.CANrange;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.generated.TunerConstants;
import org.littletonrobotics.junction.Logger;

public class DistanceSensor extends SubsystemBase {
  private CANrange CANrange1;

  // Constants used in CANrange construction

  public DistanceSensor() {
    // Construct the CANrange
    CANrange1 = new CANrange(Constants.CAN_RANGE_ID, TunerConstants.kCANBus);

    // Configure the CANrange for basic use
    CANrangeConfiguration configs = new CANrangeConfiguration();

    // Write these configs to the CANrange
    CANrange1.getConfigurator().apply(configs);
  }

  public double getDistance(){
    return CANrange1.getDistance().refresh().getValueAsDouble();
  }

  @Override
  public void periodic() {
    StatusSignal<Distance> distance = CANrange1.getDistance();

    // Refresh and print these values
    SmartDashboard.putNumber("distance sensor", distance.refresh().getValueAsDouble());
    System.out.println("Distance is " + distance.refresh().getValueAsDouble());
    Logger.recordOutput("vision/refreshDistance", distance.refresh().getValueAsDouble());
    Logger.recordOutput("vision/distance", distance.getValueAsDouble());

    // if (distance.refresh().getValueAsDouble() < 0.9) {
    //   stop();
    // }
  }

}

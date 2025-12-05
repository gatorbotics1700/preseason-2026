// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class CoralShooterSubsystem extends SubsystemBase {
  // You need to have two TalonFX motors (left and right) and a DigitalInput limit switch
  /* TODO: declare your motors and limit switch here */

  /** Creates a new CoralShooterSubsystem. */
  public CoralShooterSubsystem() {
    // To instantiate your motor, you want to create a new TalonFX object
    // Make sure to include both the CAN ID and the CANBus name from Constants.java
    /* TODO: instantiate your motors here */

    // This part of the code is making it so if we give the motors the same voltage, they'll go in
    // opposite directions
    // Since the wheels have to spin the same direction but the motors are mounted on opposite sides
    /* TODO: put your right motor name here and uncomment the next few lines */
    // .getConfigurator()
    // .apply(
    //     new TalonFXConfiguration()
    //         .withMotorOutput(
    //             new MotorOutputConfigs().withInverted(InvertedValue.Clockwise_Positive)));

    // You must also instantiate your limit switch by creating a new DigitalInput object
    // Make sure to use the limit switch constant in Constants.java
    /* TODO: instantiate your limit switch here */
  }

  /* GETTER AND SETTER METHODS */

  // We need methods to set the motor voltage and to get the value of the limit switch

  /* TODO: write a method to set motor voltage
   * You can use the TalonFX method setVoltage() on each motor to give the motor the voltages
   */

  /* TODO: write a method to get the limit switch value
   * You can use the DigitalInput method get()
   */
}

package frc.robot.commands;

import frc.robot.subsystems.PassThroughSubsystem;

public class PassThroughCommand {
    private PassThroughSubsystem  passThroughSubsystem;
    private final double voltage;
    
}

// @Override
// public void execute() {
//     passThroughSubsystem.setMotorVoltage(voltage);

//     if(voltage > 0) { //not sure which way the motor is going - change these inequalities based on build
//         passThroughSubsystem.setMotorVoltage(voltage);
//         System.out.println("INTAKING");
//     }
// }

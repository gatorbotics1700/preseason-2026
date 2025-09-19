package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.PassThroughSubsystem;

public class PassThroughCommand extends Command {
    private PassThroughSubsystem passThroughSubsystem;
    private final double voltage;
    private boolean isIntaking;

    
    public PassThroughCommand(PassThroughSubsystem passThroughSubsystem, double voltage, boolean isIntaking){
        this.passThroughSubsystem = passThroughSubsystem;
        this.voltage = voltage;
        this.isIntaking = isIntaking;
        addRequirements(passThroughSubsystem);
    }

@Override
public void execute() {
    passThroughSubsystem.setMotorVoltage(voltage);
    if (isIntaking) {
        System.out.println("INTAKING");
    } else {
        System.out.println ("OUTTAKING");
    }
    System.out.println("VOLTAGE: " + voltage);    
}

@Override
public boolean isFinished() {
    if(isIntaking){ // if intaking TODO: check sign of intaking voltage, if less/greater than 0
        if(passThroughSubsystem.isBeambreakClear()==false){ //if coral is in beambreak
            passThroughSubsystem.setMotorVoltage(0);
            return true;
        }
   } else { //if scoring TODO: check sign of each level's voltage
        if(passThroughSubsystem.isBeambreakClear()){ //if coral is out of beambreak
            passThroughSubsystem.setMotorVoltage(0);
            return true;
        }
   }
   return false;
}

}
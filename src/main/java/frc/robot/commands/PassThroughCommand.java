package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.PassThroughSubsystem;

public class PassThroughCommand extends Command {
    private PassThroughSubsystem passThroughSubsystem;
    private final double voltage;
    private boolean isIntaking;

    private final boolean state;

    
    public PassThroughCommand(PassThroughSubsystem passThroughSubsystem, double voltage, boolean state){
        this.passThroughSubsystem = passThroughSubsystem;
        this.voltage = voltage;
        this.state = state;
        addRequirements(passThroughSubsystem);
    }

@Override
public void execute() {
    passThroughSubsystem.setMotorVoltage(voltage);
    System.out.println("VOLTAGE: " + voltage);    
}

@Override
public boolean isFinished() {
    if(state == passThroughSubsystem.INTAKING){ // if intaking TODO: check sign of intaking voltage, if less/greater than 0
        if(passThroughSubsystem.isBeamBroken()==false){ //if coral is in beambreak
            passThroughSubsystem.setMotorVoltage(0);
            return true;
        }
   } else if (state == passThroughSubsystem.OUTTAKING){ //if scoring TODO: check sign of each level's voltage
        if(passThroughSubsystem.isBeamBroken()){ //if coral is out of beambreak
            passThroughSubsystem.setMotorVoltage(0);
            return true;
        }
   }
   return false;
}

}
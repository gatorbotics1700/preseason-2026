package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Subsystem;

import java.lang.module.ModuleDescriptor.Requires;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.subsystems.DistanceSensor;

public class DistanceSensorCommand extends Command {
    private DistanceSensor distanceSensor;

    public  DistanceSensorCommand(DistanceSensor distanceSensor1){
        distanceSensor = distanceSensor1;
        //addRequirements(distanceSensor);
    }

}

@Override
public boolean isFinished(){
    if((distanceSensor).getDistance() < 0.9){
    
    }
}




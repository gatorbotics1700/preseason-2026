package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.subsystems.DistanceSensor;

public class DistanceSensorCommand {
    private Subsystem distanceSensor = new DistanceSensor()
    .addRequirements(distanceSensor);
}

public boolean isFinished(){
    if(((DistanceSensor) distanceSensor).getDistance() < 0.9){

    }
}


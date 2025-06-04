package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorCommand extends Command {
    
    private ElevatorSubsystem elevatorSubsystem;
    private double speed;

    public ElevatorCommand(double speed, ElevatorSubsystem elevatorSubsystem) {
        this.speed = speed;
        this.elevatorSubsystem = elevatorSubsystem;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void initialize(){
        elevatorSubsystem.setSpeed(0);
    }
    
    @Override
    public void execute() {
        elevatorSubsystem.setSpeed(speed);
        System.out.println("SETTING SPEED TO " + speed);
    }

    @Override
    public boolean isFinished(){
        if(speed == 0){
            elevatorSubsystem.setSpeed(0);
            return true;
        }
        return false;
    }
}

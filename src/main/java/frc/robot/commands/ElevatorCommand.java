package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorCommand extends Command {

    private ElevatorSubsystem elevatorSubsystem;
    private static double height = 0; // in inches
    private double addedHeight;
    private double speed;
    double desiredTicks;
    private double DEADBAND = 1 * Constants.ELEVATOR_TICKS_PER_INCH; // 1 inch in ticks; TODO: change this value
    
    public ElevatorCommand(ElevatorSubsystem elevatorSubsystem, double addedHeight, double speed){
        this.elevatorSubsystem = elevatorSubsystem;
        this.addedHeight = addedHeight;
        this.speed = speed; // only used with joystick, otherwise set to 0
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void initialize(){
        height+=addedHeight;
        System.out.println("height: "+height);
        desiredTicks = elevatorSubsystem.determineInchesToTicks(height);
    }
    
    @Override 
    public void execute(){
        if(speed==0){
            elevatorSubsystem.setSpeed(speed);}
        else if(speed==1){
            elevatorSubsystem.setPosition(desiredTicks);
        }else{
            System.out.println("weird double casting things? idk");
        }
        //System.out.println("top limit switch: " + elevatorSubsystem.atTopLimitSwitch());
        //System.out.println("bottom limit switch: " + elevatorSubsystem.atBottomLimitSwitch());
    }

    @Override
    public boolean isFinished() {
        double currentTicks = elevatorSubsystem.getCurrentTicks(); 
        System.out.println("CURRENT HEIGHT: " + (currentTicks / Constants.ELEVATOR_TICKS_PER_INCH) + " inches");
        
        if(elevatorSubsystem.getMotorStatorCurrent()>1000){ // TODO: set current limit value
            System.out.println("ELEVATOR CURRENT PEAKED");
            elevatorSubsystem.setSpeed(0);
            return true;
        }

        if(elevatorSubsystem.atTopLimitSwitch() && desiredTicks > Math.abs(currentTicks)){
            System.out.println("TOP LIMIT SWITCH TRIGGERED - STOPPING");
            elevatorSubsystem.setSpeed(0);
            return true;
        }

        if(elevatorSubsystem.atBottomLimitSwitch() && desiredTicks < Math.abs(currentTicks)){ 
            System.out.println("BOTTOM LIMIT SWITCH TRIGGERED - STOPPING");
            elevatorSubsystem.setSpeed(0);
            return true;
        }

        double error = desiredTicks - currentTicks; 
        if(Math.abs(error) < DEADBAND){
            System.out.println("REACHED TARGET");
            elevatorSubsystem.setSpeed(0);
            return true;
        }
        if(speed==0){
            System.out.println("A PRESSED STOPPING ELEVATOR");
            elevatorSubsystem.setSpeed(0);
        }

        return false;
    }
}
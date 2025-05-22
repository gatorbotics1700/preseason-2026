package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.ElevatorSubsystem;

public class ElevatorCommand extends Command {

    private ElevatorSubsystem elevatorSubsystem;
    private double speed;
    // private double desiredHeight; // in inches!
    private double desiredChange;
    private double desiredTicks;
    private boolean isUsingPos;

    private double DEADBAND = 500; // 1 inch in ticks; TODO: change this value
    
    public ElevatorCommand(ElevatorSubsystem elevatorSubsystem, boolean isUsingPos, double desiredChange/*double desiredHeight*/, double speed){
        this.elevatorSubsystem = elevatorSubsystem;
        this.speed = speed; // only used with joystick, otherwise set to 0
        // this.desiredHeight = desiredHeight;
        this.desiredChange = desiredChange;
        this.isUsingPos = isUsingPos;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void initialize(){
        //desiredTicks = elevatorSubsystem.determineInchesToTicks(desiredHeight);
        elevatorSubsystem.setIsUsingPos(isUsingPos);
        desiredTicks = elevatorSubsystem.getCurrentTicks() + desiredChange;
    }
    
    @Override 
    public void execute(){
        if(!isUsingPos){
            elevatorSubsystem.setSpeed(speed);
        } else {
            elevatorSubsystem.setSetPoint(desiredTicks);
        }
        System.out.println("MOTOR CURRENT: " + elevatorSubsystem.getMotorStatorCurrent());
    }

    @Override
    public boolean isFinished() {
        double currentTicks = elevatorSubsystem.getCurrentTicks(); 
        // System.out.println("CURRENT HEIGHT: " + (currentTicks / Constants.ELEVATOR_TICKS_PER_INCH) + " inches");
        System.out.println("Top limit switch:" + elevatorSubsystem.atTopLimitSwitch());
        System.out.println("Bottom limit switch:" + elevatorSubsystem.atBottomLimitSwitch());
        
        if(!isUsingPos && speed==0){
            System.out.println("A PRESSED STOPPING ELEVATOR");
            elevatorSubsystem.setSpeed(0);
            elevatorSubsystem.goToPosition(elevatorSubsystem.getCurrentTicks());
        }
        
        // if(elevatorSubsystem.getMotorStatorCurrent() > 1000){ // TODO: set current limit value
        //     System.out.println("ELEVATOR CURRENT PEAKED");
        //     elevatorSubsystem.setSpeed(0);
        //     return true;
        // }

        // if(elevatorSubsystem.atBottomLimitSwitch() || elevatorSubsystem.atTopLimitSwitch()){
        //     System.out.println("LIMIT SWITCH TRIGGERED - STOPPING");
        //     elevatorSubsystem.setSpeed(0);
        //     return true;
        // }

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

        return false;
    }
}
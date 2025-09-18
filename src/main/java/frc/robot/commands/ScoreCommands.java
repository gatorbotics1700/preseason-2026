package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants;
import frc.robot.subsystems.PassThroughSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.commands.ElevatorCommand;

public class ScoreCommands {
    public static Command Level(int level, ElevatorSubsystem m_elevatorSub, PassThroughSubsystem m_passThroughSubsystem){
        System.out.println("IN SCORE COMMAND!!!");
        if (level == 1){
            return Trough(m_elevatorSub, m_passThroughSubsystem);
        } else if (level == 2){
            return LevelTwo(m_elevatorSub, m_passThroughSubsystem);
        } else if (level == 3){
            return LevelThree(m_elevatorSub, m_passThroughSubsystem);
        }

        return Commands.waitSeconds(2); //if all else fails, return a wait command
    }

    public static Command Trough(ElevatorSubsystem elevatorSubsystem, PassThroughSubsystem passThroughSubsystem){
        System.out.println("TROUGH!");
        return new ElevatorCommand(elevatorSubsystem, Constants.ELEVATOR_LEVEL_ONE)
        .andThen(new PassThroughCommand(passThroughSubsystem, Constants.TROUGH_VOLTAGE));
    }
    
    public static Command LevelTwo(ElevatorSubsystem elevatorSubsystem, PassThroughSubsystem passThroughSubsystem){
        System.out.println("LEVEL 2!");
        return new ElevatorCommand(elevatorSubsystem, Constants.ELEVATOR_LEVEL_TWO)
        .andThen(new PassThroughCommand(passThroughSubsystem, Constants.L2_VOLTAGE));
    }
    

    public static Command LevelThree(ElevatorSubsystem elevatorSubsystem, PassThroughSubsystem passThroughSubsystem){
        System.out.println("LEVEL 3!");
        return new ElevatorCommand(elevatorSubsystem, Constants.ELEVATOR_LEVEL_THREE)
        .andThen(new PassThroughCommand(passThroughSubsystem, Constants.L3_VOLTAGE));
    }

    public static Command Intake(PassThroughSubsystem passThroughSubsystem){
            System.out.println("INTAKING!");
            PassThroughCommand passThroughCommand = new PassThroughCommand(passThroughSubsystem, Constants.INTAKING_VOLTAGE);
            return passThroughCommand;
    }
}

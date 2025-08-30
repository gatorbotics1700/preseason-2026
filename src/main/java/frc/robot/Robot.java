package frc.robot;

import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger; //PLEASE NOTE THIS IS NOT THE FIRST LOGGER OPTION VS CODE SUGGESTS IMPORTING
import org.littletonrobotics.junction.networktables.LoggedNetworkNumber;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import frc.robot.subsystems.BeamBreakSubsystem;

public class Robot extends LoggedRobot {
    // private Command m_autonomousCommand;
    // private RobotContainer container;
    private BeamBreakSubsystem beamBreakSubsystem;

    public Robot() {
        Logger.start();
    }

    @Override
    public void robotInit() {
        // container = new RobotContainer();
        // SmartDashboard.putData(CommandScheduler.getInstance());
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        //  m_autonomousCommand = container.getAutonomousCommand();

        // if (m_autonomousCommand != null) {
        //     m_autonomousCommand.schedule();
        // }
    }   

    @Override
    public void teleopInit() {
        // container.setDefaultTeleopCommand();

        // // This makes sure that the autonomous stops running when teleop starts
        // if (m_autonomousCommand != null) {
        //     m_autonomousCommand.cancel();
        // }
    }

    @Override
    public void teleopPeriodic() {
        // Leave empty - default command will handle teleop
        beamBreakSubsystem.periodic();
    }

    @Override
    public void testInit(){

    }

    @Override
    public void testPeriodic(){

    }
}

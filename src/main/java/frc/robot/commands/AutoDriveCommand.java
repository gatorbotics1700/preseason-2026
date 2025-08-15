
package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DrivetrainSubsystemInterface;

public class AutoDriveCommand extends Command {
    private final DrivetrainSubsystemInterface drivetrain;
    
    public AutoDriveCommand(DrivetrainSubsystemInterface drivetrain) {
        this.drivetrain = drivetrain;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        System.out.println("TestDriveCommand started");
    }

    @Override
    public void execute() {
        // Try to drive forward at 1 m/s
        ChassisSpeeds speeds = new ChassisSpeeds(0.2, 0.0, 0.0);
        System.out.println("Attempting to drive forward...");
        drivetrain.driveRobotRelative(speeds);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.driveRobotRelative(new ChassisSpeeds());
        System.out.println("TestDriveCommand ended");
    }

    @Override
    public boolean isFinished() {
        return false;
    }
} 


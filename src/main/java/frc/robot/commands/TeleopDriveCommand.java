package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DrivetrainSubsystemInterface;

import java.util.function.DoubleSupplier;

public class TeleopDriveCommand extends Command {
    private final DrivetrainSubsystemInterface drivetrain;
    private final DoubleSupplier translationXSupplier;
    private final DoubleSupplier translationYSupplier;
    private final DoubleSupplier rotationSupplier;

    public TeleopDriveCommand(DrivetrainSubsystemInterface drivetrain,
            DoubleSupplier translationXSupplier,
            DoubleSupplier translationYSupplier,
            DoubleSupplier rotationSupplier) {
        this.drivetrain = drivetrain;
        this.translationXSupplier = translationXSupplier;
        this.translationYSupplier = translationYSupplier;
        this.rotationSupplier = rotationSupplier;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        drivetrain.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    }

    @Override
    public void execute() {
        double translationXPercent = translationXSupplier.getAsDouble(); // x speed
        double translationYPercent = translationYSupplier.getAsDouble(); // y speed
        double rotationPercent = rotationSupplier.getAsDouble(); // rotation speed

        drivetrain.drive(
                ChassisSpeeds.fromFieldRelativeSpeeds(
                        translationXPercent * DrivetrainSubsystemInterface.MAX_VELOCITY_METERS_PER_SECOND,
                        translationYPercent * DrivetrainSubsystemInterface.MAX_VELOCITY_METERS_PER_SECOND,
                        rotationPercent * DrivetrainSubsystemInterface.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND,
                        drivetrain.getRotation()));
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
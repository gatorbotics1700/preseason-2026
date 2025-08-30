package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.TurretSubsystem;
import edu.wpi.first.math.geometry.Rotation2d;

public class TurretCommand extends Command {

    private TurretSubsystem turretSubsystem;
    private Rotation2d desiredAngle;
    
    public TurretCommand(TurretSubsystem turretSubsystem, Rotation2d desiredAngle){
        this.turretSubsystem = turretSubsystem;
        this.desiredAngle = desiredAngle;
        addRequirements(turretSubsystem);
    }

    @Override
    public void initialize() {
        turretSubsystem.setUseAngle(true);
    }

    @Override
    public void execute() {
        turretSubsystem.turnToAngle(desiredAngle);
    }
    


    @Override
    public boolean isFinished() {
        Rotation2d currentAngle = turretSubsystem.getTurretAngle();

        Rotation2d error = desiredAngle.minus(currentAngle);
        if (Math.abs(error.getDegrees()) < Constants.TURRET_DEADBAND) {
            System.out.println("REACHED TARGET");
            turretSubsystem.setSpeed(0);
            turretSubsystem.setUseAngle(false);
            return true;
        }
        return false;
    }
}

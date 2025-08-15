package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.TurretSubsystem;

public class TurretCommand extends Command {

    private TurretSubsystem turretSubsystem;
    private double desiredAngle;
    
    public TurretCommand(TurretSubsystem turretSubsystem, double desiredAngle){
        this.turretSubsystem = turretSubsystem;
        this.desiredAngle = desiredAngle;
        addRequirements(turretSubsystem);
    }

    @Override
    public void execute() {
        turretSubsystem.turnToAngle(desiredAngle);
    }

    @Override
    public boolean isFinished() {
        double currentAngle = turretSubsystem.getTurretAngle();

        double error = desiredAngle - currentAngle;
        if (Math.abs(error) < Constants.TURRET_DEADBAND) {
            System.out.println("REACHED TARGET");
            turretSubsystem.setSpeed(0);
            return true;
        }
        return false;
    }
}

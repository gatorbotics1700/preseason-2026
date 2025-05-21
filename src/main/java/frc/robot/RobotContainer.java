package frc.robot;

// import frc.robot.commands.AutoDriveCommand;
// import frc.robot.commands.TeleopDriveCommand;
import frc.robot.commands.ElevatorCommand;
// import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class RobotContainer {
    // private final DrivetrainSubsystem drivetrainSubsystem = new DrivetrainSubsystem();
    private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    
    private final XboxController controller = new XboxController(0);
    private final XboxController controller_two = new XboxController(1);
    
    // private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
        // Print initial joystick values
        System.out.println("RobotContainer initializing");

        // Zero gyroscope button binding
        // new Trigger(controller::getBackButtonPressed)
        //         .onTrue(new InstantCommand(drivetrainSubsystem::zeroGyroscope));

        // new Trigger(controller::getRightBumperButtonPressed)
        //         .onTrue(new InstantCommand(drivetrainSubsystem::setSlowDrive));

        // new Trigger(controller::getLeftBumperButtonPressed)
        //     .onTrue(new InstantCommand(drivetrainSubsystem::toggleRobotRelativeDrive));
        
        // autoChooser = AutoBuilder.buildAutoChooser();

        // SmartDashboard.putData("Auto Chooser", autoChooser);

        new Trigger(controller_two::getAButtonPressed)
            .onTrue(new ElevatorCommand(elevatorSubsystem, false, 0, 0));
        
        new Trigger(controller_two::getBButtonPressed)
            .onTrue(new ElevatorCommand(elevatorSubsystem, true, 10000, 0));
        
        new Trigger(controller_two::getXButtonPressed)
            .onTrue(new ElevatorCommand(elevatorSubsystem, true, 3000, 0));

        new Trigger(controller_two::getYButtonPressed)
            .onTrue(new ElevatorCommand(elevatorSubsystem, true, -3500, 0));

        
    }

    // public Command getAutonomousCommand() {
    //     try {
    //         Command auto = autoChooser.getSelected();
    //         System.out.println("Auto loaded successfully: " + autoChooser.getSelected().getName());
    //         return auto;
    //     } catch (Exception e) {
    //         System.err.println("Failed to load auto path: " + e.getMessage());
    //         e.printStackTrace();
    //         return new AutoDriveCommand(drivetrainSubsystem);
    //     }
    // }

    // public void setDefaultTeleopCommand(){
    //     System.out.println("SETTING DEFAULT TELEOP COMMAND");
    //     var alliance = DriverStation.getAlliance();
    //     if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red){
    //         drivetrainSubsystem.setDefaultCommand(
    //             new TeleopDriveCommand(
    //                 drivetrainSubsystem,
    //                 () -> modifyAxis(0.9*controller.getRightY()),    // Changed to raw values
    //                 () -> modifyAxis(0.9*controller.getRightX()),     // Changed to raw values
    //                 () -> -modifyAxis(0.8*controller.getLeftX())    // Changed to raw values
    //             )
    //         );
    //     }else if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Blue){
    //         drivetrainSubsystem.setDefaultCommand(
    //             new TeleopDriveCommand(
    //                 drivetrainSubsystem,
    //                 () -> -modifyAxis(0.9*controller.getRightY()),    // Changed to raw values
    //                 () -> -modifyAxis(0.9*controller.getRightX()),     // Changed to raw values
    //                 () -> -modifyAxis(0.8*controller.getLeftX())    // Changed to raw values
    //             )
    //         );
    //     }
    // }

    // public DrivetrainSubsystem getDrivetrainSubsystem(){
    //     return drivetrainSubsystem;
    // }


    // private double deadband(double value, double deadband) {
    //     if (Math.abs(value) > deadband) {
    //         if (value > 0.0) {
    //             return (value - deadband) / (1.0 - deadband);
    //         } else {
    //             return (value + deadband) / (1.0 - deadband);
    //         }
    //     } else {
    //         return 0.0;
    //     }
    // }

    // private double modifyAxis(double value) {
    //     value = deadband(value, 0.05);

    //     // Square the axis
    //     value = Math.copySign(value * value, value);

    //     if(drivetrainSubsystem.getSlowDrive()){
    //         return (0.5 * value);

    //     }

    //     return value;
    // }
}

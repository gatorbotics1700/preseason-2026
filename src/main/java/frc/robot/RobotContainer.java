package frc.robot;

import frc.robot.commands.AutoDriveCommand;
import frc.robot.commands.DriveTwoMeters;
import frc.robot.commands.TeleopDriveCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.DrivetrainSubsystemInterface;
import frc.robot.subsystems.drive.DrivetrainSim;
import frc.robot.subsystems.drive.GyroIOSim;
import frc.robot.subsystems.drive.ModuleIOTalonFXSim;

import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.COTS;
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class RobotContainer {
    private final DrivetrainSubsystemInterface drivetrain;
    
    private final XboxController controller = new XboxController(0);
    
    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
        // Print initial joystick values
        System.out.println("RobotContainer initializing");

        if (Robot.isReal()) {
            this.drivetrain = new DrivetrainSubsystem(); // Real implementation

        new Trigger(controller::getXButtonPressed)
            .onTrue(new DriveTwoMeters((DrivetrainSubsystem)drivetrain));
        }
        else {
            //TODO: we probably shouldn't put this code here
            // Create and configure a drivetrain simulation configuration

            //TODO: this whole config business?? very fishy
            final DriveTrainSimulationConfig driveTrainSimulationConfig = DriveTrainSimulationConfig.Default()
                    // Specify gyro type (for realistic gyro drifting and error simulation)
                    .withGyro(COTS.ofPigeon2())
                    // Specify swerve module (for realistic swerve dynamics)
                    .withSwerveModule(COTS.ofMark4(
                            DCMotor.getKrakenX60(1), // Drive motor is a Kraken X60
                            DCMotor.getFalcon500(1), // Steer motor is a Falcon 500
                            COTS.WHEELS.COLSONS.cof, // Use the COF for Colson Wheels
                            3)) // L3 Gear ratio
                    // Configures the track length and track width (spacing between swerve modules)
                    //TODO: check the whole "units.inches.of" business in case it's converting it wrong
                    .withTrackLengthTrackWidth(Units.Inches.of(24), Units.Inches.of(24))
                    // Configures the bumper size (dimensions of the robot bumper)
                    .withBumperSize(Units.Inches.of(30), Units.Inches.of(30));
            /* Create a swerve drive simulation */
            SwerveDriveSimulation swerveDriveSimulation = new SwerveDriveSimulation(
                    // Specify Configuration
                    driveTrainSimulationConfig,
                    // Specify starting pose
                    new Pose2d(3, 3, new Rotation2d())
            );

            // Register the drivetrain simulation to the default simulation world
            SimulatedArena.getInstance().addDriveTrainSimulation(swerveDriveSimulation);

            this.drivetrain = new DrivetrainSim(
                new GyroIOSim(swerveDriveSimulation.getGyroSimulation()),
                new ModuleIOTalonFXSim(
                    TunerConstants.FrontLeft, swerveDriveSimulation.getModules()[0]),
                new ModuleIOTalonFXSim(
                    TunerConstants.FrontRight, swerveDriveSimulation.getModules()[1]),
                new ModuleIOTalonFXSim(
                    TunerConstants.BackLeft, swerveDriveSimulation.getModules()[2]),
                new ModuleIOTalonFXSim(
                    TunerConstants.BackRight, swerveDriveSimulation.getModules()[3]),
                    swerveDriveSimulation::setSimulationWorldPose); // Simulation implementation
        }

        // Zero gyroscope button binding
        new Trigger(controller::getBackButtonPressed)
                .onTrue(new InstantCommand(drivetrain::zeroGyroscope));

        new Trigger(controller::getRightBumperButtonPressed)
                .onTrue(new InstantCommand(drivetrain::setSlowDrive));

        new Trigger(controller::getLeftBumperButtonPressed)
            .onTrue(new InstantCommand(drivetrain::toggleRobotRelativeDrive));
        
        autoChooser = AutoBuilder.buildAutoChooser();

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    public Command getAutonomousCommand() {
        try {
            Command auto = autoChooser.getSelected();
            System.out.println("Auto loaded successfully: " + autoChooser.getSelected().getName());
            return auto;
        } catch (Exception e) {
            System.err.println("Failed to load auto path: " + e.getMessage());
            e.printStackTrace();
            return new AutoDriveCommand(drivetrain);
        }
    }

    public void setDefaultTeleopCommand(){
        System.out.println("SETTING DEFAULT TELEOP COMMAND");
        var alliance = DriverStation.getAlliance();
        if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red){
            drivetrain.setDefaultCommand(
                new TeleopDriveCommand(
                    drivetrain,
                    () -> modifyAxis(0.9*controller.getRightY()),    // Changed to raw values
                    () -> modifyAxis(0.9*controller.getRightX()),     // Changed to raw values
                    () -> -modifyAxis(0.8*controller.getLeftX())    // Changed to raw values
                )
            );
        }else if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Blue){
            drivetrain.setDefaultCommand(
                new TeleopDriveCommand(
                    drivetrain,
                    () -> -modifyAxis(0.9*controller.getRightY()),    // Changed to raw values
                    () -> -modifyAxis(0.9*controller.getRightX()),     // Changed to raw values
                    () -> -modifyAxis(0.8*controller.getLeftX())    // Changed to raw values
                )
            );
        }
    }

    public DrivetrainSubsystemInterface getDrivetrain(){
        return drivetrain;
    }


    private double deadband(double value, double deadband) {
        if (Math.abs(value) > deadband) {
            if (value > 0.0) {
                return (value - deadband) / (1.0 - deadband);
            } else {
                return (value + deadband) / (1.0 - deadband);
            }
        } else {
            return 0.0;
        }
    }

    private double modifyAxis(double value) {
        value = deadband(value, 0.05);

        // Square the axis
        value = Math.copySign(value * value, value);

        if(drivetrain.getSlowDrive()){
            return (0.5 * value);

        }

        return value;
    }
}
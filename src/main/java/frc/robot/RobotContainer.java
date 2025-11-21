// Copyright 2021-2025 FRC 6328
// http://github.com/Mechanical-Advantage
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// version 3 as published by the Free Software Foundation or
// available in the root directory of this project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

package frc.robot;

// import frc.robot.commands.AutoDriveCommand;
// import frc.robot.commands.TeleopDriveCommand;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.DriveCommands;
import frc.robot.commands.LineupCommand;
import frc.robot.commands.LineupCommand.ReefSide;
import frc.robot.commands.LineupCommand.YOffset;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.GyroIO;
import frc.robot.subsystems.drive.GyroIOPigeon2;
import frc.robot.subsystems.drive.ModuleIO;
import frc.robot.subsystems.drive.ModuleIOSim;
import frc.robot.subsystems.drive.ModuleIOTalonFX;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;
import frc.robot.subsystems.vision.VisionIOLimelight;
import frc.robot.subsystems.vision.VisionIOPhotonVisionSim;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Subsystems
  private final Drive drive;
  private final Vision vision;

  // Controllers
  private final CommandXboxController controller = new CommandXboxController(0);
  private final CommandXboxController controller_two = new CommandXboxController(3);

  private final GenericHID buttonBoard1A = new GenericHID(1);
  private final GenericHID buttonBoard1B = new GenericHID(2);

  // Dashboard inputs
  private final LoggedDashboardChooser<Command> autoChooser;

  // Button Bindings
  private final Trigger Q1LeftLineup = new Trigger(() -> buttonBoard1A.getRawButtonPressed(1));
  private final Trigger Q1RightLineup = new Trigger(() -> buttonBoard1A.getRawButtonPressed(2));

  private final Trigger Q2LeftLineup = new Trigger(() -> buttonBoard1B.getRawButtonPressed(2));
  private final Trigger Q2RightLineup = new Trigger(() -> buttonBoard1B.getRawButtonPressed(1));

  private final Trigger Q3LeftLineup = new Trigger(() -> buttonBoard1B.getRawButtonPressed(4));
  private final Trigger Q3RightLineup = new Trigger(() -> buttonBoard1B.getRawButtonPressed(3));

  private final Trigger Q4LeftLineup = new Trigger(() -> buttonBoard1B.getRawButtonPressed(6));
  private final Trigger Q4RightLineup = new Trigger(() -> buttonBoard1B.getRawButtonPressed(5));

  private final Trigger Q5LeftLineup = new Trigger(() -> buttonBoard1A.getRawButtonPressed(5));
  private final Trigger Q5RightLineup = new Trigger(() -> buttonBoard1A.getRawButtonPressed(6));

  private final Trigger Q6LeftLineup = new Trigger(() -> buttonBoard1A.getRawButtonPressed(3));
  private final Trigger Q6RightLineup = new Trigger(() -> buttonBoard1A.getRawButtonPressed(4));

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Named Commands

    NamedCommands.registerCommand(
        "Q1 Left Lineup",
        new InstantCommand(
            () -> {
              CommandScheduler.getInstance()
                  .schedule(LineupCommand.Lineup(ReefSide.Q1, YOffset.Left));
            }));
    NamedCommands.registerCommand(
        "Q1 Right Lineup",
        new InstantCommand(
            () -> {
              CommandScheduler.getInstance()
                  .schedule(LineupCommand.Lineup(ReefSide.Q1, YOffset.Right));
            }));

    // Set up robot depending on mode
    switch (Constants.currentMode) {
      case REAL:
        // Real robot, instantiate hardware IO implementations
        drive =
            new Drive(
                new GyroIOPigeon2(),
                new ModuleIOTalonFX(TunerConstants.FrontLeft),
                new ModuleIOTalonFX(TunerConstants.FrontRight),
                new ModuleIOTalonFX(TunerConstants.BackLeft),
                new ModuleIOTalonFX(TunerConstants.BackRight),
                (pose) -> {});
        vision =
            new Vision(
                drive,
                new VisionIOLimelight(
                    VisionConstants.CAMERA_0_NAME,
                    drive::getRotation,
                    VisionConstants.ROBOT_TO_CAMERA_0));
        break;

      case SIM:
        // Sim robot, instantiate physics sim IO implementations
        drive =
            new Drive(
                new GyroIO() {},
                new ModuleIOSim(TunerConstants.FrontLeft),
                new ModuleIOSim(TunerConstants.FrontRight),
                new ModuleIOSim(TunerConstants.BackLeft),
                new ModuleIOSim(TunerConstants.BackRight),
                (pose) -> {});
        vision =
            new Vision(
                drive,
                new VisionIOPhotonVisionSim(
                    VisionConstants.CAMERA_0_NAME,
                    VisionConstants.ROBOT_TO_CAMERA_0,
                    drive::getPose));
        break;

      default: // TODO: should the default be real as a safety for matches? to be discussed
        // Replayed robot, disable IO implementations
        drive =
            new Drive(
                new GyroIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                (pose) -> {});
        vision = new Vision(drive);
        break;
    }

    // Set up auto routines
    autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());

    // Set up SysId routines
    // autoChooser.addOption(
    //     "Drive Wheel Radius Characterization", DriveCommands.wheelRadiusCharacterization(drive));
    // autoChooser.addOption(
    //     "Drive Simple FF Characterization", DriveCommands.feedforwardCharacterization(drive));
    // autoChooser.addOption(
    //     "Drive SysId (Quasistatic Forward)",
    //     drive.sysIdQuasistatic(SysIdRoutine.Direction.kForward));
    // autoChooser.addOption(
    //     "Drive SysId (Quasistatic Reverse)",
    //     drive.sysIdQuasistatic(SysIdRoutine.Direction.kReverse));
    // autoChooser.addOption(
    //     "Drive SysId (Dynamic Forward)", drive.sysIdDynamic(SysIdRoutine.Direction.kForward));
    // autoChooser.addOption(
    //     "Drive SysId (Dynamic Reverse)", drive.sysIdDynamic(SysIdRoutine.Direction.kReverse));

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  public void configureButtonBindings() {
    // Default command, normal field-relative drive
    Trigger driverControl =
        new Trigger(
            () ->
                Math.abs(controller.getLeftY()) > 0.1
                    || Math.abs(controller.getLeftX()) > 0.1
                    || Math.abs(controller.getRightX()) > 0.1);
    var alliance = DriverStation.getAlliance();
    if (alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red) {
      driverControl
          .whileTrue(
              DriveCommands.joystickDrive(
                  drive,
                  () -> modifyJoystickAxis(controller.getLeftY(), false), // Changed to raw values
                  () -> modifyJoystickAxis(controller.getLeftX(), false), // Changed to raw values
                  () ->
                      modifyJoystickAxis(-controller.getRightX(), false))) // Changed to raw values
          .onFalse(DriveCommands.stopDriveCommand(drive));
    } else if (alliance.isPresent() && alliance.get() == DriverStation.Alliance.Blue) {
      driverControl
          .whileTrue(
              DriveCommands.joystickDrive(
                  drive,
                  () -> modifyJoystickAxis(-controller.getLeftY(), false), // Changed to raw values
                  () -> modifyJoystickAxis(-controller.getLeftX(), false), // Changed to raw values
                  () ->
                      modifyJoystickAxis(-controller.getRightX(), false))) // Changed to raw values
          .onFalse(DriveCommands.stopDriveCommand(drive));
    }

    // Lock to 0° when A button is held
    controller
        .a()
        .whileTrue(
            DriveCommands.joystickDriveAtAngle(
                drive,
                () -> -controller.getLeftY(),
                () -> -controller.getLeftX(),
                () -> new Rotation2d()));

    // Reset gyro to 0° when B button is pressed
    controller
        .b()
        .onTrue(
            Commands.runOnce(
                    () -> {
                      if (DriverStation.getAlliance().isPresent()
                          && DriverStation.getAlliance().get() == DriverStation.Alliance.Red) {
                        drive.setPose(
                            new Pose2d(
                                drive.getPose().getTranslation(),
                                new Rotation2d(Math.toRadians(0))));
                      } else {
                        drive.setPose(
                            new Pose2d(
                                drive.getPose().getTranslation(),
                                new Rotation2d(Math.toRadians(0))));
                      }
                    },
                    drive)
                .ignoringDisable(true));

    controller_two
        .back()
        .onTrue(
            Commands.runOnce(
                () -> {
                  drive.setPose(new Pose2d(4, 2, new Rotation2d(Math.toRadians(0))));
                },
                drive));

    controller_two
        .y()
        .onTrue(
            new InstantCommand(
                () -> {
                  System.out.println("current pose: " + drive.getPose());
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Test));
                  Logger.recordOutput("Robot/LineupSide", "Test");
                  Logger.recordOutput("Robot/IsLeftSide", true);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Test, YOffset.Center));
                }));

    Q1LeftLineup.onTrue(
        new InstantCommand(
                () -> {
                  System.out.println("current pose: " + drive.getPose());
                  // Log lineup target directly - much simpler!
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q1));
                  Logger.recordOutput("Robot/LineupSide", "Q1");
                  Logger.recordOutput("Robot/IsLeftSide", true);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q1, YOffset.Left));
                  System.out.println("current pose: " + drive.getPose());
                })
            .withName("Q1LeftLineup"));

    Q1RightLineup.onTrue(
        new InstantCommand(
                () -> {
                  System.out.println("current pose: " + drive.getPose());
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q1));
                  Logger.recordOutput("Robot/LineupSide", "Q1");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q1, YOffset.Right));
                  System.out.println("current pose: " + drive.getPose());
                })
            .withName("Q1RightLineup"));

    Q2LeftLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q2));
                  Logger.recordOutput("Robot/LineupSide", "Q2");
                  Logger.recordOutput("Robot/IsLeftSide", true);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q2, YOffset.Left));
                })
            .withName("Q2LeftLineup"));

    Q2RightLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q2));
                  Logger.recordOutput("Robot/LineupSide", "Q2");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q2, YOffset.Right));
                })
            .withName("Q2RightLineup"));

    Q3LeftLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q3));
                  Logger.recordOutput("Robot/LineupSide", "Q3");
                  Logger.recordOutput("Robot/IsLeftSide", true);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q3, YOffset.Left));
                })
            .withName("Q3LeftLineup"));

    Q3RightLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q3));
                  Logger.recordOutput("Robot/LineupSide", "Q3");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q3, YOffset.Right));
                })
            .withName("Q3RightLineup"));

    Q4LeftLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q4));
                  Logger.recordOutput("Robot/LineupSide", "Q4");
                  Logger.recordOutput("Robot/IsLeftSide", true);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q4, YOffset.Left));
                })
            .withName("Q4LeftLineup"));

    Q4RightLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q4));
                  Logger.recordOutput("Robot/LineupSide", "Q4");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q4, YOffset.Right));
                })
            .withName("Q4RightLineup"));

    Q5LeftLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q5));
                  Logger.recordOutput("Robot/LineupSide", "Q5");
                  Logger.recordOutput("Robot/IsLeftSide", true);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q5, YOffset.Left));
                })
            .withName("Q5LeftLineup"));

    Q5RightLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q5));
                  Logger.recordOutput("Robot/LineupSide", "Q5");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q5, YOffset.Right));
                })
            .withName("Q5RightLineup"));

    Q6LeftLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q6));
                  Logger.recordOutput("Robot/LineupSide", "Q6");
                  Logger.recordOutput("Robot/IsLeftSide", true);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q6, YOffset.Center));
                })
            .withName("Q6LeftLineup"));

    Q6RightLineup.onTrue(
        new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue), ReefSide.Q6));
                  Logger.recordOutput("Robot/LineupSide", "Q6");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.Q6, YOffset.Center));
                  System.out.println("current pose: " + drive.getPose());
                })
            .withName("Q6RightLineup"));

    controller_two
        .leftBumper()
        .onTrue(
            new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue),
                          ReefSide.LeftSubstation));
                  Logger.recordOutput("Robot/LineupSide", "LeftSubstation");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.LeftSubstation, YOffset.Center));
                  System.out.println("current pose: " + drive.getPose());
                }));
    controller_two
        .rightBumper()
        .onTrue(
            new InstantCommand(
                () -> {
                  Logger.recordOutput(
                      "Robot/TargetPose",
                      LineupCommand.getLineupTagPose(
                          DriverStation.getAlliance().orElse(Alliance.Blue),
                          ReefSide.RightSubstation));
                  Logger.recordOutput("Robot/LineupSide", "RightSubstation");
                  Logger.recordOutput("Robot/IsLeftSide", false);
                  CommandScheduler.getInstance()
                      .schedule(LineupCommand.Lineup(ReefSide.RightSubstation, YOffset.Center));
                }));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    try {
      return autoChooser.get();
    } catch (Exception ioe) {
      System.out.println("bad io error");
      return Commands.none();
    }
  }

  public Drive getDriveSubsystem() {
    return drive;
  }

  private double deadband(double value, double deadband) {
    // If controller reads very tiny value close to zero, we don't want to make the robot think it
    // has to move
    // Without deadband, robot will think it has to move, and then it will go crazy
    if (Math.abs(value) > deadband) {
      if (value > 0.0) {
        return (value - deadband) / (1.0 - deadband);
      } else {
        return (value + deadband) / (1.0 - deadband);
      }
    } else {
      return 0.0; // Return 0 if absolute value is within desired margin of error
    }
  }

  private double modifyJoystickAxis(double value, boolean isSlow) {
    // Deadband
    value = deadband(value, 0.025);

    // Square the axis
    value = Math.copySign(value * value, value);

    if (isSlow) {
      return 0.5 * value;
    }

    return value;
  }

  /**
   * Periodic method to log button states and other robot information. Call this from
   * Robot.teleopPeriodic() and Robot.autonomousPeriodic().
   */
  public void periodic() {
    // Log button states directly - much simpler!
    Logger.recordOutput("Buttons/Controller1/A", controller.a().getAsBoolean());
    Logger.recordOutput("Buttons/Controller1/B", controller.b().getAsBoolean());
    Logger.recordOutput("Buttons/Controller1/X", controller.x().getAsBoolean());
    Logger.recordOutput("Buttons/Controller1/Y", controller.y().getAsBoolean());

    Logger.recordOutput("Buttons/Controller2/A", controller_two.a().getAsBoolean());
    Logger.recordOutput("Buttons/Controller2/B", controller_two.b().getAsBoolean());
    Logger.recordOutput("Buttons/Controller2/X", controller_two.x().getAsBoolean());
    Logger.recordOutput("Buttons/Controller2/Y", controller_two.y().getAsBoolean());

    // Log button board states
    for (int i = 1; i <= 6; i++) {
      Logger.recordOutput("Buttons/ButtonBoard1A/Button" + i, buttonBoard1A.getRawButton(i));
      Logger.recordOutput("Buttons/ButtonBoard1B/Button" + i, buttonBoard1B.getRawButton(i));
    }

    // Log command scheduler status
    Logger.recordOutput("Commands/SchedulerActive", true);
    Logger.recordOutput("Commands/LogTime", System.currentTimeMillis());

    // Log command information with names
    Command driveCmd = drive.getCurrentCommand();

    Logger.recordOutput("Commands/DriveCommand", driveCmd != null ? driveCmd.getName() : "None");

    // Log if commands are running
    Logger.recordOutput("Commands/DriveCommandActive", driveCmd != null);
  }
}

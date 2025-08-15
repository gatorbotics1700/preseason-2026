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

//TODO: we do a lot of things with changing heading and reseting odometry, how does that change with sim?
//TODO: and slow drive, that's definitely messed up, redundant somewhere

package frc.robot.subsystems.drive;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.CANBus;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.ModuleConfig;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.pathfinding.Pathfinding;
import com.pathplanner.lib.util.PathPlannerLogging;
import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants;
import frc.robot.Constants.Mode;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.DrivetrainSubsystemInterface;
import frc.robot.util.LocalADStarAK;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import org.ironmaple.simulation.drivesims.COTS;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;
import org.ironmaple.simulation.drivesims.configs.SwerveModuleSimulationConfig;
import org.littletonrobotics.junction.AutoLogOutput;
import org.littletonrobotics.junction.Logger;

public class DrivetrainSim extends SubsystemBase implements DrivetrainSubsystemInterface {
    // TunerConstants doesn't include these constants, so they are declared locally
    static final double ODOMETRY_FREQUENCY =
            new CANBus(TunerConstants.DrivetrainConstants.CANBusName).isNetworkFD() ? 250.0 : 100.0;
    public static final double DRIVE_BASE_RADIUS = Math.max(
            Math.max(
                    Math.hypot(TunerConstants.FrontLeft.LocationX, TunerConstants.FrontLeft.LocationY),
                    Math.hypot(TunerConstants.FrontRight.LocationX, TunerConstants.FrontRight.LocationY)),
            Math.max(
                    Math.hypot(TunerConstants.BackLeft.LocationX, TunerConstants.BackLeft.LocationY),
                    Math.hypot(TunerConstants.BackRight.LocationX, TunerConstants.BackRight.LocationY)));

    // PathPlanner config constants
    private static final double ROBOT_MASS_KG = 74.088;
    private static final double ROBOT_MOI = 6.883;
    private static final double WHEEL_COF = 1.2;
    private static final RobotConfig PP_CONFIG = new RobotConfig(
            ROBOT_MASS_KG,
            ROBOT_MOI,
            new ModuleConfig(
                    TunerConstants.FrontLeft.WheelRadius,
                    TunerConstants.kSpeedAt12Volts.in(MetersPerSecond),
                    WHEEL_COF,
                    DCMotor.getKrakenX60Foc(1).withReduction(TunerConstants.FrontLeft.DriveMotorGearRatio),
                    TunerConstants.FrontLeft.SlipCurrent,
                    1),
            getModuleTranslations());

    public static final DriveTrainSimulationConfig mapleSimConfig = DriveTrainSimulationConfig.Default()
            .withRobotMass(Kilograms.of(ROBOT_MASS_KG))
            .withCustomModuleTranslations(getModuleTranslations())
            .withGyro(COTS.ofPigeon2())
            .withSwerveModule(new SwerveModuleSimulationConfig(
                    DCMotor.getKrakenX60(1),
                    DCMotor.getFalcon500(1),
                    TunerConstants.FrontLeft.DriveMotorGearRatio,
                    TunerConstants.FrontLeft.SteerMotorGearRatio,
                    Volts.of(TunerConstants.FrontLeft.DriveFrictionVoltage),
                    Volts.of(TunerConstants.FrontLeft.SteerFrictionVoltage),
                    Meters.of(TunerConstants.FrontLeft.WheelRadius),
                    KilogramSquareMeters.of(TunerConstants.FrontLeft.SteerInertia),
                    WHEEL_COF));

    static final Lock odometryLock = new ReentrantLock();
    private final GyroIO gyroIO;
    private final GyroIOInputsAutoLogged gyroInputs = new GyroIOInputsAutoLogged();
    private final Module[] modules = new Module[4]; // FL, FR, BL, BR
    private final SysIdRoutine sysId;
    private final Alert gyroDisconnectedAlert =
            new Alert("Disconnected gyro, using kinematics as fallback.", AlertType.kError);

    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(getModuleTranslations());
    private Rotation2d rawGyroRotation = new Rotation2d();
    private final SwerveModulePosition[] lastModulePositions = // For delta tracking
            new SwerveModulePosition[] {
                new SwerveModulePosition(),
                new SwerveModulePosition(),
                new SwerveModulePosition(),
                new SwerveModulePosition()
            };
    private final SwerveDrivePoseEstimator odometry =
            new SwerveDrivePoseEstimator(kinematics, rawGyroRotation, lastModulePositions, new Pose2d());

    private final Consumer<Pose2d> resetSimulationPoseCallBack;

    private boolean robotRelativeDrive = false;
    private boolean slowDrive = false;
    private static final double SLOW_DRIVE_SCALAR = 0.5; // 50% speed in slow mode

    public DrivetrainSim(
            GyroIO gyroIO,
            ModuleIO flModuleIO,
            ModuleIO frModuleIO,
            ModuleIO blModuleIO,
            ModuleIO brModuleIO,
            Consumer<Pose2d> resetSimulationPoseCallBack) {
        this.gyroIO = gyroIO;
        this.resetSimulationPoseCallBack = resetSimulationPoseCallBack;
        modules[0] = new Module(flModuleIO, 0, TunerConstants.FrontLeft);
        modules[1] = new Module(frModuleIO, 1, TunerConstants.FrontRight);
        modules[2] = new Module(blModuleIO, 2, TunerConstants.BackLeft);
        modules[3] = new Module(brModuleIO, 3, TunerConstants.BackRight);

        // Usage reporting for swerve template
        HAL.report(tResourceType.kResourceType_RobotDrive, tInstances.kRobotDriveSwerve_AdvantageKit);

        // Start odometry thread
        PhoenixOdometryThread.getInstance().start();

        // Configure AutoBuilder for PathPlanner
        AutoBuilder.configure(
                this::getPose,
                this::setPose,
                this::getRobotRelativeSpeeds,
                this::driveRobotRelative,
                new PPHolonomicDriveController(new PIDConstants(5.0, 0.0, 0.0), new PIDConstants(5.0, 0.0, 0.0)),
                PP_CONFIG,
                () -> DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red,
                this);
        Pathfinding.setPathfinder(new LocalADStarAK());
        PathPlannerLogging.setLogActivePathCallback((activePath) -> {
            Logger.recordOutput("Odometry/Trajectory", activePath.toArray(new Pose2d[activePath.size()]));
        });
        PathPlannerLogging.setLogTargetPoseCallback((targetPose) -> {
            Logger.recordOutput("Odometry/TrajectorySetpoint", targetPose);
        });

        // Configure SysId
        sysId = new SysIdRoutine(
                new SysIdRoutine.Config(
                        null, null, null, (state) -> Logger.recordOutput("Drive/SysIdState", state.toString())),
                new SysIdRoutine.Mechanism((voltage) -> runCharacterization(voltage.in(Volts)), null, this));
    }

    @Override
    public void periodic() {
        odometryLock.lock(); // Prevents odometry updates while reading data
        gyroIO.updateInputs(gyroInputs);
        Logger.processInputs("Drive/Gyro", gyroInputs);
        for (var module : modules) {
            module.periodic();
        }
        odometryLock.unlock();

        // Stop moving when disabled
        if (DriverStation.isDisabled()) {
            for (var module : modules) {
                module.stop();
            }
        }

        // Log empty setpoint states when disabled
        if (DriverStation.isDisabled()) {
            Logger.recordOutput("SwerveStates/Setpoints", new SwerveModuleState[] {});
            Logger.recordOutput("SwerveStates/SetpointsOptimized", new SwerveModuleState[] {});
        }

        // Update odometry
        double[] sampleTimestamps = modules[0].getOdometryTimestamps(); // All signals are sampled together
        int sampleCount = sampleTimestamps.length;
        for (int i = 0; i < sampleCount; i++) {
            // Read wheel positions and deltas from each module
            SwerveModulePosition[] modulePositions = new SwerveModulePosition[4];
            SwerveModulePosition[] moduleDeltas = new SwerveModulePosition[4];
            for (int moduleIndex = 0; moduleIndex < 4; moduleIndex++) {
                modulePositions[moduleIndex] = modules[moduleIndex].getOdometryPositions()[i];
                moduleDeltas[moduleIndex] = new SwerveModulePosition(
                        modulePositions[moduleIndex].distanceMeters - lastModulePositions[moduleIndex].distanceMeters,
                        modulePositions[moduleIndex].angle);
                lastModulePositions[moduleIndex] = modulePositions[moduleIndex];
            }

            // Update gyro angle
            if (gyroInputs.connected) {
                // Use the real gyro angle
                rawGyroRotation = gyroInputs.odometryYawPositions[i];
            } else {
                // Use the angle delta from the kinematics and module deltas
                Twist2d twist = kinematics.toTwist2d(moduleDeltas);
                rawGyroRotation = rawGyroRotation.plus(new Rotation2d(twist.dtheta));
            }

            // Apply update
            odometry.updateWithTime(sampleTimestamps[i], rawGyroRotation, modulePositions);
        }

        // Update gyro alert
        gyroDisconnectedAlert.set(!gyroInputs.connected && Constants.currentMode != Mode.SIM);
    }

    /**
     * Runs the drive at the desired velocity.
     *
     * @param speeds Speeds in meters/sec
     */
    public void setStates(SwerveModuleState[] setpointStates) {
        // Calculate module setpoints
        SwerveDriveKinematics.desaturateWheelSpeeds(setpointStates, TunerConstants.kSpeedAt12Volts);

        // Log unoptimized setpoints and setpoint speeds
        Logger.recordOutput("SwerveStates/Setpoints", setpointStates);

        // Send setpoints to modules
        for (int i = 0; i < 4; i++) {
            modules[i].runSetpoint(setpointStates[i]);
        }

        // Log optimized setpoints (runSetpoint mutates each state)
        Logger.recordOutput("SwerveStates/SetpointsOptimized", setpointStates);
    }

    /** Runs the drive in a straight line with the specified drive output. */
    public void runCharacterization(double output) {
        for (int i = 0; i < 4; i++) {
            modules[i].runCharacterization(output);
        }
    }

    /** Stops the drive. */
    public void stop() {
        ChassisSpeeds speeds = new ChassisSpeeds();
        SwerveModuleState[] setpointStates = kinematics.toSwerveModuleStates(speeds);
        setStates(setpointStates);
    }

    /**
     * Stops the drive and turns the modules to an X arrangement to resist movement. The modules will return to their
     * normal orientations the next time a nonzero velocity is requested.
     */
    public void stopWithX() {
        Rotation2d[] headings = new Rotation2d[4];
        for (int i = 0; i < 4; i++) {
            headings[i] = getModuleTranslations()[i].getAngle();
        }
        kinematics.resetHeadings(headings);
        stop();
    }

    /** Returns a command to run a quasistatic test in the specified direction. */
    public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
        return run(() -> runCharacterization(0.0)).withTimeout(1.0).andThen(sysId.quasistatic(direction));
    }

    /** Returns a command to run a dynamic test in the specified direction. */
    public Command sysIdDynamic(SysIdRoutine.Direction direction) {
        return run(() -> runCharacterization(0.0)).withTimeout(1.0).andThen(sysId.dynamic(direction));
    }

    /** Returns the module states received this cycle. */
    @AutoLogOutput(key = "SwerveStates/Measured")
    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] states = new SwerveModuleState[4];
        for (int i = 0; i < 4; i++) {
            states[i] = modules[i].getState();
        }
        return states;
    }

    /** Returns the module positions (turn angles and drive positions) for all of the modules. */
    public SwerveModulePosition[] getModulePositionArray() {
        SwerveModulePosition[] states = new SwerveModulePosition[4];
        for (int i = 0; i < 4; i++) {
            states[i] = modules[i].getPosition();
        }
        return states;
    }

    /** Returns the measured chassis speeds of the robot. */
    @AutoLogOutput(key = "SwerveChassisSpeeds/Measured")
    public ChassisSpeeds getRobotRelativeSpeeds() {
        return kinematics.toChassisSpeeds(getModuleStates());
    }

    /** Returns the timestamps of the samples received this cycle. */
    public double[] getOdometryTimestamps() {
        return modules[0].getOdometryTimestamps();
    }

    /** Returns the position of each module in radians. */
    public double[] getWheelRadiusCharacterizationPositions() {
        double[] values = new double[4];
        for (int i = 0; i < 4; i++) {
            values[i] = modules[i].getWheelRadiusCharacterizationPosition();
        }
        return values;
    }

    /** Returns the average velocity of the modules in rotations/sec (Phoenix native units). */
    public double getFFCharacterizationVelocity() {
        double output = 0.0;
        for (int i = 0; i < 4; i++) {
            output += modules[i].getFFCharacterizationVelocity() / 4.0;
        }
        return output;
    }

    /** Returns the current odometry pose. */
    @AutoLogOutput(key = "Odometry/Robot")
    public Pose2d getPose() {
        return odometry.getEstimatedPosition();
    }

    /** Returns the current odometry rotation. */
    public Rotation2d getRotation() {
        return getPose().getRotation();
    }

    public double getRobotRotationDegrees(){
        return getPose().getRotation().getDegrees();
    }

    /** Resets the current odometry pose. */
    public void setPose(Pose2d pose) {
        resetSimulationPoseCallBack.accept(pose);
        odometry.resetPosition(rawGyroRotation, getModulePositionArray(), pose);
    }

    /** Returns the maximum linear speed in meters per sec. */
    public double getMaxLinearSpeedMetersPerSec() {
        return TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
    }

    /** Returns the maximum angular speed in radians per sec. */
    public double getMaxAngularSpeedRadPerSec() {
        return getMaxLinearSpeedMetersPerSec() / DRIVE_BASE_RADIUS;
    }

    /** Returns an array of module translations. */
    public static Translation2d[] getModuleTranslations() {
        return new Translation2d[] {
            new Translation2d(TunerConstants.FrontLeft.LocationX, TunerConstants.FrontLeft.LocationY),
            new Translation2d(TunerConstants.FrontRight.LocationX, TunerConstants.FrontRight.LocationY),
            new Translation2d(TunerConstants.BackLeft.LocationX, TunerConstants.BackLeft.LocationY),
            new Translation2d(TunerConstants.BackRight.LocationX, TunerConstants.BackRight.LocationY)
        };
    }

    @Override
    public void drive(ChassisSpeeds speeds) {
        // Apply slow drive scaling if enabled
        if (slowDrive) {
            speeds = new ChassisSpeeds(
                    speeds.vxMetersPerSecond * SLOW_DRIVE_SCALAR,
                    speeds.vyMetersPerSecond * SLOW_DRIVE_SCALAR,
                    speeds.omegaRadiansPerSecond * SLOW_DRIVE_SCALAR);
        }

        // Convert to robot-relative if needed
        if (robotRelativeDrive) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
                    speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, speeds.omegaRadiansPerSecond, getRotation());
        }
        
        speeds = ChassisSpeeds.discretize(speeds, Constants.LOOPTIME_SECONDS);
        SwerveModuleState[] setpointStates = kinematics.toSwerveModuleStates(speeds);
        setStates(setpointStates);
    }

    @Override
    public void driveRobotRelative(ChassisSpeeds speeds) {
        // Apply slow drive scaling if enabled
        if (slowDrive) {
            speeds = new ChassisSpeeds(
                    speeds.vxMetersPerSecond * SLOW_DRIVE_SCALAR,
                    speeds.vyMetersPerSecond * SLOW_DRIVE_SCALAR,
                    speeds.omegaRadiansPerSecond * SLOW_DRIVE_SCALAR);
        }
        speeds = ChassisSpeeds.discretize(speeds, Constants.LOOPTIME_SECONDS);
        SwerveModuleState[] setpointStates = kinematics.toSwerveModuleStates(speeds);
        setStates(setpointStates);
    }


    @Override
    public void resetPose(Pose2d pose) {
        setPose(pose);
    }

    @Override
    public void setNotAtDesiredPose() {
        // This implementation doesn't use pose control state management
        // so this is a no-op
    }

    @Override
    public void driveToPose(Pose2d desiredPose) {
        // Use PathPlanner's built-in pose following
        System.out.println("scheduling drive to pose " + desiredPose);
        CommandScheduler.getInstance().schedule(AutoBuilder.pathfindToPose(
                desiredPose,
                new PathConstraints(
                        TunerConstants.kSpeedAt12Volts.in(MetersPerSecond),
                        TunerConstants.kSpeedAt12Volts.in(MetersPerSecond) / 2.0,
                        getMaxAngularSpeedRadPerSec(),
                        getMaxAngularSpeedRadPerSec() / 2.0))
        );
    }

    //starts out pointing at apriltag, then turns to be parallel with the tag once it's close enough
    public void driveToPoseWithInitialAngle(Pose2d desiredPose, Rotation2d pointingToTagAngle) { 
        Pose2d currentPose = odometry.getEstimatedPosition();
        double xError = desiredPose.getX() - currentPose.getX();
        double yError = desiredPose.getY() - currentPose.getY();
        if (Math.abs(xError) > 0.6 && Math.abs(yError) > 0.6) {
            desiredPose = new Pose2d(desiredPose.getX(), desiredPose.getY(), pointingToTagAngle);
            System.out.println("POINTING TO ANGLE");
        }
        driveToPose(desiredPose);
    }

    @Override
    public boolean getAtDesiredPose() {
        // Since we're using PathPlanner's pose following, we'll consider ourselves at the goal
        // when we're not actively following a path. This is a simplification since PathPlanner
        // handles the actual path following and goal checking internally.
        return true;
    }

    @Override
    public void toggleRobotRelativeDrive() {
        robotRelativeDrive = !robotRelativeDrive;
        Logger.recordOutput("Drive/RobotRelative", robotRelativeDrive);
    }

    @Override
    public void setSlowDrive() {
        slowDrive = !slowDrive;
        Logger.recordOutput("Drive/SlowMode", slowDrive);
    }

    @Override
    public boolean getSlowDrive() {
        return slowDrive;
    }

    @Override
    public void zeroGyroscope() {
        var alliance = DriverStation.getAlliance();
        Rotation2d zeroedAngle = null;
        if (alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red) {
            zeroedAngle = Rotation2d.fromDegrees(180.0);
        } else if (alliance.isPresent() && alliance.get() == DriverStation.Alliance.Blue) {
            zeroedAngle = Rotation2d.fromDegrees(0.0);
        }
        if (zeroedAngle != null) {
            setPose(new Pose2d(getPose().getX(), getPose().getY(), zeroedAngle));
        } else {
            System.err.println("zeroed angle was null -- setting to 0");
            setPose(new Pose2d(getPose().getX(), getPose().getY(), Rotation2d.fromDegrees(0.0)));
        }
    }

    public void robotRelativeHeading(double offsetAngle) {
        var alliance = DriverStation.getAlliance();
        Rotation2d zeroedAngle = null;
        if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red){
            zeroedAngle = Rotation2d.fromDegrees(180.0 - offsetAngle);
        } else if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Blue){
            zeroedAngle = Rotation2d.fromDegrees(0.0 - offsetAngle);
        }
        if(zeroedAngle != null){
            odometry.resetPosition(
                getRotation(),
                new SwerveModulePosition[] { modules[0].getPosition(), modules[1].getPosition(),
                    modules[2].getPosition(), modules[3].getPosition() },
                new Pose2d(odometry.getEstimatedPosition().getX(), odometry.getEstimatedPosition().getY(),
                    zeroedAngle)
            );
        } else {
            System.err.println("zeroed angle was null -- setting to 0");
            odometry.resetPosition(
                getRotation(),
                new SwerveModulePosition[] { modules[0].getPosition(), modules[1].getPosition(),
                    modules[2].getPosition(), modules[3].getPosition() },
                new Pose2d(odometry.getEstimatedPosition().getX(), odometry.getEstimatedPosition().getY(),
                    Rotation2d.fromDegrees(0.0))
            );
        }   
    }

    public Rotation2d angleToPoint(double deltaX, double deltaY){ //delta being the target-current point
        return new Rotation2d(Math.atan2(deltaY, deltaX));
    }

    @Override
    public void facePoint(Translation2d target) {
        Pose2d currentPose = getPose();
        double deltaX = target.getX() - currentPose.getX();
        double deltaY = target.getY() - currentPose.getY();
        Rotation2d targetRotation = angleToPoint(deltaX,deltaY);
        // Create a pose that maintains current position but updates rotation
        Pose2d targetPose = new Pose2d(currentPose.getTranslation(), targetRotation);
        System.out.println("driving to " + targetPose);
        
        // Use PathPlanner's pose following to rotate to the target angle
        this.driveToPose(targetPose);
    }

        public void driveADirection(double direction){ 
        //direction should be robot relative and in degrees. for clarity direction means angle at which we are driving, it does not refer to the robot heading
        //for anyone that knows polar coordinates you can think of direction as theta and in this case our r would be infinite because we just keep driving
        Pose2d currentPose = odometry.getEstimatedPosition();
        //the robot drives field relative so we need to find the field relative direction we want to drive in by adding the current heading to the desired robot relative drive direction
        double fieldRelativeDirection = direction+currentPose.getRotation().getDegrees();
        fieldRelativeDirection = MathUtil.inputModulus(fieldRelativeDirection, -180, 180);

        //think unit circle and the math will make sense (basically scaling x and y speed to get us to drive in at a specific angle)
        double xSpeed = Math.cos(Math.toRadians(fieldRelativeDirection))*0.9;
        double ySpeed = Math.sin(Math.toRadians(fieldRelativeDirection))*0.9;
        double rotationSpeed = 0; //because we aren't altering heading
        
        drive(ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotationSpeed, currentPose.getRotation()));
    }
}

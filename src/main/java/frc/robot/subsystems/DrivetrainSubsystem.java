package frc.robot.subsystems;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.ModuleConfig;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import frc.com.swervedrivespecialties.swervelib.MkSwerveModuleBuilder;
import frc.com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import frc.com.swervedrivespecialties.swervelib.SwerveModule;
import frc.robot.Constants;
import frc.robot.LimelightHelpers;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DrivetrainSubsystem extends SubsystemBase {
    private static final double MAX_VOLTAGE = 12.0;
    public static final double MAX_VELOCITY_METERS_PER_SECOND = 6380 / 60
            * SdsModuleConfigurations.MK4_L2.getDriveReduction() * SdsModuleConfigurations.MK4_L2.getWheelDiameter()
            * Math.PI;
    public static final double MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND = MAX_VELOCITY_METERS_PER_SECOND /
            Math.hypot(Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0, Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0);

    private final SwerveModule frontLeftModule;
    private final SwerveModule frontRightModule;
    private final SwerveModule backLeftModule;
    private final SwerveModule backRightModule;
    private static boolean atDesiredPose = false;

    public static final double MAX_ACCELERATION = 11.0;

    private static Pigeon2 pigeon;

    private final SwerveDriveKinematics kinematics;

    private final SwerveDrivePoseEstimator odometry;

    private SwerveModuleState[] states;

    private ChassisSpeeds chassisSpeeds;

    private ShuffleboardTab shuffleboardTab;
    private final double TRANSLATION_kP = 2.5;
    //private final double TRANSLATION_kD = 0.05;
    private final double ROTATION_kP = 0.02;
    //private final double ROTATION_kD = 0.0003;
    private final double TRANSLATION_MIN_SPEED = 0.15;
    private final double ROTATION_MIN_SPEED = 0.25;
    private final double DISTANCE_DEADBAND = 0.0225;
    private final double ROTATION_DEADBAND = 1.0;
    private double robotRotation;

    private boolean robotRelativeDrive;

    private boolean slowDrive;
    private static CANBus CANivore = new CANBus(Constants.CANIVORE_BUS_NAME);
    public static double busUtil = CANivore.getStatus().BusUtilization*100; //bus utilization percentage
    public static boolean isFD = CANivore.isNetworkFD(); //checks if we're running CAN FD protocol
    public static double transmitErrors = CANivore.getStatus().TEC;
    public static double receiveErrors = CANivore.getStatus().REC;


    public DrivetrainSubsystem() {
        slowDrive = false;
        shuffleboardTab = Shuffleboard.getTab("Drivetrain");

        pigeon = new Pigeon2(Constants.DRIVETRAIN_PIGEON_ID, Constants.CANIVORE_BUS_NAME);

        kinematics = new SwerveDriveKinematics(
                new Translation2d(Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                        Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
                new Translation2d(Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                        -Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
                new Translation2d(-Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                        Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0),
                new Translation2d(-Constants.DRIVETRAIN_TRACKWIDTH_METERS / 2.0,
                        -Constants.DRIVETRAIN_WHEELBASE_METERS / 2.0));

        chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);

        frontLeftModule = new MkSwerveModuleBuilder()
                .withLayout(shuffleboardTab.getLayout("Front Left Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(0, 0))
                .withGearRatio(Constants.MODULE_CONFIGURATION)
                .withDriveMotor(Constants.FRONT_LEFT_MODULE_DRIVE_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerMotor(Constants.FRONT_LEFT_MODULE_STEER_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerEncoderPort(Constants.FRONT_LEFT_MODULE_STEER_ENCODER, Constants.CANIVORE_BUS_NAME)
                .withSteerOffset(Constants.FRONT_LEFT_MODULE_STEER_OFFSET)
                .build();

        frontRightModule = new MkSwerveModuleBuilder()
                .withLayout(shuffleboardTab.getLayout("Front Right Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(2, 0))
                .withGearRatio(Constants.MODULE_CONFIGURATION)
                .withDriveMotor(Constants.FRONT_RIGHT_MODULE_DRIVE_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerMotor(Constants.FRONT_RIGHT_MODULE_STEER_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerEncoderPort(Constants.FRONT_RIGHT_MODULE_STEER_ENCODER, Constants.CANIVORE_BUS_NAME)
                .withSteerOffset(Constants.FRONT_RIGHT_MODULE_STEER_OFFSET)
                .build();

        backLeftModule = new MkSwerveModuleBuilder()
                .withLayout(shuffleboardTab.getLayout("Back Left Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(4, 0))
                .withGearRatio(Constants.MODULE_CONFIGURATION)
                .withDriveMotor(Constants.BACK_LEFT_MODULE_DRIVE_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerMotor(Constants.BACK_LEFT_MODULE_STEER_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerEncoderPort(Constants.BACK_LEFT_MODULE_STEER_ENCODER, Constants.CANIVORE_BUS_NAME)
                .withSteerOffset(Constants.BACK_LEFT_MODULE_STEER_OFFSET)
                .build();

        backRightModule = new MkSwerveModuleBuilder()
                .withLayout(shuffleboardTab.getLayout("Back Right Module", BuiltInLayouts.kList)
                        .withSize(2, 4)
                        .withPosition(6, 0))
                .withGearRatio(Constants.MODULE_CONFIGURATION)
                .withDriveMotor(Constants.BACK_RIGHT_MODULE_DRIVE_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerMotor(Constants.BACK_RIGHT_MODULE_STEER_MOTOR, Constants.CANIVORE_BUS_NAME)
                .withSteerEncoderPort(Constants.BACK_RIGHT_MODULE_STEER_ENCODER, Constants.CANIVORE_BUS_NAME)
                .withSteerOffset(Constants.BACK_RIGHT_MODULE_STEER_OFFSET)
                .build();

        odometry = new SwerveDrivePoseEstimator(
                kinematics,
                new Rotation2d(Math.toRadians(pigeon.getYaw().getValueAsDouble())),
                new SwerveModulePosition[] { frontLeftModule.getPosition(), frontRightModule.getPosition(),
                        backLeftModule.getPosition(), backRightModule.getPosition() },
                new Pose2d(0, 0, new Rotation2d(Math.toRadians(0))));
        states = kinematics.toSwerveModuleStates(chassisSpeeds);
        DCMotor krakenMotor = DCMotor.getKrakenX60(1);// TODO: check if we change this number to 1 or keep

        ModuleConfig moduleConfig = new ModuleConfig(
                0.0508, // wheel radius in meters (example: 3 inches converted to meters)
                4.17, // max drive velocity in meters per second
                1.3, // coefficient of friction TODO: ask patricia
                krakenMotor, // DCMotor object
                55.0, // current limit in Amps
                1 // number of motors (e.g., 1 for swerve module)
        );
        RobotConfig config;
        try {
            config = RobotConfig.fromGUISettings();
        } catch (Exception e) {
            // Handle exception as needed???
            e.printStackTrace();
            config = new RobotConfig(16, 1.075, moduleConfig, 0.508);
        }

        AutoBuilder.configure(
                this::getPose,
                this::resetPose,
                this::getRobotRelativeSpeeds,
                this::driveRobotRelative,
                new PPHolonomicDriveController(
                        new PIDConstants(5, 0, 0.05),
                        new PIDConstants(10, 0, 0.01)
                ),
                config, // The robot configuration
                () -> {
                    return false;

                },
                this
        );

        shuffleboardTab.addNumber("Gyroscope Angle", () -> getRotation().getDegrees());
        shuffleboardTab.addNumber("Pose X", () -> odometry.getEstimatedPosition().getX());
        shuffleboardTab.addNumber("Pose Y", () -> odometry.getEstimatedPosition().getY());

    }

    public void setSlowDrive() {
        slowDrive = !slowDrive;
        // System.out.println("use of slow drive to not slow drive");
    }

    public boolean getSlowDrive() {
        return slowDrive;
    }

    public void zeroGyroscope() {
        var alliance = DriverStation.getAlliance();
        Rotation2d zeroedAngle = null;
        if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red){
            zeroedAngle = Rotation2d.fromDegrees(180.0);
        } else if(alliance.isPresent() && alliance.get() == DriverStation.Alliance.Blue){
            zeroedAngle = Rotation2d.fromDegrees(0.0);
        }
        if(zeroedAngle != null){
            odometry.resetPosition(
                new Rotation2d(Math.toRadians(pigeon.getYaw().getValueAsDouble())),
                new SwerveModulePosition[] { frontLeftModule.getPosition(), frontRightModule.getPosition(),
                    backLeftModule.getPosition(), backRightModule.getPosition() },
                new Pose2d(odometry.getEstimatedPosition().getX(), odometry.getEstimatedPosition().getY(),
                    zeroedAngle)
            );
        } else {
            System.err.println("zeroed angle was null -- setting to 0");
            odometry.resetPosition(
                new Rotation2d(Math.toRadians(pigeon.getYaw().getValueAsDouble())),
                new SwerveModulePosition[] { frontLeftModule.getPosition(), frontRightModule.getPosition(),
                    backLeftModule.getPosition(), backRightModule.getPosition() },
                new Pose2d(odometry.getEstimatedPosition().getX(), odometry.getEstimatedPosition().getY(),
                    Rotation2d.fromDegrees(0.0))
            );
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
                new Rotation2d(Math.toRadians(pigeon.getYaw().getValueAsDouble())),
                new SwerveModulePosition[] { frontLeftModule.getPosition(), frontRightModule.getPosition(),
                    backLeftModule.getPosition(), backRightModule.getPosition() },
                new Pose2d(odometry.getEstimatedPosition().getX(), odometry.getEstimatedPosition().getY(),
                    zeroedAngle)
            );
        } else {
            System.err.println("zeroed angle was null -- setting to 0");
            odometry.resetPosition(
                new Rotation2d(Math.toRadians(pigeon.getYaw().getValueAsDouble())),
                new SwerveModulePosition[] { frontLeftModule.getPosition(), frontRightModule.getPosition(),
                    backLeftModule.getPosition(), backRightModule.getPosition() },
                new Pose2d(odometry.getEstimatedPosition().getX(), odometry.getEstimatedPosition().getY(),
                    Rotation2d.fromDegrees(0.0))
            );
        }
        
    }

    public Pose2d getPose() {
        return odometry.getEstimatedPosition();
    }

    public static double getRobotAngle() {
        return pigeon.getYaw().getValueAsDouble(); // in degrees?
    }

    public void resetPose(Pose2d pose) {
        odometry.resetPosition(new Rotation2d(Math.toRadians(pigeon.getYaw().getValueAsDouble())),
                getModulePositionArray(), pose);
    }

    public SwerveModulePosition[] getModulePositionArray() {
        return new SwerveModulePosition[] { frontLeftModule.getPosition(), frontRightModule.getPosition(),
                backLeftModule.getPosition(), backRightModule.getPosition() };
    }

    public Rotation2d getRotation() {
        return odometry.getEstimatedPosition().getRotation();
    }

    public double getRobotRotationDegrees(){
        return odometry.getEstimatedPosition().getRotation().getDegrees();
    }

    public ChassisSpeeds getRobotRelativeSpeeds() {
        return kinematics.toChassisSpeeds(getModuleStates());
    }

    public SwerveModuleState[] getModuleStates() {
        return states;
    }

    public void setStates(SwerveModuleState[] targetStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(targetStates, MAX_VELOCITY_METERS_PER_SECOND);

        // Calculate voltages (using a higher minimum voltage to ensure movement)
        double fl_voltage = (targetStates[0].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE;
        double fr_voltage = (targetStates[1].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE;
        double bl_voltage = (targetStates[2].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE;
        double br_voltage = (targetStates[3].speedMetersPerSecond / MAX_VELOCITY_METERS_PER_SECOND) * MAX_VOLTAGE;

        // Set modules with calculated voltages
        frontLeftModule.set(fl_voltage, targetStates[0].angle.getRadians());
        frontRightModule.set(fr_voltage, targetStates[1].angle.getRadians());
        backLeftModule.set(bl_voltage, targetStates[2].angle.getRadians());
        backRightModule.set(br_voltage, targetStates[3].angle.getRadians());
    }

    public void drive(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;

        // Convert chassis speeds to module states and apply them
        ChassisSpeeds targetSpeeds = ChassisSpeeds.discretize(chassisSpeeds, Constants.LOOPTIME_SECONDS);
        SwerveModuleState[] targetStates = kinematics.toSwerveModuleStates(targetSpeeds);
        setStates(targetStates);
    }

    public void driveRobotRelative(ChassisSpeeds robotRelativeSpeeds) {
        ChassisSpeeds targetSpeeds = ChassisSpeeds.discretize(robotRelativeSpeeds, 0.02);
        SwerveModuleState[] targetStates = kinematics.toSwerveModuleStates(targetSpeeds);
        setStates(targetStates);
    }

    @Override
    public void periodic() {

        if(robotRelativeDrive){
            robotRelativeHeading(180);
        }

        odometry.update(
            new Rotation2d(Math.toRadians(pigeon.getYaw().getValueAsDouble())),
            new SwerveModulePosition[]{ 
                frontLeftModule.getPosition(), 
                frontRightModule.getPosition(), 
                backLeftModule.getPosition(), 
                backRightModule.getPosition() 
            }
        );
        boolean doRejectUpdate = false;
        
        LimelightHelpers.SetRobotOrientation("limelight", odometry.getEstimatedPosition().getRotation().getDegrees(), 0, 0, 0, 0, 0);
        LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight");
        if(mt2 != null){
            if(Math.abs(pigeon.getAngularVelocityZWorld().getValueAsDouble()) > 720){
                doRejectUpdate = true;
            }
            if(mt2.tagCount == 0){
                doRejectUpdate = true;
            }
            if(!doRejectUpdate){
                //this line sets the amount the pigeon odometry can be off by in each axis before vision odometry takes over. we trust the pigeon for heading, hence the reaaaaally big number
                odometry.setVisionMeasurementStdDevs(VecBuilder.fill(.05, .05, 999999)); 
                odometry.addVisionMeasurement(mt2.pose, mt2.timestampSeconds);
            }
        }
        
        SmartDashboard.putNumber("Gyroscope Angle", getRotation().getDegrees());
        SmartDashboard.putNumber("Pose X", odometry.getEstimatedPosition().getX());
        SmartDashboard.putNumber("Pose Y", odometry.getEstimatedPosition().getY());
    }

    public void driveToPose(Pose2d desiredPose) {
        Pose2d currentPose = odometry.getEstimatedPosition();
        double xError = desiredPose.getX() - currentPose.getX();
        double yError = desiredPose.getY() - currentPose.getY();
        double rotationError = desiredPose.getRotation().getDegrees() - currentPose.getRotation().getDegrees();
        rotationError = MathUtil.inputModulus(rotationError, -180, 180); // sets the value between -180 and 180

        if (Math.abs(xError) < DISTANCE_DEADBAND) { // Stop if within deadband
            xError = 0.0;
            // System.out.println("AT X DEADBAND");
        }

        if (Math.abs(yError) < DISTANCE_DEADBAND) {
            yError = 0.0;
            // System.out.println("AT Y DEADBAND");
        }
        
        if (Math.abs(rotationError) < ROTATION_DEADBAND) {
            rotationError = 0.0;
             System.out.println("AT ROTATION DEADBAND");
        }

        atDesiredPose = xError == 0.0 && yError == 0.0 && rotationError == 0.0;

        if (atDesiredPose) { // stop
            setStates(new SwerveModuleState[] {
                    new SwerveModuleState(0.0, new Rotation2d()),
                    new SwerveModuleState(0.0, new Rotation2d()),
                    new SwerveModuleState(0.0, new Rotation2d()),
                    new SwerveModuleState(0.0, new Rotation2d())
            });
            System.out.println("At desired pose, stopping.");
            return;
        }

        double xSpeed = Math.max(Math.abs(xError * TRANSLATION_kP), TRANSLATION_MIN_SPEED) * Math.signum(xError);
        double ySpeed = Math.max(Math.abs(yError * TRANSLATION_kP), TRANSLATION_MIN_SPEED) * Math.signum(yError);
        double rotationSpeed = Math.max(Math.abs(rotationError * ROTATION_kP), ROTATION_MIN_SPEED) * Math.signum(rotationError);
        if(xSpeed >= 1.8){
            xSpeed = 1.8;
        }
        if(ySpeed >= 1.8){
            ySpeed = 1.8;
        }
       System.out.println("rotationspeed:"+rotationSpeed);
        drive(ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotationSpeed, currentPose.getRotation()));
    }

    public void turnToAngle(Rotation2d desiredAngle){
        //driveToPose(new Pose2d(odometry.getEstimatedPosition().getTranslation(), desiredAngle));
        Pose2d currentPose = odometry.getEstimatedPosition();
        double rotationError = desiredAngle.getDegrees() - currentPose.getRotation().getDegrees();
        rotationError = MathUtil.inputModulus(rotationError, -180, 180); // sets the value between -180 and 180
        
        if (Math.abs(rotationError) < ROTATION_DEADBAND) {
            rotationError = 0.0;
             System.out.println("AT ROTATION DEADBAND");
        }

        atDesiredPose = rotationError == 0.0;

        if (atDesiredPose) { // stop
            setStates(new SwerveModuleState[] {
                    new SwerveModuleState(0.0, new Rotation2d()),
                    new SwerveModuleState(0.0, new Rotation2d()),
                    new SwerveModuleState(0.0, new Rotation2d()),
                    new SwerveModuleState(0.0, new Rotation2d())
            });
            System.out.println("At desired pose, stopping.");
            return;
        }

        double rotationSpeed = Math.max(Math.abs(rotationError * ROTATION_kP*5), ROTATION_MIN_SPEED) * Math.signum(rotationError);
        System.out.println("rotationspeed:"+rotationSpeed);
        drive(ChassisSpeeds.fromFieldRelativeSpeeds(0, 0, rotationSpeed, currentPose.getRotation()));
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

    public boolean getAtDesiredPose(){
        return atDesiredPose;
    }

    public void setNotAtDesiredPose(){
        atDesiredPose = false;
    }

    public Rotation2d angleToPoint(double deltaX, double deltaY){ //delta being the target-current point
        return new Rotation2d(Math.atan2(deltaY, deltaX));
    }

    public void facePoint(Translation2d target){
        Pose2d currentPose = odometry.getEstimatedPosition();
        double deltaX = target.getX() - currentPose.getX();
        double deltaY = target.getY() - currentPose.getY();
        Rotation2d targetRotation = angleToPoint(deltaX, deltaY);
        turnToAngle(targetRotation);
    }



    public void toggleRobotRelativeDrive(){
        robotRelativeDrive = !robotRelativeDrive;
    }

    // public void driveADirection(double direction, double angle){ //THIS WILL ONLY WORK IF DIRECTION IS FIELD RELATIVE
       
    //     Pose2d currentPose = odometry.getEstimatedPosition();
    //     double fieldRelativeDirection = direction+currentPose.getRotation().getDegrees();
    //     fieldRelativeDirection = direction;
    //     double rotationError = angle - currentPose.getRotation().getDegrees();
    //     rotationError = MathUtil.inputModulus(rotationError, -180, 180); // sets the value between -180 and 180

    //     if (Math.abs(rotationError) < ROTATION_DEADBAND) {
    //         rotationError = 0.0;
    //          System.out.println("AT ROTATION DEADBAND");
    //     }

    //     double xSpeed = Math.cos(Math.toRadians(fieldRelativeDirection))*0.5;
    //     double ySpeed = Math.sin(Math.toRadians(fieldRelativeDirection))*0.5;
    //     double rotationSpeed = Math.max(Math.abs(rotationError * ROTATION_kP), ROTATION_MIN_SPEED) * Math.signum(rotationError);
        
    //     if(rotationSpeed >= 1.8){
    //         rotationSpeed = 1.8;
    //     }
    
    //     drive(ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotationSpeed, currentPose.getRotation()));
    // }

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
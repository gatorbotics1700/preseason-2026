// package frc.robot.subsystems;

// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import frc.robot.LimelightHelpers;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Pose3d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Transform2d;
// import edu.wpi.first.math.geometry.Transform3d;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.math.geometry.Rotation3d;
// import edu.wpi.first.networktables.NetworkTable;
// import edu.wpi.first.networktables.NetworkTableInstance;
// import frc.robot.Constants;

// public class LimelightSubsystem extends SubsystemBase {

//     private final NetworkTable limelightTable;
//     private final Pose3d limelightOffsets;
//     private final String limelightName;

//     public LimelightSubsystem(String limelightName, Pose3d limelightOffsets) {
//         this.limelightOffsets = limelightOffsets;
//         this.limelightName = limelightName;
//         limelightTable = NetworkTableInstance.getDefault().getTable(limelightName);
        
//         LimelightHelpers.setCameraPose_RobotSpace(limelightName, 
//         limelightOffsets.getX() , -limelightOffsets.getY(), limelightOffsets.getZ(), //now flipping Y here instead of in constants.java
//         Math.toDegrees(limelightOffsets.getRotation().getX()), Math.toDegrees(limelightOffsets.getRotation().getY()), 
//         Math.toDegrees(limelightOffsets.getRotation().getZ()));
//     }

//     public String getLimelightName(){
//         return limelightName;
//     }

//     public void turnOnLED() {
//         limelightTable.getEntry("ledMode").setNumber(3); // 3 means force on
//     }

//     public void turnOffLED() {
//         limelightTable.getEntry("ledMode").setNumber(1); // 1 means force off
//     }

//     public boolean hasValidTarget() {
//         return limelightTable.getEntry("tv").getDouble(0.0) == 1.0;
//     }

//     //angle between center of camera and center of apriltag (degrees) -- doesn't account for apriltag angle
//     public double getHorizontalOffsetAngle() {
//         return limelightTable.getEntry("tx").getDouble(0.0);
//     }

//     public double getVerticalOffsetAngle() { //we don't trust this method for distance calculations -- don't use for that
//         return limelightTable.getEntry("ty").getDouble(0.0);
//     }

//     public double getTargetArea() {
//         return limelightTable.getEntry("ta").getDouble(0.0);
//     }

//     public double getSkew() {
//         return limelightTable.getEntry("ts").getDouble(0.0);
//     }

//     public double getLatency() {
//         return limelightTable.getEntry("tl").getDouble(0.0);
//     }

//     public double getTargetID() {
//         return limelightTable.getEntry("tid").getDouble(0.0);
//     }

//     //camera relative angle of apriltag (degrees)
//     public double getTagYaw() {
//         //returns yaw of april tag relative to camera
//         return limelightTable.getEntry("targetpose_cameraspace").getDoubleArray(new double[6])[4];
//     }
    
//     /**
//      * Calculates a target pose for the robot based on AprilTag detection from the Limelight.
//      * Uses vision data to determine the relative position and orientation to an AprilTag,
//      * then transforms this into field coordinates for robot positioning.
//      *
//      * @param robotPoseInFieldSpace The current Pose2d of the robot in field coordinates
//      * @return A Pose2d representing the target position and rotation of the center of the robot in field coordinates,
//      *         such that the front center will be aligned with the AprilTag,
//      *         calculated using the detected AprilTag's position and the robot's current pose.
//      *         The returned pose includes:
//      *         - X/Y coordinates offset from current position based on tag distance
//      *         - Rotation aligned parallel to the detected tag
//      *         Returns null if no AprilTag is detected or vision data is invalid.
//      */
//     public Pose2d aprilTagPoseInFieldSpace(Pose2d robotPoseInFieldSpace, Pose2d lineUpOffset) {
//         // distance to the camera from the tag (in camera's coordinate space)
//         double[] aprilTagArrayInCameraSpace = limelightTable.getEntry("targetpose_cameraspace").getDoubleArray(new double[6]);
       
//         if(aprilTagArrayInCameraSpace == null){ // idk if this is really necessary but better safe than sorry?
//             return null;
//         }
        
//         Pose3d aprilTagPoseInCameraSpace = arrayToPose3d(aprilTagArrayInCameraSpace);
//         Pose2d aprilTagPoseInRobotSpace = convertCameraSpaceToRobotSpace(aprilTagPoseInCameraSpace);
//         Pose2d aprilTagPoseFieldSpace = convertToFieldSpace(aprilTagPoseInRobotSpace, robotPoseInFieldSpace);
//         Pose2d aprilTagPoseOffsetFrontCenter = offsetToLineUpPoint(aprilTagPoseFieldSpace, lineUpOffset);
//         return aprilTagPoseOffsetFrontCenter;
//     }

//     //converts targetpose_cameraspace array into FRC coordinates. for a diagram of the coordinate system search for "targetpose_cameraspace coordinate system vs robot/pigeon coordinate system" in #progthoughts (2025 slack)
//     public Pose2d arrayToPose2d(double[] array){
//         // I HATE THIS IT'S EVIL AAAAAAAAA - Patricia
//         //the apriltag returns the camera relative pose as an array {TX, TY, TZ, PITCH, YAW, ROLL}
//         //positive TX is to the right, equivalent to the y axis on the pigeon but flipped (so when we convert to a pose we flip this to match the axes on the pigeon)
//         //positive TZ is straight forward, equivalent to the x axis on the pigeon
//         //positive YAW is clockwise, so we flip it when we convert to pose so that it matches all of our other rotational components
//         return new Pose2d(array[2], -array[0], new Rotation2d(Math.toRadians(-array[4])));
//     }

//     //converts targetpose_cameraspace array into FRC coordinates. for a diagram of the coordinate system search for "targetpose_cameraspace coordinate system vs robot/pigeon coordinate system" in #progthoughts (2025 slack)
//     public Pose3d arrayToPose3d(double[] array){
//         // I HATE THIS IT'S EVIL AAAAAAAAA - Patricia
//         //the apriltag returns the camera relative pose as an array {TX, TY, TZ, PITCH, YAW, ROLL}
//         //positive TX is to the right, equivalent to the y axis on the pigeon but flipped (so when we convert to a pose we flip this to match the axes on the pigeon)
//         //positive TZ is straight forward, equivalent to the x axis on the pigeon
//         //positive YAW is clockwise, so we flip it when we convert to pose so that it matches all of our other rotational components
//         return new Pose3d(array[2], -array[0], -array[1], new Rotation3d(Math.toRadians(array[5]), Math.toRadians(-array[3]), Math.toRadians(-array[4]))); // there's no way the angles on this are right and frankly we might not even need the tag pose to be a pose3d???
//     }

//     public Pose2d convertCameraSpaceToRobotSpace(Pose3d poseInCameraSpace){ 
//         // Create the 3D pose of camera relative to robot center (it's just the limelightOffsets but it makes more sense if you name it cameraPose lol)
//         Pose3d cameraPoseFromRobotCenter = limelightOffsets;
        
//         // Transform the camera pose by the input pose
//         Transform3d transform = new Transform3d(poseInCameraSpace.getTranslation(), poseInCameraSpace.getRotation());
//         Pose3d robotSpacePose3d = cameraPoseFromRobotCenter.transformBy(transform);
        
//         // Project to 2D by taking x/y components and yaw
//         Pose2d robotSpacePose = new Pose2d(
//             robotSpacePose3d.getX(),
//             robotSpacePose3d.getY(),
//             new Rotation2d(robotSpacePose3d.getRotation().getZ())
//         );

//         return robotSpacePose;
//     }

    
//     Pose2d convertToFieldSpace(Pose2d targetPoseInRobotSpace, Pose2d robotPoseInFieldSpace) {
//         Transform2d transform = new Transform2d(targetPoseInRobotSpace.getTranslation(), targetPoseInRobotSpace.getRotation()); //defines a transform
//         Pose2d fieldPose = robotPoseInFieldSpace.transformBy(transform); //applies the transform to the pose (transforms the robot's pose by the target pose)
//         return fieldPose;
//     }

//     //offsets a pose so that the front center of the robot will be at that point rather than the center
//     Pose2d offsetToFrontCenter (Pose2d targetPoseInFieldSpace){ 
//         Transform2d transform = new Transform2d(-Constants.CENTER_TO_BUMPER_OFFSET, 0, new Rotation2d(0)); //could change numbers here if we wanted to offset to a different part of the robot
//         Pose2d result = targetPoseInFieldSpace.transformBy(transform);
//         return result; 
//     }

//     Pose2d offsetToLineUpPoint (Pose2d targetPoseInFieldSpace, Pose2d lineUpPointRobotSpace){ 
//         Transform2d transform = new Transform2d(new Translation2d(-lineUpPointRobotSpace.getX(), -lineUpPointRobotSpace.getY()), lineUpPointRobotSpace.getRotation()); //we flip x and y because logically we want to move the center of the robot back by the offset in order to align the point with the apriltag (because we can only drive to a pose by giving it a target pose for the center)
//         Pose2d result = targetPoseInFieldSpace.transformBy(transform);
//         return result; 
//     }

//     public void setPipeline(int pipelineID) {
//         limelightTable.getEntry("pipeline").setNumber(pipelineID); // Set the pipeline ID
//     }

//     @Override
//     public void periodic() {

//     }
// }
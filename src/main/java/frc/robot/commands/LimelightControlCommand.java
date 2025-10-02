package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.vision.Vision;

public class LimelightControlCommand extends Command {
  private final Vision vision;
  private final Drive drive;
  private final CommandXboxController controller;
  private final int pipeline;
  private Pose2d desiredPose;
  private Pose2d lineUpOffset;
  private boolean dontStart = false;

  public LimelightControlCommand(
      Vision vision,
      Drive drive,
      int pipeline,
      CommandXboxController controller,
      Pose2d lineUpOffset) {
    this.vision = vision;
    this.drive = drive;
    this.pipeline = pipeline;
    this.controller = controller;
    this.lineUpOffset = lineUpOffset;

    addRequirements(vision, drive);
  }

  // @Override
  // public void initialize() {
  //     //limelightSubsystem.setPipeline(pipeline);
  //     //drivetrainSubsystem.setNotAtDesiredPose();
  //     if (vision.hasValidTarget() && targetMatchesPipeline()) {
  //         dontStart = false;
  //     } else {
  //         dontStart = true;
  //         System.out.println("\tNo valid target detected.");
  //     }
  // }

  // @Override
  // public void execute() {
  //     // makes sure we are looking at the correct id
  //     if (limelightSubsystem.hasValidTarget() && targetMatchesPipeline()) {
  //         updateDesiredPose();
  //     } else {
  //         System.out.println("\tNo valid target detected.");
  //     }

  //     if (desiredPose != null) {
  //         drivetrainSubsystem.driveToPose(desiredPose);
  //     }
  // }

  // @Override
  // public boolean isFinished() {

  //     // Check if either stick on the Xbox controller is moved
  //     boolean joystickMoved = Math.abs(controller.getLeftX()) > 0.2 ||
  //             Math.abs(controller.getLeftY()) > 0.2 ||
  //             Math.abs(controller.getRightX()) > 0.2 ||
  //             Math.abs(controller.getRightY()) > 0.2;
  //     if (joystickMoved) {
  //         System.out.println("Joystick moved, ending command.");
  //         desiredPose = null;
  //         return true;
  //     }

  //     if(drivetrainSubsystem.getAtDesiredPose()){
  //         return true;
  //     }
  //     if(dontStart){
  //         return true;
  //     }

  //     return false;
  // }

  // private void updateDesiredPose() {
  //     desiredPose = limelightSubsystem.aprilTagPoseInFieldSpace(drivetrainSubsystem.getPose(),
  // lineUpOffset);
  // }

  // private boolean targetMatchesPipeline() {
  //     if (pipeline == 1) {
  //         return limelightSubsystem.getTargetID() == 6 || limelightSubsystem.getTargetID() == 19;
  //     } else if (pipeline == 2) {
  //         return limelightSubsystem.getTargetID() == 7 || limelightSubsystem.getTargetID() == 10
  // ||limelightSubsystem.getTargetID() == 18 || limelightSubsystem.getTargetID() == 21;
  //     } else if (pipeline == 3){
  //         return limelightSubsystem.getTargetID() == 8 || limelightSubsystem.getTargetID() == 17;
  //     } else if (pipeline == 4){
  //         return limelightSubsystem.getTargetID() == 9 || limelightSubsystem.getTargetID() == 22;
  //     } else if (pipeline == 5){
  //         return limelightSubsystem.getTargetID() == 11 || limelightSubsystem.getTargetID() ==
  // 20;
  //     } else if (pipeline == 6){
  //         return limelightSubsystem.getTargetID() == 3 || limelightSubsystem.getTargetID() == 16;
  //     } else if (pipeline == 7){
  //         return limelightSubsystem.getTargetID() == 1 || limelightSubsystem.getTargetID() == 2
  // || limelightSubsystem.getTargetID() == 12 || limelightSubsystem.getTargetID() == 13;
  //     }  else if (pipeline == 8){
  //         return limelightSubsystem.getTargetID() == 4 || limelightSubsystem.getTargetID() == 14
  // || limelightSubsystem.getTargetID() == 5 || limelightSubsystem.getTargetID() == 15;
  //     } else if (pipeline == 9){
  //         return limelightSubsystem.getTargetID() == 5 || limelightSubsystem.getTargetID() == 15;
  //     } else{
  //         return false;
  //     }
  // }
}

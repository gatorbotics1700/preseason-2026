package frc.robot.commands;

import static edu.wpi.first.units.Units.Centimeters;
import static edu.wpi.first.units.Units.Degrees;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants;
import frc.robot.subsystems.vision.VisionConstants;

public class LineupCommand {

  public LineupCommand() {}

  public static enum ReefSide {
    Q1,
    Q2,
    Q3,
    Q4,
    Q5,
    Q6,
    LeftSubstation,
    RightSubstation,
    Test // give the robot a hardcoded pose to go to
  }

  public static enum YOffset {
    Left,
    Right,
    Center
  }

  public static Pose2d getLineupTagPose(Alliance alliance, ReefSide side) {
    if (alliance == Alliance.Red) {
      switch (side) {
        case Q1:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(10).get().toPose2d();
        case Q2:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(9).get().toPose2d();
        case Q3:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(8).get().toPose2d();
        case Q4:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(7).get().toPose2d();
        case Q5:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(6).get().toPose2d();
        case Q6:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(11).get().toPose2d();
        case LeftSubstation:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(1).get().toPose2d();
        case RightSubstation:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(2).get().toPose2d();
        case Test: // this test pose can be edited
          return new Pose2d(6, 2, new Rotation2d(0));
      }
    } else {
      switch (side) {
        case Q1:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(21).get().toPose2d();
        case Q2:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(22).get().toPose2d();
        case Q3:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(17).get().toPose2d();
        case Q4:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(18).get().toPose2d();
        case Q5:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(19).get().toPose2d();
        case Q6:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(20).get().toPose2d();
        case LeftSubstation:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(13).get().toPose2d();
        case RightSubstation:
          return VisionConstants.APRIL_TAG_LAYOUT.getTagPose(12).get().toPose2d();
        case Test: // this test pose can be edited
          return new Pose2d(6, 2, new Rotation2d(0));
      }
    }
    return null;
  }

  public static Command Lineup(ReefSide side, YOffset yOffset) {
    PathConstraints constraints =
        new PathConstraints(1, 1, Units.degreesToRadians(700), Units.degreesToRadians(1000));
    // it's safe to get the alliance here, because we're calling this every
    // time a button is pressed
    Alliance alliance = DriverStation.getAlliance().get();
    Pose2d desiredPose = getLineupTagPose(alliance, side);

    // should never happen, but just in case we don't find a pose for a reef side
    if (desiredPose == null) {
      System.err.println(
          "***************** ERROR: No pose found for " + side + "*****************");
      return Commands.none();
    }
    // figure out our desired final lineup spot by transforming out from the tag, and
    // rotating 180 (we want to face the reef)
    // left and right poles of the reef are from the perspective of looking from the outside of the
    // reef
    if (yOffset == YOffset.Left) {
      // Center to pole offset is negative because from april tag perspective, the left pole is in
      // the negative y direction
      desiredPose =
          desiredPose.transformBy(
              new Transform2d(
                  Constants.CENTER_TO_BUMPER_OFFSET,
                  Constants.CENTER_TO_POLE_OFFSET.times(-1),
                  new Rotation2d(Degrees.of(180))));
    } else if (yOffset == YOffset.Right) {
      desiredPose =
          desiredPose.transformBy(
              new Transform2d(
                  Constants.CENTER_TO_BUMPER_OFFSET,
                  Constants.CENTER_TO_POLE_OFFSET,
                  new Rotation2d(Degrees.of(180))));
    } else if (yOffset == YOffset.Center) {
      desiredPose =
          desiredPose.transformBy(
              new Transform2d(
                  Constants.CENTER_TO_BUMPER_OFFSET,
                  Centimeters.of(0),
                  new Rotation2d(Degrees.of(180))));
    }

    return AutoBuilder.pathfindToPose(desiredPose, constraints);
  }
}

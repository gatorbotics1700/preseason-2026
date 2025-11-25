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

  //looking at this piece of code could possibly come in handy
  public static enum YOffset {
    Left,
    Right,
    Center
  }

  //TODO: write a method called getLineupTagPose that returns a Pose2d of position of the april tag we are lining up to, depending on the alliance and the side of the reef(Q1-Q6)
  //you can get the pose of an april tag with a specific id by saying VisionConstants.APRIL_TAG_LAYOUT.getTagPose(INSERT ID NUMBER HERE).get().toPose2d()

  
  //returns a command that drives the robot to a position on the field, based off the side of the reef we want to line up to and the current alliance
  //TODO: add a parameter to Lineup() that helps the method determine which side of the reef you are trying to line up to
  public static Command Lineup(YOffset yOffset) {
    PathConstraints constraints =
        new PathConstraints(1, 1, Units.degreesToRadians(700), Units.degreesToRadians(1000));
    // it's safe to get the alliance here, because we're calling this every
    // time a button is pressed
    Alliance alliance = DriverStation.getAlliance().get();
    //TODO: create a variable called desiredPose that gets the pose of the april tag we want to be lining up to

    // should never happen, but just in case we don't find a pose for a reef side
    if (desiredPose == null) {
      System.out.println("No pose found");
      return Commands.none();
    }
    // figure out our desired final lineup spot by transforming out from the tag, and
    // rotating 180 (we want to face the reef)
    //left and right poles of the reef are from the perspective of looking from the outside of the reef
    if (yOffset == YOffset.Left) {
      // Center to pole offset is negative because from april tag perspective, the left pole is in
      // the negative y direction
      desiredPose =
          desiredPose.transformBy(
              new Transform2d(
                  Centimeters.of(0),
                  Constants.CENTER_TO_POLE_OFFSET.times(-1),
                  new Rotation2d(Degrees.of(180))));
    } else if (yOffset == YOffset.Right) {
      desiredPose =
          desiredPose.transformBy(
              new Transform2d(
                  Centimeters.of(0),
                  Constants.CENTER_TO_POLE_OFFSET,
                  new Rotation2d(Degrees.of(180))));
    } else if (yOffset == YOffset.Center) {
      desiredPose =
          desiredPose.transformBy(
              new Transform2d(
                  Centimeters.of(0),
                  Centimeters.of(0),
                  new Rotation2d(Degrees.of(180))));
    }
    
    //TODO: transform the x component of desiredPose by the distance between the center of the robot and the edge of its bumpers
    //look at the block of code above to see how to perform transformations

    //AutoBuilder is automatically making a command that drives to the pose that you give it
    return AutoBuilder.pathfindToPose(desiredPose, constraints);
  }
}

// package frc.robot.subsystems;

// import static org.junit.jupiter.api.Assertions.*;
// import org.junit.jupiter.api.Test;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Pose3d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Rotation3d;
// import edu.wpi.first.math.geometry.Translation2d;
// import frc.robot.subsystems.TurretSubsystem;
// import java.util.function.Supplier;

// public class TurretTest{
//     private final Translation2d TARGET = new Translation2d(1, 1);
//     private TurretSubsystem turret; // Declare as class field
    
//     @Test
//     public void testConvertToRobotSpace1() {
//         Supplier<Pose2d> currentPose= () -> {
//             return new Pose2d(0, 0, new Rotation2d(0));
//         };
//         turret = new TurretSubsystem(currentPose); // Initialize the field

//         double result = turret.getTargetTurretAngle(TARGET);
//         System.out.println("RESULT: " + result);
//         assertAll("Robot at origin, facing positive X",
//             () -> assertEquals(0.785, result, 0.1, "X coordinate should match")
//         );
//     }

//     @Test
//     public void testConvertToRobotSpace2() {
//         Supplier<Pose2d> currentPose= () -> {
//             return new Pose2d(0, 1, new Rotation2d(0));
//         };
//         turret = new TurretSubsystem(currentPose); // Initialize the field

//         double result = turret.getTargetTurretAngle(TARGET);
//         System.out.println("RESULT: " + result);
//         assertAll("Robot at origin, facing positive X",
//             () -> assertEquals(0, result, 0.1, "X coordinate should match")
//         );
//     }

//     @Test
//     public void testConvertToRobotSpace3() {
//         Supplier<Pose2d> currentPose= () -> {
//             return new Pose2d(0, 2, new Rotation2d(0));
//         };
//         turret = new TurretSubsystem(currentPose); // Initialize the field

//         double result = turret.getTargetTurretAngle(TARGET);
//         System.out.println("RESULT: " + result);
//         assertAll("Robot at origin, facing positive X",
//             () -> assertEquals(-0.785, result, 0.1, "X coordinate should match")
//         );
//     }

    
// }
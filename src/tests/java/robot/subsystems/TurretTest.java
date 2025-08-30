package frc.robot.subsystems;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.TurretSubsystem;

public class TurretTest extends junit {
    private final Translation2d TARGET = new Translation2d(1, 1);
    @Test
    public void testConvertToRobotSpace1() {
        Supplier<Pose2d> currentPose= () -> {
            return new Pose2d(0, 0, new Rotation2d(0));
        };
        private final TurretSubsystem turret = new TurretSubsystem(currentPose);

        final double result = turret.getTargetTurretAngle(TARGET);
        
        assertAll("Robot at origin, facing positive X",
            () -> assertEquals(12, result, 0.1, "X coordinate should match")
        );
    }
}
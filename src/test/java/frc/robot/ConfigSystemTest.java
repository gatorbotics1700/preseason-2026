package frc.robot;

import static org.junit.jupiter.api.Assertions.*;

import frc.robot.subsystems.vision.VisionConstants;
import frc.robot.util.RobotConfigLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test class to verify the configuration system works correctly. This test verifies that the
 * configuration values are loaded properly from the properties file.
 */
public class ConfigSystemTest {

  private static final String TEST_SERIAL = "03223852";
  private static final String TEST_CONFIG_FILE = "config_sting.properties";

  @BeforeAll
  public static void configureRobotConfigLoader() {
    RobotConfigLoader.setSerialNumberOverride(TEST_SERIAL);
    RobotConfigLoader.setConfigFileOverride(TEST_CONFIG_FILE);
    RobotConfigLoader.clearCache();
  }

  @Test
  public void testConstantsLoaded() {
    // Test that the robot serial number is loaded
    assertNotNull(Constants.ROBOT_SERIAL_NUMBER, "Robot serial number should be loaded");

    // Test that limelight constants are loaded
    assertNotNull(VisionConstants.LIMELIGHT_0_NAME, "Limelight camera 0 name should be loaded");

    // Test that tuner constants are loaded
    assertTrue(RobotConfigLoader.getDouble("tuner.steer_kp") > 0, "Steer KP should be positive");
    assertTrue(
        RobotConfigLoader.getDouble("tuner.drive_kp") >= 0, "Drive KP should be non-negative");
    assertTrue(RobotConfigLoader.getInt("tuner.pigeon_id") > 0, "Pigeon ID should be positive");

    // Test that motor IDs are loaded
    assertTrue(
        RobotConfigLoader.getInt("tuner.front_left_drive_motor_id") > 0,
        "Front left drive motor ID should be positive");
    assertTrue(
        RobotConfigLoader.getInt("tuner.front_left_steer_motor_id") > 0,
        "Front left steer motor ID should be positive");
    assertTrue(
        RobotConfigLoader.getInt("tuner.front_left_encoder_id") > 0,
        "Front left encoder ID should be positive");

    // Test that module positions are loaded
    assertNotEquals(
        0.0,
        RobotConfigLoader.getDouble("tuner.front_left_pos.x_inches"),
        "Front left X position should not be zero");
    assertNotEquals(
        0.0,
        RobotConfigLoader.getDouble("tuner.front_left_pos.y_inches"),
        "Front left Y position should not be zero");
  }

  @Test
  public void testVisionConstants() {
    // Test that vision constants have reasonable values
    assertTrue(
        VisionConstants.MAX_AMBIGUITY > 0 && VisionConstants.MAX_AMBIGUITY < 1,
        "Max ambiguity should be between 0 and 1");
    assertTrue(VisionConstants.MAX_Z_ERROR > 0, "Max Z error should be positive");
    assertTrue(
        VisionConstants.LINEAR_STD_DEV_BASELINE > 0, "Linear std dev baseline should be positive");
    assertTrue(
        VisionConstants.ANGULAR_STD_DEV_BASELINE > 0,
        "Angular std dev baseline should be positive");
  }

  @Test
  public void testTunerConstants() {
    // Test that tuner constants have reasonable values
    assertTrue(
        RobotConfigLoader.getDouble("tuner.slip_current_amps") > 0,
        "Slip current should be positive");
    assertTrue(
        RobotConfigLoader.getDouble("tuner.stator_current_limit_amps") > 0,
        "Stator current limit should be positive");
    assertTrue(
        RobotConfigLoader.getDouble("tuner.speed_at_12_volts_meters_per_sec") > 0,
        "Speed at 12 volts should be positive");
    assertTrue(
        RobotConfigLoader.getDouble("tuner.drive_gear_ratio") > 0,
        "Drive gear ratio should be positive");
    assertTrue(
        RobotConfigLoader.getDouble("tuner.steer_gear_ratio") > 0,
        "Steer gear ratio should be positive");
    assertTrue(
        RobotConfigLoader.getDouble("tuner.wheel_radius") > 0, "Wheel radius should be positive");
  }
}

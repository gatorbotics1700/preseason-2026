package frc.robot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class to verify the configuration system works correctly. This test verifies that the
 * Constants class loads configuration values properly.
 */
public class ConfigSystemTest {

  @Test
  public void testConstantsLoaded() {
    // Test that the robot serial number is loaded
    assertNotNull(Constants.ROBOT_SERIAL_NUMBER, "Robot serial number should be loaded");

    // Test that limelight constants are loaded
    assertNotNull(Constants.LIMELIGHT_CAMERA_0_NAME, "Limelight camera 0 name should be loaded");
    assertNotNull(Constants.LIMELIGHT_CAMERA_1_NAME, "Limelight camera 1 name should be loaded");

    // Test that tuner constants are loaded
    assertTrue(Constants.TUNER_STEER_KP > 0, "Steer KP should be positive");
    assertTrue(Constants.TUNER_DRIVE_KP >= 0, "Drive KP should be non-negative");
    assertTrue(Constants.TUNER_PIGEON_ID > 0, "Pigeon ID should be positive");

    // Test that motor IDs are loaded
    assertTrue(
        Constants.TUNER_FRONT_LEFT_DRIVE_MOTOR_ID > 0,
        "Front left drive motor ID should be positive");
    assertTrue(
        Constants.TUNER_FRONT_LEFT_STEER_MOTOR_ID > 0,
        "Front left steer motor ID should be positive");
    assertTrue(
        Constants.TUNER_FRONT_LEFT_ENCODER_ID > 0, "Front left encoder ID should be positive");

    // Test that module positions are loaded
    assertNotEquals(
        0.0, Constants.TUNER_FRONT_LEFT_X_POS, "Front left X position should not be zero");
    assertNotEquals(
        0.0, Constants.TUNER_FRONT_LEFT_Y_POS, "Front left Y position should not be zero");
  }

  @Test
  public void testVisionConstants() {
    // Test that vision constants have reasonable values
    assertTrue(
        Constants.LIMELIGHT_MAX_AMBIGUITY > 0 && Constants.LIMELIGHT_MAX_AMBIGUITY < 1,
        "Max ambiguity should be between 0 and 1");
    assertTrue(Constants.LIMELIGHT_MAX_Z_ERROR > 0, "Max Z error should be positive");
    assertTrue(
        Constants.LIMELIGHT_LINEAR_STD_DEV_BASELINE > 0,
        "Linear std dev baseline should be positive");
    assertTrue(
        Constants.LIMELIGHT_ANGULAR_STD_DEV_BASELINE > 0,
        "Angular std dev baseline should be positive");
  }

  @Test
  public void testTunerConstants() {
    // Test that tuner constants have reasonable values
    assertTrue(Constants.TUNER_SLIP_CURRENT > 0, "Slip current should be positive");
    assertTrue(Constants.TUNER_STATOR_CURRENT_LIMIT > 0, "Stator current limit should be positive");
    assertTrue(Constants.TUNER_SPEED_AT_12_VOLTS > 0, "Speed at 12 volts should be positive");
    assertTrue(Constants.TUNER_DRIVE_GEAR_RATIO > 0, "Drive gear ratio should be positive");
    assertTrue(Constants.TUNER_STEER_GEAR_RATIO > 0, "Steer gear ratio should be positive");
    assertTrue(Constants.TUNER_WHEEL_RADIUS > 0, "Wheel radius should be positive");
  }
}


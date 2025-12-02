# Robot Configuration System

This robot uses a configuration system that automatically loads robot-specific constants based on the roboRIO serial number. This allows the same codebase to work with different robot configurations without code changes.

## How It Works

1. **Serial Number Detection**: The system uses `RobotController.getSerialNumber()` to detect which roboRIO is running the code.

2. **Configuration Files**: Two configuration files are stored in the `src/main/deploy/` directory:
   - `config_robot1.properties` - Configuration for roboRIO serial number `032398EA`
   - `config_robot2.properties` - Configuration for roboRIO serial number `032398EB`

3. **Automatic Loading**: The `Constants.java` class automatically loads the appropriate configuration when the robot starts.

## Configuration Files

Each configuration file contains the same variables but with different values for each robot:

### Limelight Configuration
- Camera names and transforms
- Vision filtering thresholds
- Standard deviation parameters

### Tuner Constants Configuration
- PID gains for steer and drive motors
- Motor parameters (slip current, gear ratios, etc.)
- Encoder offsets for each swerve module
- Motor IDs for each module
- Module positions

## Adding a New Robot

To add support for a new robot:

1. **Add the serial number** to `RobotConfigLoader.java`:
   ```java
   case "NEW_SERIAL_NUMBER":
       configFileName = "config_robot3.properties";
       break;
   ```

2. **Create a new configuration file** `config_robot3.properties` in `src/main/deploy/` with the robot-specific values.

3. **Deploy the configuration file** to the roboRIO along with the robot code.

## Using the Configuration

The configuration values are available as static constants in the `Constants` class:

```java
// Vision constants
String cameraName = Constants.LIMELIGHT_CAMERA_0_NAME;
double cameraX = Constants.LIMELIGHT_ROBOT_TO_CAMERA0_X;

// Tuner constants
double steerKp = Constants.TUNER_STEER_KP;
int pigeonId = Constants.TUNER_PIGEON_ID;
```

## Current Robot Configurations

### Robot 1 (Serial: 032398EA)
- Uses "dory's sketchy limelight offsets"
- Drive KP = 0.8
- Encoder offsets: Front Left: -4.87, Front Right: -12.808, Back Left: -8.863, Back Right: -10.491

### Robot 2 (Serial: 032398EB)
- Uses "manta's limelight offsets"
- Drive KP = 0.1
- Encoder offsets: Front Left: -1.502, Front Right: 2.370, Back Left: -1.025, Back Right: -0.858

## Troubleshooting

If the robot fails to start with a configuration error:

1. **Check the serial number**: Verify the roboRIO serial number is correctly mapped in `RobotConfigLoader.java`
2. **Check the configuration file**: Ensure the configuration file exists and is properly formatted
3. **Check file deployment**: Make sure the configuration files are deployed to the roboRIO

The system will throw a runtime exception if:
- The serial number is not recognized
- The configuration file cannot be loaded
- Required configuration values are missing

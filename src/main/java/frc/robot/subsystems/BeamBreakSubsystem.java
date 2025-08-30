package frc.robot.subsystems;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class BeamBreakSubsystem extends SubsystemBase {

    private final int receiverPort = 3;
    private final int transmitterPort = 2;
    DigitalInput receiver = new DigitalInput(receiverPort);
    DigitalOutput transmitter = new DigitalOutput(transmitterPort);
    private boolean lastState;

    public BeamBreakSubsystem(){
        
    }
    
    @Override
    public void periodic(){
        transmitter.set(true);
        if(receiver.get()){
            System.out.println("OPEN");
        } else {
            System.out.println("CLOSED");
        }
    }
}

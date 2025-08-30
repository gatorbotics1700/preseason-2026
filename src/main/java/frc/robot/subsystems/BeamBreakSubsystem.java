package frc.robot.subsystems;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class BeamBreakSubsystem extends SubsystemBase {

    private final int receiverPort = 1;
    private final int transmitterPort = 0;
    DigitalInput receiver = new DigitalInput(receiverPort);
    DigitalOutput transmitter = new DigitalOutput(transmitterPort);
    


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

package frc.robot.subsystems;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class BeamBreakSubsystem extends SubsystemBase {

    private final int receiverPort = 0;
    private final int transmitterPort = 1;
    DigitalInput receiver = new DigitalInput(receiverPort);
    DigitalInput transmitter = new DigitalInput(transmitterPort);


    public BeamBreakSubsystem(){
    }
    
    @Override
    public void periodic(){
        if(receiver.get()){
            System.out.println("OPEN");
        } else {
            System.out.println("CLOSED");
        }
    }
}

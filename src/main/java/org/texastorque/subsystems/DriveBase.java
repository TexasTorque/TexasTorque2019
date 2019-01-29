package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class DriveBase extends Subsystem {

    private static volatile DriveBase instance;
    private RobotState currentState;

    private TorqueMotor leftFore;
    private TorqueMotor leftMid;
	private TorqueMotor leftRear;
    private TorqueMotor rightFore;
    private TorqueMotor rightMid;
    private TorqueMotor rightRear;
    private DoubleSolenoid gearShift;
    
    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private boolean highGear = false;
    
    private static boolean clockwise = true;

    private DriveBase() {
        leftFore = new TorqueMotor(new VictorSP(Ports.DB_LEFT_FORE_MOTOR), !clockwise);
        leftMid = new TorqueMotor(new VictorSP(Ports.DB_LEFT_MID_MOTOR), !clockwise);
        leftRear = new TorqueMotor(new VictorSP(Ports.DB_LEFT_REAR_MOTOR), !clockwise);
        
        rightFore = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_FORE_MOTOR), clockwise);
        leftMid = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_MID_MOTOR), clockwise);
        rightRear = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_REAR_MOTOR), clockwise);
        
        gearShift = new DoubleSolenoid(2, Ports.DB_SOLE_A, Ports.DB_SOLE_B);
    }

    @Override
    public void autoInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void teleopInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void disabledInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void disabledContinuous() {
        output();
    }

    @Override
    public void autoContinuous() {
        // Do something
        output();
    }

    @Override
    public void teleopContinuous() {
        currentState = state.getRobotState();

        if (currentState == RobotState.TELEOP) {
            leftSpeed = input.getDBLeftSpeed();
            rightSpeed = input.getDBRightSpeed();
        }
        else if (currentState == RobotState.VISION) {
            // Read feedback for NetworkTables input, calculate output
        }
        else if (currentState == RobotState.LINE) {
            // Read feedback for NetworkTables input, calculate output
        }

        output();
    }

    @Override
    protected void output() {
        setGears();
        if (highGear)
            gearShift.set(Value.kForward);
        else
            gearShift.set(Value.kReverse);
        
        leftFore.set(leftSpeed);
        leftMid.set(leftSpeed);
        leftRear.set(leftSpeed);
        
		rightFore.set(rightSpeed);
		rightMid.set(rightSpeed);
        rightRear.set(rightSpeed);
    }

    public boolean getHighGear() {
        return highGear;
    }

    /**
     * Potential auto transmission
     */
    private void setGears() {
        highGear = input.getDBHighGear();
    }

    @Override
    public void smartDashboard() {}

    public static DriveBase getInstance() {
        if (instance == null) {
            synchronized (DriveBase.class) {
                if (instance == null)
                    instance = new DriveBase();
            }
        }
        return instance;
    }
}
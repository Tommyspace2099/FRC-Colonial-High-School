package org.usfirst.frc.team945.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;


public class XboxController extends GenericHID {
    
    private DriverStation m_ds;
    private final int m_port;
    
    /**
     * Represents an analog axis on a joystick.
     */
    public static class AxisType {
        
        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int kLeftX_val = 0;
        private static final int kLeftY_val = 1;
        private static final int kLeftTrigger_val = 2;
        private static final int kRightTrigger_val = 3;
        private static final int kRightX_val = 4;
        private static final int kRightY_val = 5;
        
        private AxisType(int value) {
            this.value = value;
        }
        
        /**
         * Axis: Left X
         */
        public static final AxisType kLeftX = new AxisType(kLeftX_val);
        
        /**
         * Axis: Left Y
         */
        public static final AxisType kLeftY = new AxisType(kLeftY_val);

        /**
         * Axis: Triggers
         */
        public static final AxisType kLeftTrigger = new AxisType(kLeftTrigger_val);
        /**
         * Axis: Triggers
         */
        public static final AxisType kRightTrigger = new AxisType(kRightTrigger_val);
        
        /**
         * Axis: Right X
         */
        public static final AxisType kRightX = new AxisType(kRightX_val);
        
        /**
         * Axis: Right Y
         */
        public static final AxisType kRightY = new AxisType(kRightY_val);
        
    }
    
    /**
     * Represents a digital button on a joystick.
     */
    public static class ButtonType {
        
        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int kA_val = 1;
        private static final int kB_val = 2;
        private static final int kX_val = 3;
        private static final int kY_val = 4;
        private static final int kL_val = 5;
        private static final int kR_val = 6;
        private static final int kBack_val = 7;
        private static final int kStart_val = 8;
        private static final int kLeftStick_val = 9;
        private static final int kRightStick_val = 10;
        
        private ButtonType(int value) {
            this.value = value;
        }
        
        /**
         * Button: X-Joystick
         */
        public static final ButtonType kLeftStick = new ButtonType(kLeftStick_val);
        
        /**
         * Button: Y-Joystick
         */
        public static final ButtonType kRightStick = new ButtonType(kRightStick_val);
        
        /**
         * Button: X
         */
        public static final ButtonType kX = new ButtonType(kX_val);
        
        /**
         * Button: Y
         */
        public static final ButtonType kY = new ButtonType(kY_val);
        
        /**
         * Button: A
         */
        public static final ButtonType kA = new ButtonType(kA_val);
        
        /**
         * Button: B
         */
        public static final ButtonType kB = new ButtonType(kB_val);
        
        /**
         * Button: R1
         */
        public static final ButtonType kR = new ButtonType(kR_val);
        
        /**
         * Button: L1
         */
        public static final ButtonType kL = new ButtonType(kL_val);
        
        /**
         * Button: Select
         */
        public static final ButtonType kStart = new ButtonType(kStart_val);
        
        /**
         * Button: Start
         */
        public static final ButtonType kBack = new ButtonType(kBack_val);
    }
    
    
    /**
     * Constructor
     * @param port USB Port on DriverStation
     */
    public XboxController(int port) {
        super(port);
        m_port = port;
        m_ds = DriverStation.getInstance();
    }
    
    /**
     * Get Value from an Axis
     * @param axis Axis Number
     * @return Value from Axis (-1 to 1)
     */
    public double getRawAxis(int axis) {
        return m_ds.getStickAxis(m_port, axis);
    }
    
    /**
     * Get Value from an Axis
     * @param axis AxisType
     * @return 
     */
    public double getAxis(AxisType axis) {
        return getRawAxis(axis.value);
    }
    
	@Override
	public double getX(Hand hand) {
		return getX(hand,0.0);
	}

	@Override
	public double getY(Hand hand) {
		return getY(hand,0.0);
	}
    
    /**
     * Retrieve value for X axis
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getX(Hand hand, double threshold) {
        if(hand.value == Hand.kRight.value) {
            return Math.abs(getAxis(AxisType.kRightX))>threshold?getAxis(AxisType.kRightX):0.0;
        } else if(hand.value == Hand.kLeft.value) {
            return Math.abs(getAxis(AxisType.kLeftX))>threshold?getAxis(AxisType.kLeftX):0.0;
        } else {
            return 0;
        }
    }
    
    /**
     * Retrieve value for Y axis
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getY(Hand hand, double threshold) {
        if(hand.value == Hand.kRight.value) {
            return Math.abs(getAxis(AxisType.kRightY))>threshold?getAxis(AxisType.kRightY):0.0;
        } else if(hand.value == Hand.kLeft.value) {
            return Math.abs(getAxis(AxisType.kLeftY))>threshold?getAxis(AxisType.kLeftY):0.0;
        } else {
            return 0;
        }
    }
    
    public double getThrottle(Hand hand, double threshold){
    	if(hand.value == Hand.kRight.value){
    		return Math.abs(getAxis(AxisType.kRightTrigger))>threshold?getAxis(AxisType.kRightTrigger):0.0;
    	} else if(hand.value == Hand.kLeft.value){
    		return Math.abs(getAxis(AxisType.kLeftTrigger))>threshold?getAxis(AxisType.kLeftTrigger):0.0;
    	} else {
    		return 0;
    	}
    }
    
    /**
     * Unused
     * @param hand Unused
     * @return 0
     */
    public double getZ(Hand hand) {
        return 0;
    }
    
    /**
     * Get Value from a button
     * @param button Button Type
     * @return 
     */
    public boolean getButton(ButtonType button) {
        return getRawButton(button.value);
    }
    
    /**
     * Get Button from Joystick
     * @param hand hand associated with the button
     * @return Button Status (true or false)
     */
    public boolean getJoystickClicked(Hand hand) {
        if(hand == Hand.kRight) {
            return getButton(ButtonType.kRightStick);
        } else if(hand == Hand.kLeft) {
            return getButton(ButtonType.kLeftStick);
        } else {
            return false;
        }
    }
    
    /**
     * Get Value from Back buttons
     * @param hand hand associated with the button
     * @return state of left or right 
     */
    public boolean getBumper(Hand hand) {
        if(hand == Hand.kRight) {
            return getButton(ButtonType.kR);
        } else if(hand == Hand.kLeft) {
            return getButton(ButtonType.kL);
        } else {
            return false;
        }
    }
    
    /**
     * Get State of Select Button
     * @return State of button
     */
    public boolean getStart() {
        return getButton(ButtonType.kStart);
    }
    
    /**
     * Get State of Back Button
     * @return State of button
     */
    public boolean getBack() {
        return getButton(ButtonType.kBack);
    }
    
    /**
     * Get State of A Button
     * @param ka 
     * @return State of button
     */
    public boolean getAButton() {
        return getButton(ButtonType.kA);
    }
    
    /**
     * Get State of B Button
     * @return State of button
     */
    public boolean getBButton() {
        return getButton(ButtonType.kB);
    }
    
    /**
     * Get State of X Button
     * @return State of button
     */
    public boolean getXButton() {
        return getButton(ButtonType.kX);
    }
    
    /**
     * Get State of Y Button
     * @return State of button
     */
    public boolean getYButton() {
        return getButton(ButtonType.kY);
    }
}

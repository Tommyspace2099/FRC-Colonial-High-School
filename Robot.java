package org.usfirst.frc.team945.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
@SuppressWarnings("deprecation")
public class Robot extends SampleRobot {
	
	//The code below is something I found for the xbox 360 controller not the xbox one 

	/**
	 * The Xbox One Controller that drives the robot on port 4
	 */
	XboxController mXboxDriver = new XboxController(1);
	/**
	 * The Xbox 360 controller used to operate robot functions
	 */
	XboxController mXboxOperator = new XboxController(0); 
	//Joystick drivestick = new Joystick(1); //Where "1" is the index of the joystick (you can set this in the Driver Station software).
	/**
	 * The front left speed controller on port 3.
	 */
	SpeedController rearRightMotor = new Spark(2);
	/**
	 * The rear left speed controller on port 2.
	 */
	SpeedController frontRightMotor = new Spark(1);
	/**
	 * The front right speed controller on port 0.
	 */
	SpeedController rearLeftMotor = new Spark(3);
	/**
	 * The rear right speed controller on port 1.
	 */
	SpeedController frontLeftMotor = new Spark(0);
	/**
	 * This is going be the robot lifter below
	 */
	SpeedController Robotlifter = new Victor (4);
	/**
	 * The limit switch for the Robolifter
	 */
	DigitalInput LifterToHigh = new DigitalInput(6);
	/**
	 * The limit switch for the Robolifter
	 */
	DigitalInput LifterToLow = new DigitalInput(7);
	/**
	 * This is going be the claw motor that picks up milk crates
	 */
	SpeedController ClawMotor = new Victor(5);
	/**
	 * The limit switch for the ClawMotor
	 */
	DigitalInput ClawToHigh = new DigitalInput(9);
	/**
	 * The limit switch for the ClawMotor
	 */
	DigitalInput ClawToLow = new DigitalInput(8);
	/**
	 * The Claw solenoid that opens or closes the claw
	 */
	DoubleSolenoid ClawSolenoid = new DoubleSolenoid(0,1);
	/**
	 * TODO: update port number 
	 */
	DoubleSolenoid ClawPushSolenoid = new DoubleSolenoid(2,3);
	/**
	 * TODO: update port number 
	 */
	Servo Liftratchet = new Servo(9);
	/**
	 * The threshold to read from the xbox controller before moving anything
	 */
	private double driveThreshold = 0.26;
	/**
	 * The default drive threshold to use when no value is found
	 */
	private double defaultDriveThreshold = 0.26;	
	/**
	 * The threshold to use when reading trigger pressed.
	 */
	private double triggerThreshold = 0.0;
	/**
	 * Releases the gear from the window
	 */
	//DoubleSolenoid gearRelease = new DoubleSolenoid(0, 1);
	/**
	 * Lifts the basket to release the balls
	 */
	//DoubleSolenoid liftBasket = new DoubleSolenoid(2, 3);
	/**
	 * The robot drive. This class is used to control the
	 * driving of the robot. It needs the speed controllers
	 * that are used to control each individual motor.
	 */
	RobotDrive myRobot =
			new RobotDrive(
					frontLeftMotor, rearLeftMotor,
					frontRightMotor, rearRightMotor);
	
	/**
	 * The joystick that controlls driving on USB port 0.
	 */
	Joystick driveStick = new Joystick(0);
	/**
	 * The value multiplied to the Joystick values. Less than 1
	 * means that it will make the robot slower and more than 1 makes
	 * the robot faster.
	 */
	double speedFactor = 1.0;
	double defaultSpeedFactor = 1.0;
	
	int xboxControllerPort = 1;
	int defaultXboxControllerPort = 1;
	double defaultLifterSpeed = 0.5;
	double defaultClawSpeed = 1.0;
	double defaultClawDownSpeed = -0.5;
	double maxRatchetAngle = 180.0;
	double minRatchetAngle = 0.0;

	/**
	 * These are the autonomous mode defaults.
	 * Change these after a test run on the carpet.
	 */
	double autoSpeed = 0.8;// speed robot drives
	double switchTime = 1.6;// how long to the switch
	double scaleTime = 2.5;// how long to the scale
	double turnTime = 0.5;// how long to turn 90 degrees
	double timeAfterTurn = 0.5;// how long to drive to pull right up to the switch or the scale after turning
	double centerTime = 1.2;// how long to drive left or right when in the center
	double centerTimeAfterTurn = 1.0;// how long to drive to the front of the switch when in the center
	double centerTimeAfterDropOff = 1.0;// how long to drive around the switch, first left or right, then how long to cross the line
	boolean crossLineAfterDropOff = false;// do we cross the line or not when starting in the center
	double robotLifterTime = 3.6;// time to lift the robot lifter to drop off at the scale
	double clawLifterTime = 0.0;// time to lift the claw lifter to drop off at the scale
	
	private enum ColorPosition{
		LEFT, RIGHT, UNSET;
	}
	
	private ColorPosition cpSwitch = ColorPosition.UNSET;
	private ColorPosition cpScale = ColorPosition.UNSET;
	private SendableChooser<Integer> startChooser = new SendableChooser<>();
	
	
	//ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	
	public Robot() {
	}
	//Image img = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	UsbCamera cam = CameraServer.getInstance().startAutomaticCapture(0);
	UsbCamera cam2 = CameraServer.getInstance().startAutomaticCapture(1);

	/**
	 * @author James
	 * Finds the scale and switch color positions from the Driver Station game specific message.
	 */
	private void getSwitchScaleColors() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		System.out.println("Game Data: " + gameData);
		if (gameData.length() > 2) {
			if (gameData.charAt(1) == 'L') {
				cpScale = ColorPosition.LEFT;
				System.out.println("Scale: Left");
			} else {
				cpScale = ColorPosition.RIGHT;
				System.out.println("Scale: Right");
			}
			
			if (gameData.charAt(0) == 'L') {
				cpSwitch = ColorPosition.LEFT;
				System.out.println("Switch: Left");
			} else {
				cpSwitch = ColorPosition.RIGHT;
				System.out.println("Switch: Right");
			}
			
		}
	}

	/**
	 * This will be called once and is used to initialize any robot data.
	 */
	@Override
	public void robotInit() {
		// This expiration is used to turn off the motor if it isn't set
		// after the expiration time. The time is set to 0.1 seconds, after which
		// the motor will stop moving if no inputs were sent to it.
		myRobot.setExpiration(0.1);
		rearLeftMotor.setInverted(true);
		frontLeftMotor.setInverted(true);
		//gyro.calibrate();
		printStatus();
		SmartDashboard.putNumber("Auto Speed", autoSpeed);
		SmartDashboard.putNumber("Switch Time", switchTime);
		SmartDashboard.putNumber("Scale Time", scaleTime);
		SmartDashboard.putNumber("Turn Time", turnTime);
		SmartDashboard.putNumber("Time After Turn", timeAfterTurn);
		SmartDashboard.putNumber("Center Time", centerTime);
		SmartDashboard.putNumber("Center Time After Turn", centerTimeAfterTurn);
		SmartDashboard.putNumber("Center Time After Drop Off", centerTimeAfterDropOff);
		SmartDashboard.putBoolean("Cross Line After Drop Off", crossLineAfterDropOff);
		SmartDashboard.putNumber("Robot Lifter Time", robotLifterTime);
		SmartDashboard.putNumber("Claw Lifter Time", clawLifterTime);
		
		SmartDashboard.putNumber("Speed Factor", speedFactor);
		SmartDashboard.putNumber("Drive Threshold", driveThreshold);
		SmartDashboard.putNumber("Trigger Threshold", triggerThreshold);
		SmartDashboard.putNumber("Xbox Controller Port", xboxControllerPort);
		SmartDashboard.putBoolean("Override Warnings",true);
		SmartDashboard.putNumber("Lifter Speed", defaultLifterSpeed);
		SmartDashboard.putNumber("Claw Up Speed", defaultClawSpeed);
		SmartDashboard.putNumber("Claw Down Speed", defaultClawDownSpeed);
		SmartDashboard.putNumber("Ratchet", Liftratchet.get());
		SmartDashboard.getNumber("Claw Up Speed", defaultClawSpeed);
		SmartDashboard.putNumber("Claw Down Speed", defaultClawDownSpeed);
		SmartDashboard.putNumber("Rachet Max", maxRatchetAngle);
		SmartDashboard.putNumber("Rachet Min", minRatchetAngle);
//		SmartDashboard.putString("Push Out", "Value: " + ClawPushSolenoid.get());
		startChooser.addObject("Center", 2);
		startChooser.addObject("Right", 3);
		startChooser.addDefault("Left", 1);
		SmartDashboard.putData("Start Location", startChooser);
		
		
		
	
		printStatus();
		Timer.delay(1.0);
		(new Thread(){
			public void run(){
				cam.setResolution(649, 480);
				cam.setExposureManual(50);
				cam.setBrightness(50);
				cam.setFPS(50);
				cam.setWhiteBalanceManual(50);
		}}).start();
		
		(new Thread(){
			public void run(){
				cam2.setResolution(649, 480);
				cam2.setExposureManual(50);
				cam2.setBrightness(50);
				cam2.setFPS(50);
				cam2.setWhiteBalanceManual(50);
		}}).start();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * if-else structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomous() {
		//String autoSelected = autoChooser.getSelected();
		//System.out.println("Auto selected: " + autoSelected);
		getSwitchScaleColors();
		runAutonomous(); //(autoSelected);
		turnOffEverything();
	}
	public void turnOffEverything()
	{
		// For safety always turn off everything
		myRobot.setSafetyEnabled(true);
		ClawSolenoid.set(Value.kOff);
		ClawPushSolenoid.set(Value.kOff);
		Robotlifter.set(0.0);
		printStatus();
	}
	public void runAutonomous() //(String mode)
	{ 		
		myRobot.setSafetyEnabled(false);
		ClawPushSolenoid.set(Value.kForward);
		ClawSolenoid.set(Value.kReverse);
		autoSpeed = SmartDashboard.getNumber("Auto Speed", autoSpeed);
		scaleTime = SmartDashboard.getNumber("Scale Time", scaleTime);
		switchTime = SmartDashboard.getNumber("Switch Time", switchTime);
		turnTime = SmartDashboard.getNumber("Turn Time", turnTime);
		timeAfterTurn = SmartDashboard.getNumber("Time After Turn", timeAfterTurn);
		centerTime = SmartDashboard.getNumber("Center Time", centerTime);
		centerTimeAfterTurn = SmartDashboard.getNumber("Center Time After Turn", centerTimeAfterTurn);
		centerTimeAfterDropOff = SmartDashboard.getNumber("Center Time After Drop Off", centerTimeAfterDropOff);
		crossLineAfterDropOff = SmartDashboard.getBoolean("Cross Line After Drop Off", crossLineAfterDropOff);
		
		int retrievestart = startChooser.getSelected();
		// if were on the left and the switch on the left is our color
		if ((cpSwitch == ColorPosition.LEFT)&& (retrievestart ==1)){
			Drive(autoSpeed, switchTime);// Drive to switch
			TurnRight(autoSpeed, turnTime);// turn right since we're on the left
			Drive(autoSpeed, timeAfterTurn);// drive to the switch
			DropOffBox();
		// If were on the right and the switch on the right is our color
		} else if ((cpSwitch == ColorPosition.RIGHT)&& (retrievestart==3)){
			Drive(autoSpeed, switchTime);// Drive to switch
			TurnLeft(autoSpeed, turnTime);// turn left since we're on the right
			Drive(autoSpeed, timeAfterTurn);// drive to the switch
			DropOffBox();
		// If were on the left and the scale on the left is our color
		} else if ((cpScale == ColorPosition.LEFT)&& (retrievestart ==1)){
			Drive(autoSpeed, scaleTime);// Drive to scale
			TurnRight(autoSpeed, turnTime);// turn right since we're on the left
			Telescope();// raise the box to the top
			Drive(autoSpeed * 0.5, timeAfterTurn);// drive to the scale at half speed so we don't tip over
			DropOffBox();
		// if were on the right and the scale on the right is our color
		}else if ((cpScale == ColorPosition.RIGHT)&& (retrievestart==3)){
			Drive(autoSpeed, scaleTime);// Drive to scale
			TurnLeft(autoSpeed, turnTime);// turn Left since we're on the right
			Telescope();// raise the box to the top
			Drive(autoSpeed * 0.5, timeAfterTurn);// drive to the scale at half speed so we don't tip over
			DropOffBox();
		// If were in the center all we can do is the switch from the front
		}else if(retrievestart == 2){
			// were in the center and need to go right
			if(cpSwitch == ColorPosition.RIGHT)
			{
				TurnRight(autoSpeed, turnTime);
				Drive(autoSpeed, centerTime);
				TurnLeft(autoSpeed, turnTime);
				Drive(autoSpeed, centerTimeAfterTurn);
				DropOffBox();
				// if the robot is out of the way then we'll continue on
				if(crossLineAfterDropOff){
					TurnRight(autoSpeed, turnTime);
					Drive(autoSpeed, centerTimeAfterDropOff);
					TurnLeft(autoSpeed, turnTime);
					Drive(autoSpeed, centerTimeAfterDropOff);
				}
			// were in the center and need to go left
			} else if(cpSwitch == ColorPosition.LEFT){
				TurnLeft(autoSpeed, turnTime);
				Drive(autoSpeed, centerTime);
				TurnRight(autoSpeed, turnTime);
				Drive(autoSpeed, centerTimeAfterTurn);
				DropOffBox();
				// if the robot is out of the way then we'll continue on
				if(crossLineAfterDropOff){
					TurnLeft(autoSpeed, turnTime);
					Drive(autoSpeed, centerTimeAfterDropOff);
					TurnRight(autoSpeed, turnTime);
					Drive(autoSpeed, centerTimeAfterDropOff);
				}
			}
		}else {
			// We can't drop anything off just drive forward.
			Drive(autoSpeed /* straight ahead full speed */, scaleTime /* seconds */);
		}
	} 
	
	public void Telescope(){
		Robotlifter.set(SmartDashboard.getNumber("Lifter Speed", defaultLifterSpeed));
		Timer.delay(SmartDashboard.getNumber("Robot Lifter Time", robotLifterTime));
		Robotlifter.set(0.0);
		ClawMotor.set(SmartDashboard.getNumber("Claw Up Speed", defaultClawSpeed));
		Timer.delay(SmartDashboard.getNumber("Claw Lifter Time", clawLifterTime));
		ClawMotor.set(0.0);
	}
	
	public void DropOffBox(){

		ClawSolenoid.set(Value.kOff);
		ClawPushSolenoid.set(Value.kOff);
		Timer.delay(0.1);
		// open claw
		ClawSolenoid.set(Value.kForward);
		// push box out of claw
		ClawPushSolenoid.set(Value.kReverse);
		Timer.delay(0.1);
	}
	
	public void Turn (double speed, double time, boolean isLeft)
	{
		System.out.println("Driving at " + speed + " for " + time + ".");
		// For some reason the speed needs to be negated.
		if(isLeft){
		myRobot.mecanumDrive_Cartesian(0.0, 0.0,speed * -1.0, 0.0);
		}else{
			myRobot.mecanumDrive_Cartesian(0.0, 0.0, speed, 0.0);
		}
		printStatus();
		Timer.delay(time);
		myRobot.stopMotor();
		printStatus();
	}
	
	public void TurnLeft(double speed, double time){
		Turn(speed, time, true);
	}

	public void TurnRight(double speed, double time){
		Turn(speed, time, false);
	}
	
	public void Drive (double speed, double time)
	{
		System.out.println("Driving at " + speed + " for " + time + ".");
		// For some reason the speed needs to be negated.
		myRobot.mecanumDrive_Cartesian(0.0, speed * -1.0, 0.0, 0.0);
		printStatus();
		Timer.delay(time);
		myRobot.stopMotor();
		printStatus();
	}

	
	public void printStatus(){
		SmartDashboard.putNumber("Gyro", 0.0);
		xboxControllerPort = (int)(SmartDashboard.getNumber("Xbox Controller Port",defaultXboxControllerPort));
		XboxController mXboxController = mXboxDriver;
		if(xboxControllerPort == mXboxDriver.getPort()){
			mXboxController = mXboxDriver;
		} else if(xboxControllerPort == mXboxOperator.getPort()){
			mXboxController = mXboxOperator;
		}
		SmartDashboard.putNumber("Left x", mXboxController.getX(Hand.kLeft, driveThreshold));
		SmartDashboard.putNumber("Left y", -1.0 * mXboxController.getY(Hand.kLeft, driveThreshold));
		SmartDashboard.putNumber("Right x", mXboxController.getX(Hand.kRight, driveThreshold));
		SmartDashboard.putNumber("Right y", -1.0 * mXboxController.getY(Hand.kRight, driveThreshold));
		SmartDashboard.putNumber("LT", mXboxController.getThrottle(Hand.kLeft, triggerThreshold));
		SmartDashboard.putNumber("RT", mXboxController.getThrottle(Hand.kRight, triggerThreshold));
		SmartDashboard.putBoolean("A", mXboxController.getAButton());
		SmartDashboard.putBoolean("B", mXboxController.getBButton());
		SmartDashboard.putBoolean("X", mXboxController.getXButton());
		SmartDashboard.putBoolean("Y", mXboxController.getYButton());
		SmartDashboard.putBoolean("Start", mXboxController.getStart());
		SmartDashboard.putBoolean("Back", mXboxController.getBack());
		SmartDashboard.putBoolean("L3", mXboxController.getJoystickClicked(Hand.kLeft));
		SmartDashboard.putBoolean("R3", mXboxController.getJoystickClicked(Hand.kRight));
		SmartDashboard.putBoolean("LB", mXboxController.getBumper(Hand.kLeft));
		SmartDashboard.putBoolean("RB", mXboxController.getBumper(Hand.kRight));
		SmartDashboard.putNumber("POV", mXboxController.getPOV(0));
		SmartDashboard.putNumber("FL", frontLeftMotor.get() * -1.0);
		SmartDashboard.putNumber("FR", frontRightMotor.get());
		SmartDashboard.putNumber("RL", rearLeftMotor.get() * -1.0);
		SmartDashboard.putNumber("RR", rearRightMotor.get());
		SmartDashboard.putNumber("ClawMotor", ClawMotor.get());
		SmartDashboard.putNumber("Robotlifter", Robotlifter.get());
		SmartDashboard.putNumber("Ratchet Angle", Liftratchet.getAngle());
		if(ClawSolenoid.get() == Value.kForward){
			SmartDashboard.putString("Claw", "Open");
		} else if(ClawSolenoid.get() == Value.kReverse){
			SmartDashboard.putString("Claw", "Closed");
		} else {
			SmartDashboard.putString("Claw", "Neutral");
		}
		if(ClawPushSolenoid.get() == Value.kForward){
			SmartDashboard.putString("ClawPush", "Forward");
		} else if(ClawPushSolenoid.get() == Value.kReverse){
			SmartDashboard.putString("ClawPush", "Reverse");
		} else if(ClawPushSolenoid.get() == Value.kOff){
			SmartDashboard.putString("ClawPush", "Off");
		} else {
			SmartDashboard.putString("ClawPush", "Unknown: " + ClawPushSolenoid.get());
		}
		if(ClawToHigh.get())
			SmartDashboard.putString("Claw", "TOO HIGH!");
		else if (ClawToLow.get())
			SmartDashboard.putString("Claw", "TOO LOW!");
		else
			SmartDashboard.putString("Claw", "Okie Dokie");
		if(LifterToHigh.get())
			SmartDashboard.putString("Lifter", "TOO HIGH!");
		else if (LifterToLow.get())
			SmartDashboard.putString("Lifter", "TOO LOW!");
		else
			SmartDashboard.putString("Lifter", "Okie Dokie");
	}
	/**
	 * Runs the motors with tank drive steering.
	 */
	@Override
	public void operatorControl() {
		
		// Enabling safety allows the motor to automatically
		// shutdown after the specified expire time. In our case
		// it's 0.1 seconds.
		myRobot.setSafetyEnabled(true);
		double leftX = 0.0;
		double leftY = 0.0;
		double rightX = 0.0;
		@SuppressWarnings("unused")
		double rightY = 0.0;
		// Only drive while in teleOperated mode
		while (isOperatorControl() && isEnabled()) {
			// There are 2 ways to drive mecanum wheels
			// one is like a translation of the robot without turning (specifying an x and y speed and let it get there without turning.)
			// the other turns like a normal car (goes forward, reverse and turns left spin and right spin)
			
			
			speedFactor = SmartDashboard.getNumber("Speed Factor", defaultSpeedFactor);
			driveThreshold = SmartDashboard.getNumber("Drive Threshold", defaultDriveThreshold);
			
			leftX = mXboxDriver.getX(Hand.kLeft, driveThreshold);
			//Inverting the Y axis
			leftY = mXboxDriver.getY(Hand.kLeft, driveThreshold);
			rightX = mXboxDriver.getX(Hand.kRight, driveThreshold);
			//Inverting the Y axis
			rightY = mXboxDriver.getY(Hand.kRight, driveThreshold);
		
			// drives without turning (crab walk side to side)
			myRobot.mecanumDrive_Cartesian(leftX * speedFactor, leftY * speedFactor, rightX * speedFactor, 0.0);
			
			boolean lifterUp = true;
			boolean lifterDown = true;
			boolean clawUp = true;
			boolean clawDown = true;
			
			if(SmartDashboard.getBoolean("Override Warnings",false)){
				lifterUp = false;
				lifterDown = false;
				clawUp = false;
				clawDown = false;
			} else {
				lifterUp = LifterToHigh.get();
				lifterDown = LifterToLow.get();
				clawUp = ClawToHigh.get();
				clawDown = ClawToLow.get();
			}
			
			if( mXboxOperator.getAButton() && !lifterUp){
				Robotlifter.set(SmartDashboard.getNumber("Lifter Speed", defaultLifterSpeed));
			} else if (mXboxOperator.getBButton() && !lifterDown){
				Robotlifter.set(SmartDashboard.getNumber("Lifter Speed", defaultLifterSpeed)*-1.0);
			} else {
				Robotlifter.set(0);
			}

			double clawUpSpeed = SmartDashboard.getNumber("Claw Up Speed", defaultClawSpeed);
			double clawDownSpeed = SmartDashboard.getNumber("Claw Down Speed", defaultClawDownSpeed);
			
			if( mXboxOperator.getXButton() && !clawUp){
				ClawMotor.set(clawUpSpeed);
			} else if (mXboxOperator.getYButton() && !clawDown){
				ClawMotor.set(clawDownSpeed);
			} else {
				ClawMotor.set(0);
			}
			
			if(mXboxOperator.getStart()){
				ClawSolenoid.set(Value.kForward);
			} else if(mXboxOperator.getBack()){
				ClawSolenoid.set(Value.kReverse);
			} else {
				ClawSolenoid.set(Value.kOff);
			}
			
			if(mXboxOperator.getBumper(Hand.kLeft)){
				ClawPushSolenoid.set(Value.kForward);
			} else if(mXboxOperator.getBumper(Hand.kRight)){
				ClawPushSolenoid.set(Value.kReverse);
			} else {
				ClawPushSolenoid.set(Value.kOff);
			}
			
			double rachetMax = SmartDashboard.getNumber("Rachet Max", maxRatchetAngle);
			double rachetMin = SmartDashboard.getNumber("Rachet Min", minRatchetAngle);
			if(mXboxOperator.getJoystickClicked(Hand.kLeft)){
				Liftratchet.setAngle(rachetMax);
			} else if(mXboxOperator.getJoystickClicked(Hand.kRight))
			{
				Liftratchet.setAngle(rachetMin);
			}
			
			printStatus();
			Timer.delay(0.005); // wait for a motor update time
		}
		turnOffEverything();
	}

	/**
	 * Runs during test mode
	 */
	@Override
	public void test() {
	}

	/**
	 * @return the mXboxDriver
	 */
	public XboxController getmXboxDriver() {
		return mXboxDriver;
	}

	/**
	 * @param mXboxDriver the mXboxDriver to set
	 */
	public void setmXboxDriver(XboxController mXboxDriver) {
		this.mXboxDriver = mXboxDriver;
	}

	/**
	 * @return the mXboxOperator
	 */
	public XboxController getmXboxOperator() {
		return mXboxOperator;
	}

	/**
	 * @param mXboxOperator the mXboxOperator to set
	 */
	public void setmXboxOperator(XboxController mXboxOperator) {
		this.mXboxOperator = mXboxOperator;
	}

	/**
	 * @return the rearRightMotor
	 */
	public SpeedController getRearRightMotor() {
		return rearRightMotor;
	}

	/**
	 * @param rearRightMotor the rearRightMotor to set
	 */
	public void setRearRightMotor(SpeedController rearRightMotor) {
		this.rearRightMotor = rearRightMotor;
	}

	/**
	 * @return the frontRightMotor
	 */
	public SpeedController getFrontRightMotor() {
		return frontRightMotor;
	}

	/**
	 * @param frontRightMotor the frontRightMotor to set
	 */
	public void setFrontRightMotor(SpeedController frontRightMotor) {
		this.frontRightMotor = frontRightMotor;
	}

	/**
	 * @return the rearLeftMotor
	 */
	public SpeedController getRearLeftMotor() {
		return rearLeftMotor;
	}

	/**
	 * @param rearLeftMotor the rearLeftMotor to set
	 */
	public void setRearLeftMotor(SpeedController rearLeftMotor) {
		this.rearLeftMotor = rearLeftMotor;
	}

	/**
	 * @return the frontLeftMotor
	 */
	public SpeedController getFrontLeftMotor() {
		return frontLeftMotor;
	}

	/**
	 * @param frontLeftMotor the frontLeftMotor to set
	 */
	public void setFrontLeftMotor(SpeedController frontLeftMotor) {
		this.frontLeftMotor = frontLeftMotor;
	}

	/**
	 * @return the robotlifter
	 */
	public SpeedController getRobotlifter() {
		return Robotlifter;
	}

	/**
	 * @param robotlifter the robotlifter to set
	 */
	public void setRobotlifter(SpeedController robotlifter) {
		Robotlifter = robotlifter;
	}

	/**
	 * @return the lifterToHigh
	 */
	public DigitalInput getLifterToHigh() {
		return LifterToHigh;
	}

	/**
	 * @param lifterToHigh the lifterToHigh to set
	 */
	public void setLifterToHigh(DigitalInput lifterToHigh) {
		LifterToHigh = lifterToHigh;
	}

	/**
	 * @return the lifterToLow
	 */
	public DigitalInput getLifterToLow() {
		return LifterToLow;
	}

	/**
	 * @param lifterToLow the lifterToLow to set
	 */
	public void setLifterToLow(DigitalInput lifterToLow) {
		LifterToLow = lifterToLow;
	}

	/**
	 * @return the clawMotor
	 */
	public SpeedController getClawMotor() {
		return ClawMotor;
	}

	/**
	 * @param clawMotor the clawMotor to set
	 */
	public void setClawMotor(SpeedController clawMotor) {
		ClawMotor = clawMotor;
	}

	/**
	 * @return the clawToHigh
	 */
	public DigitalInput getClawToHigh() {
		return ClawToHigh;
	}

	/**
	 * @param clawToHigh the clawToHigh to set
	 */
	public void setClawToHigh(DigitalInput clawToHigh) {
		ClawToHigh = clawToHigh;
	}

	/**
	 * @return the clawToLow
	 */
	public DigitalInput getClawToLow() {
		return ClawToLow;
	}

	/**
	 * @param clawToLow the clawToLow to set
	 */
	public void setClawToLow(DigitalInput clawToLow) {
		ClawToLow = clawToLow;
	}

	/**
	 * @return the clawSolenoid
	 */
	public DoubleSolenoid getClawSolenoid() {
		return ClawSolenoid;
	}

	/**
	 * @param clawSolenoid the clawSolenoid to set
	 */
	public void setClawSolenoid(DoubleSolenoid clawSolenoid) {
		ClawSolenoid = clawSolenoid;
	}

	/**
	 * @return the clawPushSolenoid
	 */
	public DoubleSolenoid getClawPushSolenoid() {
		return ClawPushSolenoid;
	}

	/**
	 * @param clawPushSolenoid the clawPushSolenoid to set
	 */
	public void setClawPushSolenoid(DoubleSolenoid clawPushSolenoid) {
		ClawPushSolenoid = clawPushSolenoid;
	}

	/**
	 * @return the liftratchet
	 */
	public Servo getLiftratchet() {
		return Liftratchet;
	}

	/**
	 * @param liftratchet the liftratchet to set
	 */
	public void setLiftratchet(Servo liftratchet) {
		Liftratchet = liftratchet;
	}

	/**
	 * @return the driveThreshold
	 */
	public double getDriveThreshold() {
		return driveThreshold;
	}

	/**
	 * @param driveThreshold the driveThreshold to set
	 */
	public void setDriveThreshold(double driveThreshold) {
		this.driveThreshold = driveThreshold;
	}

	/**
	 * @return the defaultDriveThreshold
	 */
	public double getDefaultDriveThreshold() {
		return defaultDriveThreshold;
	}

	/**
	 * @param defaultDriveThreshold the defaultDriveThreshold to set
	 */
	public void setDefaultDriveThreshold(double defaultDriveThreshold) {
		this.defaultDriveThreshold = defaultDriveThreshold;
	}

	/**
	 * @return the triggerThreshold
	 */
	public double getTriggerThreshold() {
		return triggerThreshold;
	}

	/**
	 * @param triggerThreshold the triggerThreshold to set
	 */
	public void setTriggerThreshold(double triggerThreshold) {
		this.triggerThreshold = triggerThreshold;
	}

	/**
	 * @return the myRobot
	 */
	public RobotDrive getMyRobot() {
		return myRobot;
	}

	/**
	 * @param myRobot the myRobot to set
	 */
	public void setMyRobot(RobotDrive myRobot) {
		this.myRobot = myRobot;
	}

	/**
	 * @return the driveStick
	 */
	public Joystick getDriveStick() {
		return driveStick;
	}

	/**
	 * @param driveStick the driveStick to set
	 */
	public void setDriveStick(Joystick driveStick) {
		this.driveStick = driveStick;
	}

	/**
	 * @return the speedFactor
	 */
	public double getSpeedFactor() {
		return speedFactor;
	}

	/**
	 * @param speedFactor the speedFactor to set
	 */
	public void setSpeedFactor(double speedFactor) {
		this.speedFactor = speedFactor;
	}

	/**
	 * @return the defaultSpeedFactor
	 */
	public double getDefaultSpeedFactor() {
		return defaultSpeedFactor;
	}

	/**
	 * @param defaultSpeedFactor the defaultSpeedFactor to set
	 */
	public void setDefaultSpeedFactor(double defaultSpeedFactor) {
		this.defaultSpeedFactor = defaultSpeedFactor;
	}

	/**
	 * @return the xboxControllerPort
	 */
	public int getXboxControllerPort() {
		return xboxControllerPort;
	}

	/**
	 * @param xboxControllerPort the xboxControllerPort to set
	 */
	public void setXboxControllerPort(int xboxControllerPort) {
		this.xboxControllerPort = xboxControllerPort;
	}

	/**
	 * @return the defaultXboxControllerPort
	 */
	public int getDefaultXboxControllerPort() {
		return defaultXboxControllerPort;
	}

	/**
	 * @param defaultXboxControllerPort the defaultXboxControllerPort to set
	 */
	public void setDefaultXboxControllerPort(int defaultXboxControllerPort) {
		this.defaultXboxControllerPort = defaultXboxControllerPort;
	}

	/**
	 * @return the defaultLifterSpeed
	 */
	public double getDefaultLifterSpeed() {
		return defaultLifterSpeed;
	}

	/**
	 * @param defaultLifterSpeed the defaultLifterSpeed to set
	 */
	public void setDefaultLifterSpeed(double defaultLifterSpeed) {
		this.defaultLifterSpeed = defaultLifterSpeed;
	}

	/**
	 * @return the defaultClawSpeed
	 */
	public double getDefaultClawSpeed() {
		return defaultClawSpeed;
	}

	/**
	 * @param defaultClawSpeed the defaultClawSpeed to set
	 */
	public void setDefaultClawSpeed(double defaultClawSpeed) {
		this.defaultClawSpeed = defaultClawSpeed;
	}

	/**
	 * @return the defaultClawDownSpeed
	 */
	public double getDefaultClawDownSpeed() {
		return defaultClawDownSpeed;
	}

	/**
	 * @param defaultClawDownSpeed the defaultClawDownSpeed to set
	 */
	public void setDefaultClawDownSpeed(double defaultClawDownSpeed) {
		this.defaultClawDownSpeed = defaultClawDownSpeed;
	}

	/**
	 * @return the maxRatchetAngle
	 */
	public double getMaxRatchetAngle() {
		return maxRatchetAngle;
	}

	/**
	 * @param maxRatchetAngle the maxRatchetAngle to set
	 */
	public void setMaxRatchetAngle(double maxRatchetAngle) {
		this.maxRatchetAngle = maxRatchetAngle;
	}

	/**
	 * @return the minRatchetAngle
	 */
	public double getMinRatchetAngle() {
		return minRatchetAngle;
	}

	/**
	 * @param minRatchetAngle the minRatchetAngle to set
	 */
	public void setMinRatchetAngle(double minRatchetAngle) {
		this.minRatchetAngle = minRatchetAngle;
	}

	/**
	 * @return the autoSpeed
	 */
	public double getAutoSpeed() {
		return autoSpeed;
	}

	/**
	 * @param autoSpeed the autoSpeed to set
	 */
	public void setAutoSpeed(double autoSpeed) {
		this.autoSpeed = autoSpeed;
	}

	/**
	 * @return the switchTime
	 */
	public double getSwitchTime() {
		return switchTime;
	}

	/**
	 * @param switchTime the switchTime to set
	 */
	public void setSwitchTime(double switchTime) {
		this.switchTime = switchTime;
	}

	/**
	 * @return the scaleTime
	 */
	public double getScaleTime() {
		return scaleTime;
	}

	/**
	 * @param scaleTime the scaleTime to set
	 */
	public void setScaleTime(double scaleTime) {
		this.scaleTime = scaleTime;
	}

	/**
	 * @return the turnTime
	 */
	public double getTurnTime() {
		return turnTime;
	}

	/**
	 * @param turnTime the turnTime to set
	 */
	public void setTurnTime(double turnTime) {
		this.turnTime = turnTime;
	}

	/**
	 * @return the timeAfterTurn
	 */
	public double getTimeAfterTurn() {
		return timeAfterTurn;
	}

	/**
	 * @param timeAfterTurn the timeAfterTurn to set
	 */
	public void setTimeAfterTurn(double timeAfterTurn) {
		this.timeAfterTurn = timeAfterTurn;
	}

	/**
	 * @return the centerTime
	 */
	public double getCenterTime() {
		return centerTime;
	}

	/**
	 * @param centerTime the centerTime to set
	 */
	public void setCenterTime(double centerTime) {
		this.centerTime = centerTime;
	}

	/**
	 * @return the centerTimeAfterTurn
	 */
	public double getCenterTimeAfterTurn() {
		return centerTimeAfterTurn;
	}

	/**
	 * @param centerTimeAfterTurn the centerTimeAfterTurn to set
	 */
	public void setCenterTimeAfterTurn(double centerTimeAfterTurn) {
		this.centerTimeAfterTurn = centerTimeAfterTurn;
	}

	/**
	 * @return the centerTimeAfterDropOff
	 */
	public double getCenterTimeAfterDropOff() {
		return centerTimeAfterDropOff;
	}

	/**
	 * @param centerTimeAfterDropOff the centerTimeAfterDropOff to set
	 */
	public void setCenterTimeAfterDropOff(double centerTimeAfterDropOff) {
		this.centerTimeAfterDropOff = centerTimeAfterDropOff;
	}

	/**
	 * @return the crossLineAfterDropOff
	 */
	public boolean isCrossLineAfterDropOff() {
		return crossLineAfterDropOff;
	}

	/**
	 * @param crossLineAfterDropOff the crossLineAfterDropOff to set
	 */
	public void setCrossLineAfterDropOff(boolean crossLineAfterDropOff) {
		this.crossLineAfterDropOff = crossLineAfterDropOff;
	}

	/**
	 * @return the robotLifterTime
	 */
	public double getRobotLifterTime() {
		return robotLifterTime;
	}

	/**
	 * @param robotLifterTime the robotLifterTime to set
	 */
	public void setRobotLifterTime(double robotLifterTime) {
		this.robotLifterTime = robotLifterTime;
	}

	/**
	 * @return the clawLifterTime
	 */
	public double getClawLifterTime() {
		return clawLifterTime;
	}

	/**
	 * @param clawLifterTime the clawLifterTime to set
	 */
	public void setClawLifterTime(double clawLifterTime) {
		this.clawLifterTime = clawLifterTime;
	}

	/**
	 * @return the cpSwitch
	 */
	public ColorPosition getCpSwitch() {
		return cpSwitch;
	}

	/**
	 * @param cpSwitch the cpSwitch to set
	 */
	public void setCpSwitch(ColorPosition cpSwitch) {
		this.cpSwitch = cpSwitch;
	}

	/**
	 * @return the cpScale
	 */
	public ColorPosition getCpScale() {
		return cpScale;
	}

	/**
	 * @param cpScale the cpScale to set
	 */
	public void setCpScale(ColorPosition cpScale) {
		this.cpScale = cpScale;
	}

	/**
	 * @return the startChooser
	 */
	public SendableChooser<Integer> getStartChooser() {
		return startChooser;
	}

	/**
	 * @param startChooser the startChooser to set
	 */
	public void setStartChooser(SendableChooser<Integer> startChooser) {
		this.startChooser = startChooser;
	}

	/**
	 * @return the cam
	 */
	public UsbCamera getCam() {
		return cam;
	}

	/**
	 * @param cam the cam to set
	 */
	public void setCam(UsbCamera cam) {
		this.cam = cam;
	}

	/**
	 * @return the cam2
	 */
	public UsbCamera getCam2() {
		return cam2;
	}

	/**
	 * @param cam2 the cam2 to set
	 */
	public void setCam2(UsbCamera cam2) {
		this.cam2 = cam2;
	}
}








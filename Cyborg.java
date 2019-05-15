package org.usfirst.frc.team945.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
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
 * 
 * @param <gamepadRateLimit>
 */
@SuppressWarnings("deprecation")
public class Cyborg<gamepadRateLimit> extends SampleRobot {

	VictorSP armLifter = new VictorSP(1);

	// The code below is something I found for the xbox 360 controller not the xbox
	// one
	/**
	 * The operator joystick
	 */
	Joystick operator = new Joystick(0); // where "0" is the index of the joystick (you can set this in the Driver
											// Station software).
	/**
	 * The leftDriveStick
	 */
	Joystick leftDriveStick = new Joystick(1); // Where "1" is the index of the joystick (you can set this in the Driver
												// Station software).
	/**
	 * The leftDriveStick
	 */
	Joystick rightDriveStick = new Joystick(2); // Where "2" is the index of the joystick (you can set this in the
												// Driver Station software).
	/**
	 * The front left speed controller on port 3.
	 */
	SpeedController rearRightMotor = new Spark(3);
	/**
	 * The rear left speed controller on port 2.
	 */
	SpeedController frontRightMotor = new Spark(4);
	/**
	 * The front right speed controller on port 0.
	 */
	SpeedController rearLeftMotor = new Spark(1);
	/**
	 * The rear right speed controller on port 1.
	 */
	SpeedController frontLeftMotor = new Spark(2);
	/**
	 * This is going be the arm lifter
	 */
	// SpeedController armLifter = new Spark (8);
	/**
	 * This is going be the hand lifter that picks up balls
	 */
	SpeedController handLifter = new Spark(6);
	/**
	 * The fingers that pull in the ball.
	 */
	SpeedController fingersLeft = new Spark(9);
	/**
	 * The fingers that pull in the ball.
	 */
	SpeedController fingersRight = new Spark(7);
	/**
	 * The front lifter motor that lifts the front wheels
	 */
	SpeedController frontLifter = new Spark(0);
	/**
	 * The rear lifter motor that lifts the rear wheels.
	 */
	SpeedController rearLifter = new Spark(11);
	/**
	 * The motor that drives the lifter wheels foward or backward.
	 */
	SpeedController lifterdrive = new Spark(10);
	/**
	 * The limit switch for the armLifter
	 */
	DigitalInput armToHigh = new DigitalInput(9);
	/**
	 * The limit switch for the armLifter
	 */
	DigitalInput armToLow = new DigitalInput(12);
	/**
	 * The limit switch for the handLifter
	 */
	DigitalInput handToHigh = new DigitalInput(13);
	/**
	 * The limit switch for the handLifter
	 */
	DigitalInput handToLow = new DigitalInput(14);
	/**
	 * The limit switch for the handLifter
	 */
	DigitalInput frontLiftersTooHigh = new DigitalInput(15);
	/**
	 * The limit switch for the handLifter
	 */
	DigitalInput frontLiftersTooLow = new DigitalInput(16);
	/**
	 * The limit switch for the handLifter
	 */
	DigitalInput rearLiftersTooHigh = new DigitalInput(17);
	/**
	 * The Claw solenoid that opens or closes the claw
	 */
	DoubleSolenoid ClawSolenoid = new DoubleSolenoid(4, 5);
	/**
	 * The RearLift solenoid that opens or closes the rear Pneumatic
	 */
	DoubleSolenoid RearLiftSolenoid = new DoubleSolenoid(0, 1);
	/**
	 * The FrontLift solenoid that opens or closes the Front Pneumatics
	 */
	DoubleSolenoid FrontLiftSolenoid = new DoubleSolenoid(3, 2);
	/**
	 * The threshold to read from the joysticks before moving anything
	 */
	private double driveThreshold = 0.26;
	/**
	 * The threshold to use when reading arm joystick.
	 */
	private double armThreshold = 0.26;
	/**
	 * The speed of the arm when raising or lowering it.
	 */
	private double armSpeed = 1.0;
	/**
	 * The speed of the hand when trying to keep up with the arm. This slows down
	 * the arm speed if it's too low.
	 */
	private double handSpeed = 0.3;
	/**
	 * The speed of the fingers to pull the ball in or push it out.
	 */
	private double fingersSpeedIn = 1.0;
	/**
	 * 
	 */
	private double fingersSpeedOut = 1.0;
	/**
	 * The value multiplied to the Joystick values. Less than 1 means that it will
	 * make the robot slower and more than 1 makes the robot faster.
	 */
	private double speedFactor = 1.0;

	private double lowerArmTime = 0.1;

	private enum LiftState {
		RobotOnGround, HalfOnPlatform, FullOnPlatform
	}

	/**
	 * The robot drive. This class is used to control the driving of the robot. It
	 * needs the speed controllers that are used to control each individual motor.
	 */
	

	private MecanumDrive aMecanumDrive = new MecanumDrive(frontLeftMotor, rearLeftMotor, frontRightMotor,
			rearRightMotor);

	ADXRS450_Gyro gyro = new ADXRS450_Gyro();

	public void Robot() {
	}

	// Image img = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	UsbCamera cam = CameraServer.getInstance().startAutomaticCapture(0);
	UsbCamera cam2 = CameraServer.getInstance().startAutomaticCapture(1);
	private int liftState;

	/**
	 * @author James Finds the scale and switch color positions from the Driver
	 *         Station game specific message.
	 */
	private void getGameData() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		System.out.println("Game Data: " + gameData);
	}

	/**
	 * This will be called once and is used to initialize any robot data.
	 */
	@Override
	public void robotInit() {
		// This expiration is used to turn off the motor if it isn't set
		// after the expiration time. The time is set to 0.1 seconds, after which
		// the motor will stop moving if no inputs were sent to it.
		aMecanumDrive.setExpiration(0.1);
		/**
		 * TODO: need to check invert values on motors.
		 */
		rearLeftMotor.setInverted(false);
		frontLeftMotor.setInverted(false);
		rearRightMotor.setInverted(false);
		frontRightMotor.setInverted(false);

		fingersLeft.setInverted(true);
		armLifter.setInverted(false);
		handLifter.setInverted(true);
		gyro.calibrate();

		printStatus();

		SmartDashboard.putNumber("Drive Threshold", driveThreshold);
		SmartDashboard.putNumber("Arm Threshold", armThreshold);
		SmartDashboard.putNumber("Arm Speed", armSpeed);
		SmartDashboard.putNumber("Hand Speed", handSpeed);
		SmartDashboard.putNumber("Fingers Speed In", fingersSpeedIn);
		SmartDashboard.putNumber("Fingers Speed Out", fingersSpeedOut);
		SmartDashboard.putNumber("Driving Speed", speedFactor);
		SmartDashboard.putBoolean("Override Warnings", false);

		// Adjust camera settings please, the cameras are too bright and white washed
		printStatus();
		Timer.delay(1.0);
		(new Thread() {
			public void run() {
				cam.setResolution(649, 480);
				cam.setExposureManual(0);
				cam.setBrightness(40);
				cam.setFPS(80);
				cam.setWhiteBalanceManual(0);
			}
		}).start();

		(new Thread() {
			public void run() {
				cam2.setResolution(649, 480);
				cam2.setExposureManual(0);
				cam2.setBrightness(40);
				cam2.setFPS(80);
				cam2.setWhiteBalanceManual(0);
			}
		}).start();

		RearLiftSolenoid.set(DoubleSolenoid.Value.kOff);
		RearLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
		RearLiftSolenoid.set(DoubleSolenoid.Value.kForward);

		FrontLiftSolenoid.set(DoubleSolenoid.Value.kOff);
		FrontLiftSolenoid.set(DoubleSolenoid.Value.kForward);
		FrontLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
		printStatus();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * if-else structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	public void turnOffEverything() {
		// For safety always turn off everything
		aMecanumDrive.setSafetyEnabled(true);
		ClawSolenoid.set(DoubleSolenoid.Value.kOff);
		
		armLifter.set(0.0);
		handLifter.set(0.0);
		printStatus();
	}

	public void autonomous() {
		getGameData();
		turnOffEverything();

		double leftX = 0.0;
		double leftY = 0.0;
		double rightX = 0.0;
		double rightY = 0.0;
		rearLeftMotor.setInverted(false);
		frontLeftMotor.setInverted(false);
		rearRightMotor.setInverted(false);
		frontRightMotor.setInverted(false);

		while (isAutonomous() && isEnabled()) {
			speedFactor = SmartDashboard.getNumber("Speed Factor", speedFactor);
			driveThreshold = SmartDashboard.getNumber("Drive Threshold", driveThreshold);

			leftX = leftDriveStick.getX();
			if (Math.abs(leftX) < driveThreshold) {
				leftX = 0.0;
			}
			leftY = leftDriveStick.getY();
			if (Math.abs(leftY) < driveThreshold) {
				leftY = 0.0;
			}
			rightX = rightDriveStick.getX();
			if (Math.abs(rightX) < driveThreshold) {
				rightX = 0.0;
			}
			rightY = rightDriveStick.getY();
			if (Math.abs(rightY) < driveThreshold) {
				rightY = 0.0;
			}
			aMecanumDrive.driveCartesian(leftX * speedFactor, leftY * speedFactor, rightX * speedFactor * -1.0);
		}
		turnOffEverything();
	}

	public void Turn(double speed, double time, boolean isLeft) {
		if (isLeft) {
			Drive(speed * -1.0, speed, time);
		} else {
			Drive(speed, speed * -1.0, time);
		}
	}

	public void TurnLeft(double speed, double time) {
		System.out.println("Turning Left at " + speed + " for " + time + ".");
		Turn(speed, time, true);
	}

	public void TurnRight(double speed, double time) {
		System.out.println("Turning Right at " + speed + " for " + time + ".");
		Turn(speed, time, false);
	}

	public void Drive(double leftValue, double rightValue, double time) {
		// myRobot.tankDrive(leftValue, rightValue, true);
		printStatus();
		Timer.delay(time);
		aMecanumDrive.stopMotor();
		printStatus();
	}

	public void Drive(double speed, double time) {
		System.out.println("Driving at " + speed + " for " + time + ".");
		Drive(speed, speed, time);
	}

	public void printStatus() {
		SmartDashboard.putNumber("Gyro", gyro.getAngle());
		SmartDashboard.putNumber("Left y", leftDriveStick.getY());
		SmartDashboard.putNumber("Left z", leftDriveStick.getZ());
		SmartDashboard.putNumber("Right y", rightDriveStick.getY());
		SmartDashboard.putNumber("Right z", rightDriveStick.getZ());
		SmartDashboard.putNumber("Operator y", operator.getY());
		SmartDashboard.putNumber("Operator z", operator.getZ());
		SmartDashboard.putBoolean("Hand Override", operator.getRawButton(3));
		SmartDashboard.putBoolean("Pull Ball", operator.getRawButton(6));
		SmartDashboard.putBoolean("Push Ball", operator.getRawButton(7));

		if (ClawSolenoid.get() == Value.kForward) {
			SmartDashboard.putString("Claw", "Open");
		} else if (ClawSolenoid.get() == Value.kReverse) {
			SmartDashboard.putString("Claw", "Close");
		} else if (ClawSolenoid.get() == Value.kOff) {
			SmartDashboard.putString("Claw", "Neutral");
		} else {
			SmartDashboard.putString("Claw", "Unknown: " + ClawSolenoid.get());
		}

		if (armToHigh.get())
			SmartDashboard.putString("Arm", "TOO HIGH!");
		else if (armToLow.get())
			SmartDashboard.putString("Arm", "TOO LOW!");
		else
			SmartDashboard.putString("Arm", "Okie Dokie");
		if (handToHigh.get())
			SmartDashboard.putString("Hand", "TOO HIGH!");
		else if (handToLow.get())
			SmartDashboard.putString("Hand", "TOO LOW!");
		else
			SmartDashboard.putString("Hand", "Okie Dokie");
		String frontLiftersLocation = "up";
		if (frontLiftersTooHigh.get())
			frontLiftersLocation = "Up";
		else if (frontLiftersTooLow.get())
			frontLiftersLocation = "Down";
		else
			frontLiftersLocation = "Moving";

		SmartDashboard.putString("Front Lifters", frontLiftersLocation);
		if (rearLiftersTooHigh.get())
			SmartDashboard.putString("Rear Lifters", frontLiftersLocation);
		else if (frontLiftersTooLow.get())
			SmartDashboard.putString("Rear Lifters", "Down");
		else
			SmartDashboard.putString("Rear Lifters", "Moving or Down");
	}//

	/**
	 * Runs the motors with tank drive steering.
	 */
	@Override
	public void operatorControl() {
		// LowerArm();
		// Enabling safety allows the motor to automatically
		// shutdown after the specified expire time. In our case
		// it's 0.1 seconds.
		aMecanumDrive.setSafetyEnabled(true);
		boolean goingUp = false;
		double leftX = 0.0;
		double leftY = 0.0;
		double leftZ = 0.0;

		double rightX = 0.0;
		double rightY = 0.0;
		double rightZ = 0.0;
		double armY = 0.0;
		double armZ = 0.0;
		double previousGyroValue = 0.0;
		var liftState = LiftState.RobotOnGround;
		// Only drive while in teleOperated mode
		while (isOperatorControl() && isEnabled()) {
			// There are 2 ways to drive mecanum wheels
			// one is like a translation of the robot without turning (specifying an x and y
			// speed and let it get there without turning.)
			// the other turns like a normal car (goes forward, reverse and turns left spin
			// and right spin)

			speedFactor = SmartDashboard.getNumber("Speed Factor", speedFactor);
			driveThreshold = SmartDashboard.getNumber("Drive Threshold", driveThreshold);

			leftX = leftDriveStick.getX();
			if (Math.abs(leftX) < driveThreshold) {
				leftX = 0.0;
			}
			leftY = leftDriveStick.getY();
			if (Math.abs(leftY) < driveThreshold) {
				leftY = 0.0;
			}
			leftZ = leftDriveStick.getZ();

			rightX = rightDriveStick.getX();
			if (Math.abs(rightX) < driveThreshold) {
				rightX = 0.0;
			}
			rightY = rightDriveStick.getY();
			if (Math.abs(rightY) < driveThreshold) {
				rightY = 0.0;
			}
			rightZ = rightDriveStick.getZ();

			// myRobot.tankDrive(leftY * speedFactor * leftZ, rightY * speedFactor * rightZ,
			// true);

			// myRobot.mecanumDrive_Cartesian(leftY * speedFactor * leftZ, rightY *
			// speedFactor * rightZ, true);
			// drives without turning (crab walk side to side)
			aMecanumDrive.driveCartesian(leftX * speedFactor, leftY * speedFactor, rightX * speedFactor * -1.0);

			boolean armUp = true;
			boolean armDown = true;
			// warning no limit switches for the hand
			// boolean handUp = false;
			// boolean handDown = false;

			if (SmartDashboard.getBoolean("Override Warnings", true)) {
				armUp = false;
				armDown = false;
				// handUp = false;
				// handDown = false;
			} else {
				armUp = armToHigh.get();
				armDown = armToLow.get();
				// warning no limit switches for the hand
				// handUp = handToHigh.get();
				// handDown = handToLow.get();
			}

			armY = operator.getY();
			if (Math.abs(armY) < armThreshold) {
				armY = 0.0;
			}
			armZ = operator.getZ();
			Object ControlMode;
			//Object controlMode2 = ControlMode;
			if (armZ > 0.0 && !armUp) {
				armLifter.set(limit(armZ * armSpeed * armY));
			} else if (armY < 0.0) {// && !armDown){
				armLifter.set(limit(armZ * armSpeed * armY));
			} else {
				armLifter.set(0);
			}

			if (operator.getRawButton(6)) {
				// pull the ball in
				fingersLeft.set(fingersSpeedIn);
				fingersRight.set(fingersSpeedIn);
			} else if (operator.getRawButton(7)) {
				// push the ball out
				fingersLeft.set(fingersSpeedOut * -1.0);
				fingersRight.set(fingersSpeedOut * -1.0);
			} else {
				// stop fingers
				fingersLeft.set(0.0);
				fingersRight.set(0.0);
			}

			if (operator.getRawButton(8)) {
				// open the claw
				ClawSolenoid.set(DoubleSolenoid.Value.kForward);
			} else if (operator.getRawButton(9)) {
				// Close the claw
				ClawSolenoid.set(DoubleSolenoid.Value.kReverse);
			}

			// RearLiftSolenoid.set(DoubleSolenoid.Value.kOff);
			if (leftDriveStick.getRawButton(8)) {
				// open the claw
				RearLiftSolenoid.set(DoubleSolenoid.Value.kForward);
			} else if (leftDriveStick.getRawButton(9)) {
				// Close the claw
				RearLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
			}

			if (rightDriveStick.getRawButton(8)) {
				FrontLiftSolenoid.set(DoubleSolenoid.Value.kForward);
			} else if (rightDriveStick.getRawButton(9)) {
				FrontLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
			} else {
				FrontLiftSolenoid.set(DoubleSolenoid.Value.kOff);
			}

			// TODO: RobotLift(liftState);

			// This is for emergencies. When trying to lower the arm if the hand isn't level
			// or correct this will make the hand go up.
			if (operator.getRawButton(3)) {// && !handUp){
				handLifter.set(handSpeed);
			} else if (operator.getRawButton(4)) {
				handLifter.set(-1.0 * handSpeed);
			} else if (gyro.getAngle() == previousGyroValue) {
				handLifter.set(0.0);
				gyro.calibrate();
			} else {
				previousGyroValue = gyro.getAngle();
				// DriverStation.reportWarning("Gyro:" + gyro.getAngle(), false);
				// we're only going to try this 10 times. We don't want an infinite loop.
				// 100 times is 0.5 seconds so if we're not working by then we have another
				// problem.
				// for(int tries = 10; tries > 0; tries--){

				// if the gyro is less than 0.0 then raise it up.
				if (previousGyroValue < -0.5 || goingUp) {
					// if(!handUp){
					DriverStation.reportWarning("Hand Up.", false);
					handLifter.set(-1.0 * handSpeed);
					if (previousGyroValue > 0.5) {
						goingUp = true;
					} else {
						goingUp = true;
					}
					// }
				} else if (previousGyroValue > 0.6) {// if the gyro is greater than 0.0 then lower it down.
					// if(!handDown){
					DriverStation.reportWarning("Hand Down.", true);
					handLifter.set(handSpeed);
					// }
				} else {
					DriverStation.reportWarning("Hand Neutral.", false);
					handLifter.set(0.0);
				}
				// Timer.delay(0.005);
				// }
				// handLifter.set(0.0);
			}
			// DriverStation.reportWarning("Hand Lifter " + handLifter.get(), false);
			// DriverStation.reportWarning(Timer.getFPGATimestamp() + " Robot Time in
			// Seconds 3.", false);
			printStatus();
			Timer.delay(0.005); // wait for a motor update time
		}
		turnOffEverything();
	}

	protected static double limit(double number) {
		if (number > 1.0) {
			return 1.0;
		}
		if (number < -1.0) {
			return -1.0;
		}
		return number;
	}

	/**
	 * @return the armLifter
	 */
	public VictorSP getArmLifter() {
		return armLifter;
	}

	/**
	 * @param armLifter the armLifter to set
	 */
	public void setArmLifter(VictorSP armLifter) {
		this.armLifter = armLifter;
	}

	/**
	 * @return the operator
	 */
	public Joystick getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(Joystick operator) {
		this.operator = operator;
	}

	/**
	 * @return the leftDriveStick
	 */
	public Joystick getLeftDriveStick() {
		return leftDriveStick;
	}

	/**
	 * @param leftDriveStick the leftDriveStick to set
	 */
	public void setLeftDriveStick(Joystick leftDriveStick) {
		this.leftDriveStick = leftDriveStick;
	}

	/**
	 * @return the rightDriveStick
	 */
	public Joystick getRightDriveStick() {
		return rightDriveStick;
	}

	/**
	 * @param rightDriveStick the rightDriveStick to set
	 */
	public void setRightDriveStick(Joystick rightDriveStick) {
		this.rightDriveStick = rightDriveStick;
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
	 * @return the handLifter
	 */
	public SpeedController getHandLifter() {
		return handLifter;
	}

	/**
	 * @param handLifter the handLifter to set
	 */
	public void setHandLifter(SpeedController handLifter) {
		this.handLifter = handLifter;
	}

	/**
	 * @return the fingersLeft
	 */
	public SpeedController getFingersLeft() {
		return fingersLeft;
	}

	/**
	 * @param fingersLeft the fingersLeft to set
	 */
	public void setFingersLeft(SpeedController fingersLeft) {
		this.fingersLeft = fingersLeft;
	}

	/**
	 * @return the fingersRight
	 */
	public SpeedController getFingersRight() {
		return fingersRight;
	}

	/**
	 * @param fingersRight the fingersRight to set
	 */
	public void setFingersRight(SpeedController fingersRight) {
		this.fingersRight = fingersRight;
	}

	/**
	 * @return the frontLifter
	 */
	public SpeedController getFrontLifter() {
		return frontLifter;
	}

	/**
	 * @param frontLifter the frontLifter to set
	 */
	public void setFrontLifter(SpeedController frontLifter) {
		this.frontLifter = frontLifter;
	}

	/**
	 * @return the rearLifter
	 */
	public SpeedController getRearLifter() {
		return rearLifter;
	}

	/**
	 * @param rearLifter the rearLifter to set
	 */
	public void setRearLifter(SpeedController rearLifter) {
		this.rearLifter = rearLifter;
	}

	/**
	 * @return the lifterdrive
	 */
	public SpeedController getLifterdrive() {
		return lifterdrive;
	}

	/**
	 * @param lifterdrive the lifterdrive to set
	 */
	public void setLifterdrive(SpeedController lifterdrive) {
		this.lifterdrive = lifterdrive;
	}

	/**
	 * @return the armToHigh
	 */
	public DigitalInput getArmToHigh() {
		return armToHigh;
	}

	/**
	 * @param armToHigh the armToHigh to set
	 */
	public void setArmToHigh(DigitalInput armToHigh) {
		this.armToHigh = armToHigh;
	}

	/**
	 * @return the armToLow
	 */
	public DigitalInput getArmToLow() {
		return armToLow;
	}

	/**
	 * @param armToLow the armToLow to set
	 */
	public void setArmToLow(DigitalInput armToLow) {
		this.armToLow = armToLow;
	}

	/**
	 * @return the handToHigh
	 */
	public DigitalInput getHandToHigh() {
		return handToHigh;
	}

	/**
	 * @param handToHigh the handToHigh to set
	 */
	public void setHandToHigh(DigitalInput handToHigh) {
		this.handToHigh = handToHigh;
	}

	/**
	 * @return the handToLow
	 */
	public DigitalInput getHandToLow() {
		return handToLow;
	}

	/**
	 * @param handToLow the handToLow to set
	 */
	public void setHandToLow(DigitalInput handToLow) {
		this.handToLow = handToLow;
	}

	/**
	 * @return the frontLiftersTooHigh
	 */
	public DigitalInput getFrontLiftersTooHigh() {
		return frontLiftersTooHigh;
	}

	/**
	 * @param frontLiftersTooHigh the frontLiftersTooHigh to set
	 */
	public void setFrontLiftersTooHigh(DigitalInput frontLiftersTooHigh) {
		this.frontLiftersTooHigh = frontLiftersTooHigh;
	}

	/**
	 * @return the frontLiftersTooLow
	 */
	public DigitalInput getFrontLiftersTooLow() {
		return frontLiftersTooLow;
	}

	/**
	 * @param frontLiftersTooLow the frontLiftersTooLow to set
	 */
	public void setFrontLiftersTooLow(DigitalInput frontLiftersTooLow) {
		this.frontLiftersTooLow = frontLiftersTooLow;
	}

	/**
	 * @return the rearLiftersTooHigh
	 */
	public DigitalInput getRearLiftersTooHigh() {
		return rearLiftersTooHigh;
	}

	/**
	 * @param rearLiftersTooHigh the rearLiftersTooHigh to set
	 */
	public void setRearLiftersTooHigh(DigitalInput rearLiftersTooHigh) {
		this.rearLiftersTooHigh = rearLiftersTooHigh;
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
	 * @return the rearLiftSolenoid
	 */
	public DoubleSolenoid getRearLiftSolenoid() {
		return RearLiftSolenoid;
	}

	/**
	 * @param rearLiftSolenoid the rearLiftSolenoid to set
	 */
	public void setRearLiftSolenoid(DoubleSolenoid rearLiftSolenoid) {
		RearLiftSolenoid = rearLiftSolenoid;
	}

	/**
	 * @return the frontLiftSolenoid
	 */
	public DoubleSolenoid getFrontLiftSolenoid() {
		return FrontLiftSolenoid;
	}

	/**
	 * @param frontLiftSolenoid the frontLiftSolenoid to set
	 */
	public void setFrontLiftSolenoid(DoubleSolenoid frontLiftSolenoid) {
		FrontLiftSolenoid = frontLiftSolenoid;
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
	 * @return the armThreshold
	 */
	public double getArmThreshold() {
		return armThreshold;
	}

	/**
	 * @param armThreshold the armThreshold to set
	 */
	public void setArmThreshold(double armThreshold) {
		this.armThreshold = armThreshold;
	}

	/**
	 * @return the armSpeed
	 */
	public double getArmSpeed() {
		return armSpeed;
	}

	/**
	 * @param armSpeed the armSpeed to set
	 */
	public void setArmSpeed(double armSpeed) {
		this.armSpeed = armSpeed;
	}

	/**
	 * @return the handSpeed
	 */
	public double getHandSpeed() {
		return handSpeed;
	}

	/**
	 * @param handSpeed the handSpeed to set
	 */
	public void setHandSpeed(double handSpeed) {
		this.handSpeed = handSpeed;
	}

	/**
	 * @return the fingersSpeedIn
	 */
	public double getFingersSpeedIn() {
		return fingersSpeedIn;
	}

	/**
	 * @param fingersSpeedIn the fingersSpeedIn to set
	 */
	public void setFingersSpeedIn(double fingersSpeedIn) {
		this.fingersSpeedIn = fingersSpeedIn;
	}

	/**
	 * @return the fingersSpeedOut
	 */
	public double getFingersSpeedOut() {
		return fingersSpeedOut;
	}

	/**
	 * @param fingersSpeedOut the fingersSpeedOut to set
	 */
	public void setFingersSpeedOut(double fingersSpeedOut) {
		this.fingersSpeedOut = fingersSpeedOut;
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
	 * @return the lowerArmTime
	 */
	public double getLowerArmTime() {
		return lowerArmTime;
	}

	/**
	 * @param lowerArmTime the lowerArmTime to set
	 */
	public void setLowerArmTime(double lowerArmTime) {
		this.lowerArmTime = lowerArmTime;
	}

	/**
	 * @return the aMecanumDrive
	 */
	public MecanumDrive getaMecanumDrive() {
		return aMecanumDrive;
	}

	/**
	 * @param aMecanumDrive the aMecanumDrive to set
	 */
	public void setaMecanumDrive(MecanumDrive aMecanumDrive) {
		this.aMecanumDrive = aMecanumDrive;
	}

	/**
	 * @return the gyro
	 */
	public ADXRS450_Gyro getGyro() {
		return gyro;
	}

	/**
	 * @param gyro the gyro to set
	 */
	public void setGyro(ADXRS450_Gyro gyro) {
		this.gyro = gyro;
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

	/**
	 * @return the liftState
	 */
	public int getLiftState() {
		return liftState;
	}

	/**
	 * @param liftState the liftState to set
	 */
	public void setLiftState(int liftState) {
		this.liftState = liftState;
	}

	@Override
	public void close() {
		super.close();
	}

	@Override
	protected void disabled() {
		super.disabled();
	}
}

	







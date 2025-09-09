package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;


public class Robot extends TimedRobot {

  private Joystick joydelicio = new Joystick(0);

  private VictorSPX RMotor1 = new VictorSPX(1);
  private VictorSPX RMotor2 = new VictorSPX(2);
  private VictorSPX LMotor1 = new VictorSPX(4);
  private VictorSPX LMotor2 = new VictorSPX(3);

  private NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");

  int POG;
  boolean a, b, x, analog1, analog2, ltrigbool, rtrigbool, lastA=false, lastB=false, lastX=false;
  double spdbutton = 1, Lm, Rm, Ltrig, Rtrig, mag, mag2, sen, sen2, x1, x2, y1, y2, tx, ta, tv;

  @Override
  public void robotInit() {
    RMotor2.follow(RMotor1);
    LMotor2.follow(LMotor1);

    RMotor1.setInverted(false);
    RMotor2.setInverted(false);
    LMotor1.setInverted(true);
    LMotor2.setInverted(true);

    RMotor1.configNeutralDeadband(0.04);
    LMotor1.configNeutralDeadband(0.04);
    RMotor2.configNeutralDeadband(0.04);
    LMotor2.configNeutralDeadband(0.04);

    RMotor1.setNeutralMode(NeutralMode.Brake);
    LMotor1.setNeutralMode(NeutralMode.Brake);
    RMotor2.setNeutralMode(NeutralMode.Brake);
    LMotor2.setNeutralMode(NeutralMode.Brake);
  }

  @Override
  public void teleopPeriodic() {

    a = joydelicio.getRawButton(1);
    b = joydelicio.getRawButton(2);
    x = joydelicio.getRawButton(4);
    
    Ltrig = joydelicio.getRawAxis(3);
    Rtrig = joydelicio.getRawAxis(2);

    x1 = joydelicio.getRawAxis(0); 
    x2 = joydelicio.getRawAxis(4);  
    y1 = -joydelicio.getRawAxis(1); 
    y2 = joydelicio.getRawAxis(5); 

    mag = Math.hypot(x1, y1);
    mag2 = Math.hypot(x2, y2);

    mag = Math.max(-1, Math.min(mag, 1));
    mag2  = Math.max(-1, Math.min(mag2, 1));

    sen = (mag != 0) ? y1 / mag : 0;
    sen2 = (mag2 != 0) ? y2 / mag2 : 0;

    POG = joydelicio.getPOV();
    
    CalcButton();    
    
    CalcPov();

    if (Ltrig > 0.04 && rtrigbool == false){
      CalcLTrig();
    } else if (Rtrig > 0.04 && ltrigbool == false){
      CalcRTrig();
    } 

    else {

      if (mag != 0 && !analog2) {
        analog1 = true;
        analog2 = false;
        CalcAnalog1();
  
      } else if (mag2 != 0 && !analog1) {
        analog1 = false;
        analog2 = true;
        CalcAnalog2();
  
      } 
    }

    RMotor1.set(ControlMode.PercentOutput, Rm);
    LMotor1.set(ControlMode.PercentOutput, Lm);

    Smartdashboard();

  }

  @Override
  public void autonomousPeriodic() {

    CalcFollower();

    RMotor1.set(ControlMode.PercentOutput, Rm);
    LMotor1.set(ControlMode.PercentOutput, Lm);

    Smartdashboard();
}

  public void CalcFollower() {
    tx = limelight.getEntry("tx").getDouble(0.0);
    ta = limelight.getEntry("ta").getDouble(0.0);
    tv = limelight.getEntry("tv").getDouble(0.0); 
  
    if (tv < 1.0) {
      Lm = 0;
      Rm = 0;
      return;
    }
  
    // Ajustes finos
    double rot_percent = 0.02;     
    double fwd_percent = 0.1;      
    double targetArea = 5.0;  
  
    double rot = rot_percent * tx; 
    double forward = fwd_percent * (targetArea - ta);
  
    forward = Math.max(-0.6, Math.min(forward, 0.6));
    rot = Math.max(-0.4, Math.min(rot, 0.4));
  
    Lm = -(forward + rot);
    Rm = -(forward - rot);
  }

  public void CalcButton(){
    // Toggle para A
    if (a && !lastA) {
      spdbutton = 0.25;
    }

    // Toggle para B
    if (b && !lastB) {
        spdbutton = 0.5;
    }

    // Toggle para X
    if (x && !lastX) {
        spdbutton = 1.0;
    }

    lastA = a;
    lastB = b;
    lastX = x;
  }

  public void CalcLTrig(){
    if (Rtrig < 0.04 && Ltrig >= 0.04) {
      Ltrig *= -spdbutton;
      Lm = Ltrig; Rm = Ltrig;
      ltrigbool = true;
    } 
  }

  public void CalcRTrig(){
    if (Ltrig < 0.04 && Rtrig >= 0.04) {
      Rtrig *= spdbutton;
      Lm = Rtrig; Rm = Rtrig;
      rtrigbool = true;
    }
  }

  public void CalcAnalog1(){
    // QUADRANTE 1
    if (x1 >= 0 && y1 >= 0) {
      Lm = mag * spdbutton;
      Rm = (2 * sen - 1) * mag * spdbutton;
    }
    // QUADRANTE 2
    else if (x1 < 0 && y1 >= 0) {
        Lm = (2 * sen - 1) * mag * spdbutton;
        Rm = mag * spdbutton;
    }
    // QUADRANTE 3
    else if (x1 >= 0 && y1 < 0) {
        Lm = -mag * spdbutton;
        Rm = (2 * sen + 1) * mag * spdbutton;
    }
    // QUADRANTE 4
    else if (x1 < 0 && y1 < 0) {
        Lm = (2 * sen + 1) * mag * spdbutton;
        Rm = -mag * spdbutton;
    }
  }

  public void CalcAnalog2(){
    // QUADRANTE 1
    if (x2 >= 0 && y2 >= 0) {
      Rm = (-2 * sen2 + 1) * mag2 * spdbutton;
      Lm = -mag2 * spdbutton;
    }
    // QUADRANTE 2
    else if (x2 < 0 && y2 >= 0) {
        Rm = -mag2 * spdbutton;
        Lm = (-2 * sen2 + 1) * mag2 * spdbutton;
    }
    // QUADRANTE 3
    else if (x2 >= 0 && y2 < 0) {
        Rm = mag2 * spdbutton;
        Lm = (-2 * sen2 - 1) * mag2 * spdbutton;
    }
    // QUADRANTE 4
    else if (x2 < 0 && y2 < 0) {
        Rm = (-2 * sen2 - 1) * mag2 * spdbutton;
        Lm = mag2 * spdbutton;
    }
  }

  public void CalcPov(){
    switch (POG) {

      case 0:
        Rm = 0.40;
        Lm = 0.40;
        break;
      case 45:
        Rm = 0;
        Lm = 0.40;
        break;
      case 90:
        Rm = -0.40;
        Lm = 0.40;
        break;
      case 135:
        Rm = -0.40;
        Lm = 0;
        break;
      case 180:
        Rm = -0.40;
        Lm = -0.40;
        break;
      case 225:
        Rm = 0;
        Lm = -0.40;
        break;
      case 270:
        Rm = -0.40;
        Lm = 0.40;
        break;
      case 315:
        Rm = 0.40;
        Lm = 0;
        break;
      case -1:
        Rm = 0;
        Lm = 0;
    }
  }

  public void Smartdashboard(){
    SmartDashboard.putNumber("1 - Tx", tx);
    SmartDashboard.putNumber("2 - Ta", ta);
    SmartDashboard.putNumber("3 - Tv", tv);
    SmartDashboard.putNumber("* - Lm", Lm);
    SmartDashboard.putNumber("* - Rm", Rm);
    SmartDashboard.putNumber("** - SpdButton", spdbutton);
    SmartDashboard.putNumber("4 - Ltrig", Ltrig);
    SmartDashboard.putNumber("5 - Rtrig", Rtrig);
  }
}

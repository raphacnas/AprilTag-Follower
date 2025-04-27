package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {

  private Joystick joydelicio = new Joystick(0);

  private VictorSPX RMotor1 = new VictorSPX(2);
  private VictorSPX RMotor2 = new VictorSPX(3);
  private VictorSPX LMotor1 = new VictorSPX(1);
  private VictorSPX LMotor2 = new VictorSPX(2);

  int POG;
  boolean a, b, x, analog1, analog2, ltrigbool, rtrigbool, toggleA, toggleB, toggleX, lastA, lastB, lastX;
  double spdbutton = 1, Lm, Rm, Ltrig, Rtrig, mag, mag2, sen, sen2, x1, x2, y1, y2;

  @Override
  public void robotInit() {
    RMotor2.follow(RMotor1);
    LMotor2.follow(LMotor1);

    RMotor1.setInverted(true);
    LMotor1.setInverted(true);

    RMotor1.configNeutralDeadband(0.04);
    LMotor1.configNeutralDeadband(0.04);

    RMotor1.setNeutralMode(NeutralMode.Brake);
    LMotor1.setNeutralMode(NeutralMode.Brake);
  }

  @Override
  public void teleopPeriodic() {

    a = joydelicio.getRawButton(1);
    b = joydelicio.getRawButton(2);
    x = joydelicio.getRawButton(3);
    
    Ltrig = -joydelicio.getRawAxis(3);
    Rtrig = joydelicio.getRawAxis(2);

    x1 = joydelicio.getRawAxis(0); 
    x2 = joydelicio.getRawAxis(4);  
    y1 = joydelicio.getRawAxis(1); 
    y2 = -joydelicio.getRawAxis(5); 

    mag = Math.hypot(x1, y1);
    mag2 = Math.hypot(x2, y2);

    mag = Math.max(-1, Math.min(mag, 1));
    mag2  = Math.max(-1, Math.min(mag2, 1));

    sen = y1 / mag;
    sen2 = y2 / mag2;

    POG = joydelicio.getPOV();

    // BOTÃO + LÓGICA DE TOGGLE
    if (a && !lastA) toggleA = !toggleA; 
    if (b && !lastB) toggleB = !toggleB; 
    if (x && !lastX) toggleX = !toggleX; 

    lastA = a;
    lastB = b;
    lastX = x;

    spdbutton = toggleA ? 0.25 : toggleB ? 0.5 : toggleX ? 1.0 : 1.0;
    
    
    if (POG != -1){
      CalcPov();
    }

    
    if (Ltrig > 0.04 && !rtrigbool){
      CalcLTrig();
    } else if (Rtrig > 0.04 && !ltrigbool){
      CalcRTrig();
    }

    
    if (mag != 0 && !analog2) {
      analog1 = true;
      analog2 = false;
      CalcAnalog1();
    } else if (mag2 != 0 && !analog1) {
      analog1 = false;
      analog2 = true;
      CalcAnalog2();
    } if (analog1 == false && analog2 == false) {
      Rm = 0;
      Lm = 0;
    }

    RMotor1.set(ControlMode.PercentOutput, Rm);
    LMotor1.set(ControlMode.PercentOutput, Lm);

    }

    public void CalcLTrig(){
      if (Rtrig < 0.04 && Ltrig >= 0.04) {
        Ltrig *= spdbutton;
        ltrigbool = true;
      } 
    }


    public void CalcRTrig(){
      if (Ltrig < 0.04 && Rtrig >= 0.04) {
        Rtrig *= spdbutton;
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

      default:
      case -1:
       Rm = 0;
       Lm = 0;
    }
  }
}
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
  boolean a, b, x, analog1, analog2, ltrigbool, rtrigbool;
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

    RMotor1.set(ControlMode.PercentOutput, Rm);
    LMotor1.set(ControlMode.PercentOutput, Lm);

    a = joydelicio.getRawButton(1);
    b = joydelicio.getRawButton(2);
    x = joydelicio.getRawButton(3);

    Ltrig = joydelicio.getRawAxis(3);
    Rtrig = joydelicio.getRawAxis(2);

    mag = Math.hypot(x1, y1);
    mag2 = Math.hypot(x2, y2);

    mag = Math.max(-1, Math.min(mag, 1));
    mag2  = Math.max(-1, Math.min(mag2, 1));

    sen = y1 / mag;
    sen2 = y2 / mag2;

    spdbutton = a ? 0.25 : b ? 0.5 : x ? 1.0 : 0.0;

    POG = joydelicio.getPOV();

    CalcTrigs();
    CalcAnalog1();
    CalcAnalog2();
    CalcPov();

  }

  public void CalcTrigs(){
    if (Rtrig < 0.04 && Ltrig >= 0.04) {
      Ltrig *= spdbutton;
      ltrigbool = true;
    } else if (Ltrig < 0.04 && Rtrig >= 0.04) {
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
      case 45:
        Rm = 0;
        Lm = 0.40;
      case 90:
        Rm = -0.40;
        Lm = 0.40;
      case 135:
        Rm = -0.40;
        Lm = 0;
      case 180:
        Rm = -0.40;
        Lm = -0.40;
      case 225:
        Rm = 0;
        Lm = -0.40;
      case 270:
        Rm = -0.40;
        Lm = 0.40;
      case 315:
        Rm = 0.40;
        Lm = 0;

      default:
      case -1:
       Rm = 0;
       Lm = 0;
    }
  }
}
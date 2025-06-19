# 🖥️ Teleoperated-Code for FRC 

This is a code used especially for a drivetrain robot in FRC, made in my early moments as an robots programmer, 
and has been made to work along with Hydra #9163' programming team, meaning it could, and probably will, be very different from the usual drivetrain code.

<br>

---

## 📁 Drivetrain README (and Code Location)

This is the one and only branch that has the drivetrain code. In this README.md i will explain some of the reasons of the code working the way it does and, overall, the code's logic.
<br><br>

In case you still are looking for the code, which i honestly don't think happend, just open this link: <br>
https://github.com/raphacnas/Teleop-Code/tree/master/src/main/java/frc/robot/Robot.java

<br>

---
## 🎮 Controller's objective

Before getting into anything about the actual code, we have to clear up what was our idea when it came to the controller. In the team, we were using game console's
controller and, like any other, it had buttons, analogs, triggers and a pov. So, the objective of each one of these were:

- **Buttons:** We wanted the buttons to control the speeds, so, depending on which button you pressed, the overall robot speed would be regulated (except the POV's).<br>

- **Analogs:** The left analog, unlike some built-ins WPILib's functions, was meant to control the robot speed and direction, according to the magnitude and X, Y it was in. 
The right analog, had the same purpose as the left one, but it had to be inverted, so when we were at the competition, we could control the robot independently from the direction it was in.<br>
- **Triggers:** The triggers were quite simple, the left one had to make the robot faster depending on how much you are pressing it, going backwards, and the right one did the same thing, going forward.<br>
- **POV:** The POV, as expected, would just control the motors directions, changing depending on which direction it is pointed to.

<br>

---
## 👨‍💻 Mathematical Code's Logic

Overall, the code treats the analogs and the POV as trigonometric circles. The analogs mainly, not only are treated as those circles but are integrated with other math calculus, like magnitude (pythagorean theorem applied to the X and Y axis), sine, and, if you see it that way, a first order function. Due to all that, the code may seem confusing at first, but, in a very simple way, i would explain it like this: <br>


1. **Joystick as Direction Arrow**  
   - The idea is that, when you move the joystick, it will be like drawing an arrow on a circle. The direction of the arrow should tell the robot where to go.

2. **Two Important Numbers**  
   - `mag` = how hard you're pushing the joystick (length of the arrow)
   - `sen` = how much "up/down" vs "sideways" you're pushing (`y/mag`)
  
3. **Inverted Sticks** <br>
The Y Axis (`y1` and `y2`) and the left trigger stick are inverted. There is nothing much to say about it, but it was made just so the left trigger could go backwards and the Y Axis in both analogs would be facing upwards.

4. **Analogs**  
   - Forward movements (joystick up):
     - One motor always goes full speed
     - Other motor speed = `(2 × sen - 1) × mag`
     - This makes smooth turns

   - Why `(2×sen - 1)`?  
      - When joystick is all the way forward (`sen = 1`):  
        → `(2×1 - 1) = 1` (both motors same speed = straight)
      - When sideways (`sen = 0`):  
        → `(2×0 - 1) = -1` (motors opposite = spin in place)
      - In-between = smooth turns

   - Backwards Movements  
      - Same idea, but with `(2×sen + 1)` and negative speeds

5. **D-Pad (POV)**  
   - Simple 8-direction control with fixed 40% speed

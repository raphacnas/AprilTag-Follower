## 🎮 Controller's objective

Before getting into anything about the actual code, we have to clear up what was our idea when it came to the controller. In the team, we were using game console's
controller and, like any other, it had buttons, analogs, triggers and a pov. So, the objective of each one of these were:

- **Buttons:** We wanted the buttons to control the speeds, so, depending on which button you pressed, the overall robot speed would be regulated (except the POV's).
- **Analogs:** The left analog, unlike some built-ins WPILib's functions, was meant to control the robot speed and direction, according to the magnitude and X, Y it was in. 
The right analog, had the same purpose as the left one, but it had to be inverted, so when we were at the competition, we could control the robot independently from the direction it was in.
- **Triggers:** The triggers were quite simple, the left one had to make the robot faster depending on how much you are pressing it, going backwards, and the right one did the same thing, going forward.
- **POV:** The POV, as expected, would just control the motors directions, changing depending on which direction it is pointed to.

<br>

---
## 👨‍💻 Code's logic

As you can see in the first lines of the **TimedRobot class**, I created all the variables we would use — mainly, I instantiated the motors and the joystick,
which would be responsible for reading the POV and button inputs.

Inside the **robotInit function** — which is a run-once function — I set the second motor on each side to follow the first one, ensuring uniform movement. 
Due to our robot’s electrical configuration, I inverted the right motor. I also configured a deadband, so that small unintended speeds wouldn't activate the motors.

The **teleopPeriodic function** — which runs in a loop — begins by reading the joystick's X and Y axes, button inputs, trigger values, the POV angle, magnitudes, and
sine values. Then, I implemented speed control through booleans attached to specific buttons. When pressed, they set the overall speed to 0.25, 0.5, or 1.0, 
depending on which button was triggered.

Furthermore, I created a **CalcPOV** function that treats the POV like a unit circle. Each direction case corresponds to a 40% motor speed (0.40), allowing the robot
to move in the chosen direction.

I also implemented a **CalcTrigs function**, which multiplies the current trigger input by the speed factor. This approach works in accordance with how WPILib handles
triggers.

Finally, I developed two functions that operate very similarly: the **left and right analog**. The underlying logic for both analog sticks is essentially a mathematical
basic function [...] 

/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Servo-Controller", group="Linear OpMode")
public class ServoControllerExample extends LinearOpMode {
    INA3221 servoController;
    ElapsedTime timer = new ElapsedTime();
    @Override
    public void runOpMode() {
        servoController = hardwareMap.get(INA3221.class, "servoController");
        Servo servoOnChannel1 = hardwareMap.get(Servo.class, "servo1");
        // run until the end of the match (driver presses STOP)

        servoController.reset();

        waitForStart();

        while(!servoController.isConnected()) {
            telemetry.addData("Connection status", servoController.getDeviceId());
            telemetry.update();
            if(gamepad1.a) break;
        }


        timer.reset();

        while (opModeIsActive()) {

            if(gamepad1.a) servoOnChannel1.setPosition(1);
            else servoOnChannel1.setPosition(0);

            double[] busVoltage = new double[3];
            double[] shuntVoltage = new double[3];
            double[] current = new double[3];
            for(INA3221.CHANNEL channel : INA3221.CHANNEL.values())
            {
                int channelInt = channel.ordinal();
                busVoltage[channelInt] = servoController.getBusVoltage(channel);
                shuntVoltage[channelInt] = servoController.getShuntVoltage(channel);
                current[channelInt] = servoController.getCurrent(channel);

                telemetry.addData("Bus Voltage", " (%d): %.2f", channelInt + 1, busVoltage[channelInt]);
                telemetry.addData("Shunt Voltage", " (%d): %.2f", channelInt + 1, shuntVoltage[channelInt]);
                telemetry.addData("Current", " (%d): %.2f", channelInt + 1, current[channelInt]);
            }
            telemetry.addData("Elapsed Time", timer.time());
            telemetry.update();
        }
    }
}

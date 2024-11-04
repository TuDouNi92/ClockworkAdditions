package com.github.guyapooye.clockworkadditions.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CWAGyro extends ConfigBase {
    public final ConfigFloat sensitivityToRotation = f(0.5F,0,"sensitivityToRotation", Comments.gyroR);
    public final ConfigFloat sensitivityToOmega = f(0.5F,0,"sensitivityToAngularVelocity", Comments.gyroO);
    public final ConfigFloat P=f(1.0F,0,"pInPID");
    public final ConfigFloat I=f(1.0F,0,"iInPID");
    public final ConfigFloat D=f(20.0F,0,"dInPID");
    @Override
    public String getName() {
        return "gyroscopicCircuit";
    }
    private static class Comments {
        static String gyroR = "Determines how sensitive the Redstone Gyro is to tilt";
        static String gyroO = "Determines how sensitive the Redstone Gyro is to angular velocity";
    }
}

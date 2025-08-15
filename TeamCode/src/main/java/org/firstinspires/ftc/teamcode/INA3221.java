package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.util.TypeConversion.byteArrayToInt;

import com.qualcomm.hardware.lynx.LynxI2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteOrder;

@I2cDeviceType
@DeviceProperties(
        name = "Servo-Controller",
        xmlTag = "ServoControllerINA3221",
        description ="ServoController with INA3221 for detects grap"
)
public class INA3221 extends I2cDeviceSynchDevice<I2cDeviceSynchSimple> {

    private final Byte I2C_ADDRESS_GND = 0x40;

    public enum CHANNEL {
        CHANNEL_1,
        CHANNEL_2,
        CHANNEL_3;
    }

    public enum AVG_SAMPLES {

    }

    public INA3221(I2cDeviceSynchSimple deviceClient, boolean deviceClientIsOwned)
    {
        super(deviceClient, deviceClientIsOwned);

        this.deviceClient.setI2cAddress(I2cAddr.create7bit(I2C_ADDRESS_GND));
        super.registerArmingStateCallback(false);
    }

    @Override
    protected boolean doInitialize() {
        ((LynxI2cDeviceSynch)(deviceClient)).setBusSpeed(LynxI2cDeviceSynch.BusSpeed.FAST_400K);
        return true;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "Servo-Controller";
    }

    public void reset() {
        setConfiguration((1 << 15));
    }

    public void enableChannel(CHANNEL channel, boolean enabled) {
        int configEnabledChannels = getConfiguration();
        switch(channel) {
            case CHANNEL_1:
                if(enabled)
                    configEnabledChannels |= (1 << 14);
                else
                    configEnabledChannels &= 0xBFFF;
                break;
            case CHANNEL_2:
                if(enabled)
                    configEnabledChannels |= (1 << 13);
                else
                    configEnabledChannels &= 0xDFFF;
                break;
            case CHANNEL_3:
                if(enabled)
                    configEnabledChannels |= (1 << 12);
                else
                    configEnabledChannels &= 0xEFFF;
                break;
            default:
                return;
        }

        setConfiguration(configEnabledChannels);
    }

    private int getConfiguration() {
        return readInt(RegisterMaps.CONFIGURATION);
    }

    private void setConfiguration(int configuration) {
        writeInt(RegisterMaps.CONFIGURATION, configuration);
    }

    private void writeInt(final RegisterMaps reg, int i){
        deviceClient.write(reg.getAddress(), TypeConversion.intToByteArray(i,ByteOrder.BIG_ENDIAN));
    }

    private int readInt(RegisterMaps reg){
        return byteArrayToInt(deviceClient.read(reg.getAddress(),4), ByteOrder.BIG_ENDIAN);
    }
}

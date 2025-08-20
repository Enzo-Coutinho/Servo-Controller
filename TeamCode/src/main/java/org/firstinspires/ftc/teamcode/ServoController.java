package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.util.TypeConversion.byteArrayToShort;
import static com.qualcomm.robotcore.util.TypeConversion.unsignedShortToInt;

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
public class ServoController extends I2cDeviceSynchDevice<I2cDeviceSynchSimple> {

    private final Byte I2C_ADDRESS_GND = 0x40;

    private final double[] SHUNT_RESISTORS = {0.1, 0.1, 0.1};
    private double[] currentLimit = new double[3];

    public enum CHANNEL {
        CHANNEL_1,
        CHANNEL_2,
        CHANNEL_3
    }

    public enum AVG_SAMPLES {
        AVG_1,
        AVG_4,
        AVG_16,
        AVG_64,
        AVG_128,
        AVG_256,
        AVG_512,
        AVG_1024
    }

    public enum CONVERSION_TIMES {
        _140_US,
        _204_US,
        _322_US,
        _588_US,
        _1_1_MS,
        _2_116_MS,
        _4_156_MS,
        _8_244_MS
    }

    public enum MODES {
        POWER_DOWN(0b000),
        SHUNT_VOLTAGE_SINGLE(0b001),
        BUS_VOLTAGE_SINGLE(0b10),
        SHUNT_AND_BUS_SINGLE(0b011),
        SHUNT_VOLTAGE_CONTINOUS(0b101),
        BUS_VOLTAGE_CONTINUOUS(0b110),
        BUS_SHUNT_CONTINUOUS(0b111);
        public final int value;

        MODES(int value) {
            this.value = value;
        }
    }

    public ServoController(I2cDeviceSynchSimple deviceClient, boolean deviceClientIsOwned)
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
        setConfiguration(getConfiguration() | (1 << 15));
    }

    public boolean isConnected() {
        return (readInt(RegisterMaps.MANUFACTURER_ID) == 0x5449);
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

    private void setAvarageSamples(AVG_SAMPLES avarageSamples) {
        setConfiguration(avarageSamples.ordinal() << 9);
    }

    private void setBusVoltageConversionTime(CONVERSION_TIMES conversionTime) {
        setConfiguration(conversionTime.ordinal() << 6);
    }

    private void setShuntVoltageConversionTime(CONVERSION_TIMES conversionTime) {
        setConfiguration(conversionTime.ordinal() << 3);
    }

    private void setMode(MODES mode) {
        setConfiguration(mode.value);
    }

    public boolean isClose(CHANNEL channel) {
        return (getCurrent(channel) >= currentLimit[channel.ordinal()]);
    }

    public double getBusVoltage(CHANNEL channel) {
        RegisterMaps reg;

        switch (channel) {
            case CHANNEL_1:
                reg = RegisterMaps.BUS_VOLTAGE_CH1;
                break;
            case CHANNEL_2:
                reg = RegisterMaps.BUS_VOLTAGE_CH2;
                break;
            case CHANNEL_3:
                reg = RegisterMaps.BUS_VOLTAGE_CH3;
                break;
            default:
                return 0.0;
        }

        return (readInt(reg) >> 3) * 8e-3;
    }

    public double getCurrent(CHANNEL channel) {
        double shuntVoltage = getShuntVoltage(channel);

        return shuntVoltage / SHUNT_RESISTORS[channel.ordinal()];
    }

    public void setCurrentLimit(CHANNEL channel, double currentLimit) {
        this.currentLimit[channel.ordinal()] = currentLimit;
    }

    public double getCurrentLimit(CHANNEL channel)
    {
        return currentLimit[channel.ordinal()];
    }

    private double getShuntVoltage(CHANNEL channel) {
        RegisterMaps reg;

        switch(channel) {
            case CHANNEL_1:
                reg = RegisterMaps.SHUNT_VOLTAGE_CH1;
                break;
            case CHANNEL_2:
                reg = RegisterMaps.SHUNT_VOLTAGE_CH2;
                break;
            case CHANNEL_3:
                reg = RegisterMaps.SHUNT_VOLTAGE_CH3;
                break;
            default:
                return 0.0;
        }

        return (readInt(reg) >> 3) * 40e-6;
    }

    private int getConfiguration() {
        return readInt(RegisterMaps.CONFIGURATION);
    }

    private void setConfiguration(int configuration) {
        writeInt(RegisterMaps.CONFIGURATION, (short) configuration);
    }

    private void writeInt(final RegisterMaps reg, short i){
        deviceClient.write(reg.getAddress(), TypeConversion.shortToByteArray(i, ByteOrder.BIG_ENDIAN));
    }

    private int readInt(RegisterMaps reg){
        return unsignedShortToInt(byteArrayToShort(deviceClient.read(reg.getAddress(),2), ByteOrder.BIG_ENDIAN));
    }
}

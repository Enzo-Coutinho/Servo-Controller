package org.firstinspires.ftc.teamcode;

public enum RegisterMaps {
    CONFIGURATION(0x00),
    SHUNT_VOLTAGE_CH1(0x01),
    BUS_VOLTAGE_CH1(0x02),
    SHUNT_VOLTAGE_CH2(0x03),
    BUS_VOLTAGE_CH2(0x04),
    SHUNT_VOLTAGE_CH3(0x05),
    BUS_VOLTAGE_CH3(0x06),
    CRITICAL_ALERT_CH1(0x07),
    WARNING_ALERT_CH1(0x08),
    CRITIAL_ALERT_CH2(0x09),
    WARNING_ALTERT_CH2(0x0A),
    CRITIAL_ALERT_CH3(0x0B),
    WARNING_ALTER_CH3(0x0C),
    SHUNT_VOLTAGE_SUM(0x0D),
    SHUNT_VOLTAGE_SUM_LIMIT(0x0E),
    MASK_ENABLE(0x0F),
    POWER_VALID_UPPER_LIMIT(0x10),
    POWER_VALID_LOWER_LIMIT(0X11),
    MANUFACTURER_ID(0xFE),
    DIE_ID(0xFF);

    private final int address;

    RegisterMaps(int address) {
        this.address = address;
    }

    public int getAddress()
    {
        return address;
    }

}

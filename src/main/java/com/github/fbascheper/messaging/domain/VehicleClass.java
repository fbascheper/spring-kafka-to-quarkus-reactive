package com.github.fbascheper.messaging.domain;

/**
 * meetup-gn-spring-pipeline - Description.
 *
 * @author Frederieke Scheper
 * @since 13-10-2021
 */
public enum VehicleClass {

    /**
     * Vehicle class 1 = This vehicle class was used for vehicles with estimated length less than 1 metre,
     * such as motorbikes. The occasional measurements in this vehicle class are unreliable.
     * This data is unused by AWV and the Traffic Center.
     */
    MOTORCYCLE(1, false),

    /**
     * Vehicle class 2: Cars, i.e. vehicles with an estimated length between 1,00 m and 4,90 m
     */
    CAR(2, true),

    /**
     * Vehicle class 3: Vans, i.e. vehicles with an estimated length between 4,90 m and 6,90 m
     */
    MINIVAN(3, true),

    /**
     * Vehicle class 4: Rigid lorries, i.e. vehicles with an estimated length between 6,90 m and 12,00 m
     */
    RIGID_LORRIES(4, true),

    /**
     * Vehicle class 5: (Semi-)Trailers or busses, i.e. vehicles with an estimated length longer than 12,00 m
     */
    TRUCK_OR_BUS(5, true),

    /**
     * Unknown vehicle type, unreliable by definition.
     */
    UNKNOWN(0, false);

    private final int value;
    private final boolean reliable;

    public int getValue() {
        return value;
    }

    public boolean isReliable() {
        return reliable;
    }

    VehicleClass(int value, boolean reliable) {
        this.value = value;
        this.reliable = reliable;
    }


}

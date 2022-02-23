package com.github.fbascheper.messaging.common;

import com.github.fbascheper.messaging.domain.TrafficEvent;
import com.github.fbascheper.messaging.domain.VehicleClass;
import com.github.fbascheper.miv.data.JaxbMivType;

import java.time.ZonedDateTime;
import java.util.Arrays;

/**
 * Converter between JAXB {@code XML} and the TrafficData domain.
 *
 * @author Frederieke Scheper
 * @since 15-10-2021
 */
public class TrafficDataConverter {

    /**
     * Construct a {@link TrafficEvent}-instance from its XML counterpart from JAXB.
     *
     * @param meetpunt JAXB element
     * @return a {@link TrafficEvent}-instance
     */
    public static TrafficEvent ofMeasurement(JaxbMivType.JaxbMeetpunt meetpunt, JaxbMivType.JaxbMeetpunt.JaxbMeetdata meetdata) {

        ZonedDateTime timeRegistration = meetpunt.getTijdWaarneming().toGregorianCalendar().toZonedDateTime();

        Integer sensorId = Integer.valueOf(meetpunt.getUniekeId());
        String sensorDescriptiveId = meetpunt.getBeschrijvendeId();
        boolean sensorAvailable = meetpunt.getBeschikbaar() == 1;
        boolean sensorDataRecent = meetpunt.getActueelPublicatie() == 1;
        ZonedDateTime lastUpdated = meetpunt.getTijdLaatstGewijzigd().toGregorianCalendar().toZonedDateTime();

        VehicleClass vehicleClass = getVehicleClassFromMeetData(meetdata);
        int trafficIntensity = meetdata.getVerkeersintensiteit();
        int vehicleCalculatedSpeed = meetdata.getVoertuigsnelheidRekenkundig();
        int vehicleHarmonicSpeed = meetdata.getVoertuigsnelheidHarmonisch();

        return new TrafficEvent(timeRegistration, sensorId, sensorDescriptiveId, sensorAvailable, sensorDataRecent, lastUpdated
                , vehicleClass, trafficIntensity, vehicleCalculatedSpeed, vehicleHarmonicSpeed);
    }

    private static VehicleClass getVehicleClassFromMeetData(JaxbMivType.JaxbMeetpunt.JaxbMeetdata meetdata) {
        return Arrays.stream(VehicleClass.values())
                .filter(e -> e.getValue() == meetdata.getKlasseId())
                .findFirst()
                .orElse(VehicleClass.UNKNOWN);
    }

}

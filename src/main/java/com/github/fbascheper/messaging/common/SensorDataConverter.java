package com.github.fbascheper.messaging.common;

import com.github.fbascheper.messaging.domain.GeographicCoordinates;
import com.github.fbascheper.messaging.domain.TrafficSensor;
import com.github.fbascheper.miv.config.JaxbTMeetpunt;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Converter between JAXB {@code XML} and the SensorData domain.
 *
 * @author Frederieke Scheper
 * @since 15-10-2021
 */
public class SensorDataConverter {

    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("nl-BE"));
    private static final String DECIMAL_FORMAT_PATTERN = "#,##0.###";

    /**
     * Construct a {@link TrafficSensor}-instance from its XML counterpart from JAXB.
     *
     * @param meetpunt JAXB element
     * @return a {@link TrafficSensor}-instance
     */
    public static TrafficSensor ofMeasurementPoint(JaxbTMeetpunt meetpunt) {

        var uniqueId = Integer.valueOf(meetpunt.getUniekeId());
        var descriptiveId = meetpunt.getBeschrijvendeId();
        var name = meetpunt.getVolledigeNaam();
        var ident8 = meetpunt.getIdent8();
        var trafficLane = meetpunt.getRijstrook();
        var longitude = toBigDecimal(meetpunt.getLengtegraadEPSG4326());
        var latitude = toBigDecimal(meetpunt.getBreedtegraadEPSG4326());

        var GeographicCoordinates = new GeographicCoordinates(longitude, latitude);

        return new TrafficSensor(uniqueId
                , descriptiveId
                , name
                , ident8
                , trafficLane
                , GeographicCoordinates);
    }

    private static BigDecimal toBigDecimal(String number) {
        var decimalFormat = new DecimalFormat(DECIMAL_FORMAT_PATTERN, DECIMAL_FORMAT_SYMBOLS);
        decimalFormat.setParseBigDecimal(true);

        BigDecimal bigDecimal;
        try {
            bigDecimal = (BigDecimal) decimalFormat.parse(number);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Exception convertion string to big decimal", e);
        }

        return bigDecimal;
    }
}

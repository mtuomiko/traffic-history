package net.mtuomiko.traffichistory.function.tms;

import java.util.List;

public record TmsStationResponse(List<Feature> features) {

    public record Feature(
            Geometry geometry,
            Properties properties
    ) {
    }

    public record Geometry(
            List<Double> coordinates
    ) {
    }

    public record Properties(
            Integer id,
            Integer tmsNumber,
            String name
    ) {
    }
}

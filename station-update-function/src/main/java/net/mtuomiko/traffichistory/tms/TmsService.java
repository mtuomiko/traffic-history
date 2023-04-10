package net.mtuomiko.traffichistory.tms;

import net.mtuomiko.traffichistory.StationEntity;
import net.mtuomiko.traffichistory.UpdateStationsV1;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TmsService {

    private static final Logger logger = Logger.getLogger(TmsService.class.getName());

    TmsConfig tmsConfig;

    TmsStationClient tmsStationClient;

    public TmsService(TmsConfig tmsConfig) {
        this.tmsStationClient = RestClientBuilder.newBuilder()
                .baseUri(URI.create(tmsConfig.stationApiUrl()))
                .register(GZIPDecodingInterceptor.class)
                .register(AcceptEncodingGZIPFilter.class)
                .build(TmsStationClient.class);
    }

    public List<StationEntity> fetchStations() {
        logger.info("Fetching stations from TMS");
        var response = tmsStationClient.getStations();
        return response.features().stream()
                .map(feature -> new StationEntity(
                        feature.properties().name(),
                        feature.properties().id(),
                        feature.properties().tmsNumber(),
                        feature.geometry().coordinates().get(1),
                        feature.geometry().coordinates().get(0)
                ))
                .toList();
    }
}

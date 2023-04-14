package net.mtuomiko.traffichistory.common.model;

/**
 * We need both for handling a special case where stations with negative tmsNumber must have their TMS CSV files
 * accessed using the id number.
 */
public record StationIdentity(
        int tmsId,
        int tmsNumber
) {
}

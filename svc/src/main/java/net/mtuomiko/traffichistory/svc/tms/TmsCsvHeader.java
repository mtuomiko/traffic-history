package net.mtuomiko.traffichistory.svc.tms;

/**
 * See https://www.digitraffic.fi/en/road-traffic/lam/ or https://www.digitraffic.fi/tieliikenne/lam/
 *
 * Currently only interested in limited columns, nothing more detailed.
 */
public enum TmsCsvHeader {
    ID, YEAR, DAY, HOUR, MINUTE, SECOND, SECOND_100TH, LENGTH, LANE, DIRECTION, CLASS, SPEED, FAULTY, TIME, DURATION,
    QUEUE
}

import csv
import datetime
import os
import random
import requests
import time


class Target:
    def __init__(self, name, url):
        self.name = name
        self.url = url


minimum_delay_seconds = 4400
maximum_random_increase = 900
results_filename = "cold_start_results.csv"
targets = [
    Target("native", "https://traffichistoryapi-uifnht7hhq-ey.a.run.app"),
    Target("jvm", "https://traffichistoryapi-jvm-uifnht7hhq-ey.a.run.app"),
    Target("temurin", "https://traffichistoryapi-temurin-uifnht7hhq-ey.a.run.app"),
    Target("app-engine", "https://traffic-history-376700.ey.r.appspot.com")
]
endpoint_health = "/health"
endpoint_stations = "/station"
target_endpoint = endpoint_health



def get_delay():
    random_increase = random.randint(0, maximum_random_increase)
    return minimum_delay_seconds + random_increase


def timedelta_to_millisecond_string(duration: datetime.timedelta):
    return str(round(duration.total_seconds() * 1000))


def convert_to_dict_result_row(
    time: datetime.datetime, results: list[tuple[str, datetime.timedelta]]
):
    result = {k: timedelta_to_millisecond_string(v) for k, v in results}
    result["time"] = time.strftime("%Y-%m-%d%H:%M:%S")
    return result


def write_to_csv(results: dict[str, str]):
    fieldnames = ["time", "native", "jvm", "temurin", "app-engine"]
    with open(results_filename, "a", newline="") as csvfile:
        resultwriter = csv.DictWriter(csvfile, fieldnames=fieldnames)
        resultwriter.writerow(results)


def measure_all_response_times():
    for target in targets:
        response = requests.get(f'{target.url}{target_endpoint}')
        yield (target.name, response.elapsed)


def measure():
    now = datetime.datetime.now()
    results = list(measure_all_response_times())
    formatted_results = convert_to_dict_result_row(now, results)
    write_to_csv(formatted_results)
    print(formatted_results)


if __name__ == "__main__":
    print(f'Starting cold start measurements for "{target_endpoint}" endpoints')
    measure()

    while True:
        delay = get_delay()
        next_run_time = datetime.datetime.now() + datetime.timedelta(0, delay)
        print(f'Waiting for {delay} seconds. Next run at {next_run_time.strftime("%Y-%m-%d%H:%M:%S")}')
        time.sleep(delay)
        measure()

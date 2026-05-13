import os

def measure_time():
    total_ts = 0
    total_tj = 0
    samples = 0

    with open(os.getenv("LOGFILE_PATH")) as file:
        for line in file:
            ts, tj = parse_line(line)
            total_ts += ts
            total_tj += tj
            samples += 1

    average_ts = total_ts / samples
    average_tj = total_tj / samples

    print(f"Average TS: {average_ts}")
    print(f"Average TJ: {average_tj}")


def parse_line(line):
    ts, tj = line.split(",")
    return float(ts[3:]), float(tj[3:])


if __name__ == "__main__":
    measure_time()
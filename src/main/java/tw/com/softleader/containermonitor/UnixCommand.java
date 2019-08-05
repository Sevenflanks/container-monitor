package tw.com.softleader.containermonitor;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class UnixCommand implements Command {
	@Override
	public List<String> curlJvmMetrics(@NonNull String containerId) {
		return Arrays.asList("/bin/sh", "-c",
				"docker exec " + containerId + " -H '" + AUTH + "' curl localhost:8080/metrics -s");
	}

	@Override
	public List<String> dockerPs() {
		return Arrays.asList("/bin/sh", "-c",
				"docker ps --format {{.ID}}"+S+"{{.Image}}"+S+"{{.Networks}}");
	}

	@Override public List<String> dockerImageLs() {
		return Arrays.asList("/bin/sh", "-c",
				"docker image ls --format {{.ID}}"+S+"{{.Repository}}:{{.Tag}}");
	}

	@Override
	public List<String> dockerStats() {
		return Arrays.asList("/bin/sh", "-c",
				"docker stats --no-stream --format {{.ID}}"+S+"{{.Name}}"+S+"{{.CPUPerc}}"+S+"{{.MemUsage}}"+S+"{{.NetIO}}"+S+"{{.BlockIO}}");
	}
}

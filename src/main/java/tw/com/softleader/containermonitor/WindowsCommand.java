package tw.com.softleader.containermonitor;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class WindowsCommand implements Command {
	@Override
	public List<String> curlJvmMetrics(@NonNull String containerId) {
        return Arrays.asList("cmd", "/c",
		    "docker", "exec", containerId, "curl", "localhost:8080/metrics");
	}

	@Override
	public List<String> dockerPs() {
		return Arrays.asList("cmd", "/c",
				"docker", "ps",
				"--format", "{{.ID}}"+S+"{{.Image}}"+S+"{{.Networks}}");
	}

	@Override public List<String> dockerImageLs() {
		return Arrays.asList("cmd", "/c",
				"docker", "image", "ls",
				"--format", "{{.ID}}"+S+"{{.Repository}}:{{.Tag}}");
	}

	@Override
	public List<String> dockerStats() {
		return Arrays.asList("cmd", "/c",
				"docker", "stats",
				"--no-stream",
				"--format", "{{.ID}}"+S+"{{.Name}}"+S+"{{.CPUPerc}}"+S+"{{.MemUsage}}"+S+"{{.NetIO}}"+S+"{{.BlockIO}}");
	}
}

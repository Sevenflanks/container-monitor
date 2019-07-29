package tw.com.softleader.containermonitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BytesUtils {

  private static final Pattern LAYOUT_PATTERN = Pattern.compile("([\\d.]+)(\\w+)");
  private static final double B = 1;
  private static final double KiB = 1024 * B;
  private static final double MiB = 1024 * KiB;
  private static final double GiB = 1024 * MiB;
  private static final double TiB = 1024 * GiB;
  private static final double PiB = 1024 * TiB;
  private static final double KB = 1000 * B;
  private static final double MB = 1000 * KB;
  private static final double GB = 1000 * MB;
  private static final double TB = 1000 * GB;
  private static final double PB = 1000 * TB;


  public static double toB(String input) {
    Matcher matcher = LAYOUT_PATTERN.matcher(input);
    if (matcher.matches()) {
      Double size = Double.valueOf(matcher.group(1));
      String unit = matcher.group(2);

      if (unit.contains("i")) {
        switch (unit) {
          case "PiB": return size * PiB;
          case "TiB": return size * TiB;
          case "GiB": return size * GiB;
          case "MiB": return size * MiB;
          case "KiB": return size * KiB;
          case "kiB": return size * KiB;
          default: throw new RuntimeException("unsupport unit: " + unit);
        }
      } else {
        switch (unit) {
          case "PB": return size * PB;
          case "TB": return size * TB;
          case "GB": return size * GB;
          case "MB": return size * MB;
          case "KB": return size * KB;
          case "kB": return size * KB;
          case "B": return size * B;
          default: throw new RuntimeException("unsupport unit: " + unit);
        }
      }

    } else {
      throw new RuntimeException("unsupport input: " + input);
    }
  }

}

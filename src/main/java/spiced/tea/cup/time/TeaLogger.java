package spiced.tea.cup.time;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TeaLogger {

	private String userHome = System.getProperty("user.home");
	private File logFile = new File(userHome + "/.config/teabot-spice/teabot-spice.log");
	private FileWriter writer;

	public TeaLogger() {

	}

	public void log(String log) {

		try {

			if (logFile.exists()) {

				writer = new FileWriter(userHome
						+ "/.config/teabot-spice/teabot-spice.log", true);
				writer.append(log + "\n");
				writer.close();

			} else {

				logFile.getParentFile().mkdir();
				logFile.createNewFile();

				if (logFile.exists()) {

					writer = new FileWriter(userHome
							+ "/.config/teabot-spice/teabot-spice.log", true);
					writer.append(log + "\n");
					writer.close();
				} else {
					System.out
							.println("ERROR: Could not create log file in ~/.config/teabot-spice/teabot-spice.log");
					System.exit(0);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}

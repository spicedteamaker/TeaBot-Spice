package spiced.tea.cup.time;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

public class TeaTray {

	private BufferedImage bufferedTeaIcon = null;
	private PopupMenu teaPopup = new PopupMenu();
	private TrayIcon teaIconTray = null;
	final SystemTray tray = SystemTray.getSystemTray();
	private TeaLogger teaLogger = new TeaLogger();
	private FileWriter writer;
	private String userHome = System.getProperty("user.home");
	private File logFile = new File(userHome + "/.config/teabot-spice/teabot-spice.log");
	private TeaMaker teaMaker = null;
	private TeaServer teaServer = new TeaServer();
	
	public TeaTray() {

	}

	public void beginTeaTray() {

		checkCompatibility();
		loadTeaTrayImage();
		teaIconTray = new TrayIcon(bufferedTeaIcon, "Tea-bot Spice");
		MenuItem exitItem = new MenuItem("Exit");
		MenuItem clearLogFile = new MenuItem("Clear Logs");
		MenuItem nextScheduledTime = new MenuItem("Next Tea Time");
		MenuItem teaMenu = new MenuItem("Menu");
		
		teaPopup.add(nextScheduledTime);
		teaPopup.add(clearLogFile);
		teaPopup.add(teaMenu);
		teaPopup.add(exitItem);
		

		teaIconTray.setPopupMenu(teaPopup);

		try {
			tray.add(teaIconTray);
		} catch (AWTException e) {
			System.out.println("ERROR: Failed to add teaIconTray");
			teaLogger.log("ERROR: Failed to add teaIconTray");
			System.exit(0);
		}

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(teaIconTray);
				System.out
						.println("INFO: Tray item Exit was pressed. Exiting...");
				teaLogger.log("INFO: Tray item Exit was pressed. Exiting...");
				System.exit(0);
			}
		});
		
		clearLogFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					writer = new FileWriter(userHome
							+ "/.config/teabot-spice/teabot-spice.log");
					
					writer.write("INFO: Request from system tray to clear log file...\n");
					writer.append("INFO: Log file cleared\n");
					writer.close();
					
					System.out.println("INFO: Request from system tray to clear log file...");
					System.out.println("INFO: Log file cleared");
				} catch (Exception logException){
					
				}
				
				
			}
		});
		
		nextScheduledTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					teaServer.setCurrentOrderDetails(teaMaker.getCurrentOrderDetails());
					teaServer.remindNextTeaTime();
				} catch (Exception nextScheduledTimeException){
					
				}
			}
		});
		
		teaMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.launchMenu();
			}
		});
	}

	private void checkCompatibility() {

		if (!SystemTray.isSupported()) {
			System.out.println("ERROR: system tray is not supported");
			teaLogger.log("ERROR: system tray is not supported");
			System.exit(0);
		}
	}

	private void loadTeaTrayImage() {

		try {
			// this.bufferedTeaIcon = ImageIO.read(new File(userHome +
			// "/.config/teabot-spice/trayImageDefault.png"));
			this.bufferedTeaIcon = ImageIO.read(getClass().getResourceAsStream(
					"/trayImageDefault.png"));
		} catch (Exception e) {
			System.out.println("WARNING: could not load tray image");
			teaLogger.log("WARNING: could not load tray image");
		}
	}
	
	public void setTeaMaker(TeaMaker teaMaker){
		this.teaMaker = teaMaker;
	}
	

}

package spiced.tea.cup.time;

import java.time.LocalTime;

class TeaClock implements Runnable {
	
	// TODO add an updateImmediately() function for when changes are made

	// TODO what is variable thread doing?
	private Thread thread;
	private String clockName;
	private TeaMaker currentBarista;
	private LocalTime localTime;

	private TeaLogger teaLogger = new TeaLogger();

	TeaClock(String clockName, TeaMaker currentBarista) {
		this.clockName = clockName;
		this.currentBarista = currentBarista;
		System.out.println("INFO: " + clockName + " has been created");
		teaLogger.log("INFO: " + clockName + " has been created");
		System.out.println("INFO: current barista under this clock: "
				+ currentBarista.getBaristaName());
		teaLogger.log("INFO: current barista under this clock: "
				+ currentBarista.getBaristaName());

	}

	public void run() {
		System.out.println("INFO: " + clockName + " is running");
		teaLogger.log("INFO: " + clockName + " is running");

		while (true) {
			localTime = LocalTime.now();
			localTime = formatLocalTime(localTime);
			currentBarista.setLocalTime(localTime);
			System.out.println("INFO: current time: " + localTime);
			teaLogger.log("INFO: current time: " + localTime);
			currentBarista.checkTeaTime();
			currentBarista.performNextTeaTimeLogic();

			System.out.println("=-=-= Next Tea Time: =-=-=");
			printNextOrderDetailsFromBarista();

			try {
				// update the localtime every minute
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				System.out.println("ERROR: TeaClock thread failed");
				teaLogger.log("ERROR: TeaClock thread failed");
				e.printStackTrace();
			}
		}

	}

	public void start() {
		System.out.println("INFO: " + clockName + " has started");
		teaLogger.log("INFO: " + clockName + " has started");

		// TODO learn why you add this if conditional
		if (thread == null) {
			thread = new Thread(this, clockName);
			thread.start();
		}
	}

	public LocalTime formatLocalTime(LocalTime localTime) {

		if (localTime.getHour() < 10 && localTime.getMinute() < 10) {
			return (LocalTime.parse("0" + localTime.getHour() + ":" + "0"
					+ localTime.getMinute()));
		} else if (localTime.getHour() < 10) {
			return LocalTime.parse("0" + localTime.getHour() + ":"
					+ localTime.getMinute());
		} else if (localTime.getMinute() < 10) {
			return LocalTime.parse(localTime.getHour() + ":" + "0"
					+ localTime.getMinute());
		} else {
			return LocalTime.parse(localTime.getHour() + ":"
					+ localTime.getMinute());
		}

	}

	public void printNextOrderDetailsFromBarista() {
		System.out.println(currentBarista.getCurrentOrderDetails());
		teaLogger.log(currentBarista.getCurrentOrderDetails());
	}
	
	public void setBarista(TeaMaker teaMaker) {
		currentBarista = teaMaker;
	}

}

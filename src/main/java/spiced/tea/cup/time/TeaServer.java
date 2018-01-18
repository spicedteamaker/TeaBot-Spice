package spiced.tea.cup.time;

public class TeaServer {

	private String currentOrderDetails;

	private ProcessBuilder notifyUser;

	public TeaServer() {

		this.currentOrderDetails = "WARNING: Unknown tea order details";

	}

	public TeaServer(String currentOrderDetails) {
		this.currentOrderDetails = (currentOrderDetails);
	}

	public void serveTea() {

		try {

			notifyUser = new ProcessBuilder("/usr/bin/notify-send",
					"=-=-= Tea Time! =-=-=\n" + currentOrderDetails);
			notifyUser.start();

		} catch (Exception e) {

		}
	}
	
	public void remindNextTeaTime() {
		
		try {
		notifyUser = new ProcessBuilder("/usr/bin/notify-send",
				"=-=-= Next Scheduled Time: =-=-=\n" + currentOrderDetails);
		
		notifyUser.start();
		
		} catch (Exception e) {
			
		}
	}
	
	public void setCurrentOrderDetails(String currentOrderDetails) {
		this.currentOrderDetails = currentOrderDetails;
	}

}

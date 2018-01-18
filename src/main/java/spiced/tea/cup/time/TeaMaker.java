package spiced.tea.cup.time;

import java.time.LocalTime;
import java.util.List;

public class TeaMaker {
	

	private List<TeaOrder> currentOrders;
	private String baristaName = "Bernd";
	private LocalTime localTime;
	private StringBuilder orderStringBuilder = new StringBuilder();
	private String currentOrderDetails = "No Active Order";
	private boolean isWorking = false;
	private LocalTime nextTeaTime;

	private TeaServer waitress;
	private TeaLogger teaLogger = new TeaLogger();

	int nextTeaTimeHour;
	int nextTeaTimeMinute;

	public TeaMaker(List<TeaOrder> currentOrders) {
		this(currentOrders, "Bernd");
		isWorking = true;
	}

	public TeaMaker(List<TeaOrder> currentOrders, String baristaName) {
		this.currentOrders = currentOrders;
		this.baristaName = baristaName;
		isWorking = true;

	}

	public String getOrderDetailsToString(int orderNumber) {

		orderStringBuilder = new StringBuilder();

		if (orderNumber > currentOrders.size() - 1) {
			System.out.println("ERROR: Invalid order number received");
			teaLogger.log("ERROR: Invalid order number received");
			System.exit(0);
		}
		
		orderStringBuilder.append("Order: ");
		orderStringBuilder
				.append(currentOrders.get(orderNumber).getOrderName());
		orderStringBuilder.append("\n");
		orderStringBuilder.append("Scheduled Tea Time: ");
		orderStringBuilder.append(currentOrders.get(orderNumber).getTeaTime());
		orderStringBuilder.append("\n\n");

		return orderStringBuilder.toString();

	}

	public int getOrderNumberFromName(String orderName) {
		int i = 0;
		for (TeaOrder order : currentOrders) {
			if (order.getOrderName() == orderName) {
				return i;
			}
			i++;
		}

		System.out
				.println("WARNING: tea OrderName not found in function getOrderNumberFromName. Using default order 0");
		teaLogger
				.log("WARNING: tea OrderName not found in function getOrderNumberFromName. Using default order 0");
		return 0;

	}

	public String getBaristaName() {
		return baristaName;
	}

	public String getStringTime() {
		return ("time: " + localTime);
	}

	public List<TeaOrder> getActiveOrders() {
		return currentOrders;
	}

	public void performNextTeaTimeLogic() {

		LocalTime checkTime = currentOrders.get(0).getTeaTime();
		LocalTime soonestTimeInAllOrders = currentOrders.get(0).getTeaTime();

		int currentOrderIndex = 0;
		int earliestOrderIndex = 0;
		int allBeforeLocalCounter = 0;
		int soonestTimeInAllOrdersIndex = 0;

		boolean firstRun = true;
		boolean allTimesTomorrow = false;

		// we're going to do a quick runby and figure out which ones are
		// after the local time, just to set the checkTime for later checks
		// we also check to see if all the times are before localTime
		// so that the times will be considered tomorrow
		if (firstRun) {
			firstRun = false;

			for (TeaOrder firstRunOrder : currentOrders) {
				currentOrderIndex = getOrderNumberFromName(firstRunOrder
						.getOrderName());
				System.out
						.println("INFO: Handling order: " + currentOrderIndex);
				teaLogger.log("INFO: Handling order: " + currentOrderIndex);
				if (firstRunOrder.getTeaTime().isAfter(localTime)) {
					checkTime = firstRunOrder.getTeaTime();
					System.out.println("INFO: initial run order "
							+ firstRunOrder.getTeaTime() + " is after "
							+ localTime);

					teaLogger.log("INFO: initial run order "
							+ firstRunOrder.getTeaTime() + " is after "
							+ localTime);
					earliestOrderIndex = getOrderNumberFromName(firstRunOrder
							.getOrderName());

				} else {
					// if the time is before localtime, let's increment a list
					// later we'll check if ALL teaTimes are before local time
					// this implies that all the times must be tomorrow
					allBeforeLocalCounter++;
					if (firstRunOrder.getTeaTime().isBefore(
							soonestTimeInAllOrders)) {
						soonestTimeInAllOrders = firstRunOrder.getTeaTime();
						soonestTimeInAllOrdersIndex = getOrderNumberFromName((firstRunOrder
								.getOrderName()));

					}

					if (allBeforeLocalCounter == currentOrders.size()) {
						System.out
								.println("INFO: No tea times after local time detected for today");
						teaLogger
								.log("INFO: No tea times after local time detected for today");
						System.out
								.println("INFO: All tea times are scheduled for tomorrow");
						teaLogger
								.log("INFO: All tea times are scheduled for tomorrow");
						allTimesTomorrow = true;
					}
				}

			}

			System.out.println("INFO: End of initial order runthrough");
			teaLogger.log("INFO: End of initial order runthrough");
		}

		if (allTimesTomorrow == false) {

			System.out
					.println("INFO: Rechecking orders based on previous findings");
			teaLogger.log("INFO: Rechecking orders based on previous findings");
			for (TeaOrder order : currentOrders) {

				currentOrderIndex = getOrderNumberFromName(order.getOrderName());
				System.out
						.println("INFO: Handling order: " + currentOrderIndex);
				teaLogger.log("INFO: Handling order: " + currentOrderIndex);
				if (order.getTeaTime().isAfter(localTime)) {
					if (order.getTeaTime().isBefore(checkTime)) {
						System.out
								.println("INFO: found new time: "
										+ order.getTeaTime()
										+ " is earlier than original time "
										+ checkTime);
						teaLogger
								.log("INFO: found new time: "
										+ order.getTeaTime()
										+ " is earlier than original time "
										+ checkTime);
						checkTime = order.getTeaTime();
						earliestOrderIndex = getOrderNumberFromName(order
								.getOrderName());
					} else {
						System.out.println("INFO: " + order.getTeaTime()
								+ " is after " + checkTime
								+ ". Disregarding...");
						teaLogger.log("INFO: " + order.getTeaTime()
								+ " is after " + checkTime
								+ ". Disregarding...");
					}

				} else {
					System.out.println("INFO: " + order.getTeaTime()
							+ " is not after local time " + localTime
							+ ". Disregarding...");
					teaLogger.log("INFO: " + order.getTeaTime()
							+ " is not after local time " + localTime
							+ ". Disregarding...");
				}

			}

			currentOrderDetails = getOrderDetailsToString(earliestOrderIndex);
			this.nextTeaTime = checkTime;

		} else {
			currentOrderDetails = getOrderDetailsToString(soonestTimeInAllOrdersIndex);
			this.nextTeaTime = soonestTimeInAllOrders;
		}

	}

	public void checkTeaTime() {
		if (localTime.equals(nextTeaTime)) {
			waitress = new TeaServer(currentOrderDetails);
			waitress.serveTea();
		} else {
			System.out.println("INFO: It's not tea time yet.");
			teaLogger.log("INFO: It's not tea time yet.");
		}
	}

	public void setNextTeaTime(LocalTime nextTeaTime) {

		this.nextTeaTime = nextTeaTime;
	}

	public LocalTime getNextTeaTime() {
		return this.nextTeaTime;
	}

	public String getCurrentOrderDetails() {
		return currentOrderDetails;
	}

	public void setLocalTime(LocalTime localTime) {
		this.localTime = localTime;
	}

}

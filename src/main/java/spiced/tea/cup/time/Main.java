package spiced.tea.cup.time;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class Main extends Application {

	private static TeaMaker baristaBernd;
	private static String userHome = System.getProperty("user.home");
	private static File configFile = new File(userHome + "/.config/teabot-spice/config");
	private static Gson gson = new Gson();
	private static List<TeaOrder> openOrders = new ArrayList<TeaOrder>();
	private static TeaTray teaTray = new TeaTray();
	private static TeaClock clock;
	private static TeaLogger teaLogger = new TeaLogger();
	private static Stage stage = new Stage();

	// note - you need to take this extra step when you're creating
	// collections from json. Basically:
	// We create a collectionType of type Type as a new Type Token
	// of an ArrayList that holds TeaOrders.
	// tl;dr magic google type code
	private static Type collectionType = new TypeToken<ArrayList<TeaOrder>>() {
	}.getType();

	public static void main(String[] args) {

		System.out.println("Opening Teahouse...");
		teaLogger.log("Opening Teahouse...");

		// TODO read up on standards for configuration files on linux
		// TODO read up on standards for log files on linux
		// TODO fix the hardcode of the ~/.config file (VERY BAD)
		// https://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html

		// TODO setup Maven

		// TODO consider only declaring openOrders and wrap the initialize in
		// the try/catch

		String allTeaOrders;
		LocalTime defaultTeaTime;
		TeaOrder defaultOrder;
		String defaultJson;

		System.out.println("INFO: Log file can be found in $HOME/.config/teaot-spice/teabot-spice.log");

		try {
			// TODO test config file with messed up white space to ensure
			// compatibility

			if (configFile.exists()) {

				System.out.println("INFO: Found config file in ~/.config/teabot-spice/config");
				teaLogger.log("INFO: Found config file in ~/.config/teabot-spice/config");
				allTeaOrders = new String(Files.readAllBytes(Paths.get(userHome + "/.config/teabot-spice/config")));

				openOrders = gson.fromJson(allTeaOrders, collectionType);

			} else {
				// create default config file in ~/.config/teabot-spice/
				// create default json object for default user setting
				// write to file
				// then read

				System.out.println("INFO: Attempting to create config file in ~/.config/teabot-spice/config");
				teaLogger.log("INFO: Attempting to create config file in ~/.config/teabot-spice/config");
				configFile.getParentFile().mkdir();
				configFile.createNewFile();

				if (configFile.exists()) {

					System.out.println("INFO: config file creation successful");
					teaLogger.log("INFO: config file creation successful");

					defaultTeaTime = LocalTime.of(12, 30, 0, 0);
					defaultOrder = new TeaOrder("Default Order", defaultTeaTime);
					defaultJson = gson.toJson(defaultOrder);

					FileWriter writer = new FileWriter(userHome + "/.config/teabot-spice/config");
					writer.write("[ " + defaultJson + " ]");
					writer.close();

					allTeaOrders = new String(Files.readAllBytes(Paths.get(userHome + "/.config/teabot-spice/config")));

					openOrders = gson.fromJson(allTeaOrders, collectionType);

				} else {
					System.out.println("ERROR: Could not create config file in ~/.config/teabot-spice/config");
					teaLogger.log("ERROR: Could not create config file in ~/.config/teabot-spice/config");
					System.exit(1);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		baristaBernd = new TeaMaker(openOrders);

		clock = new TeaClock("Initial Clock", baristaBernd);

		clock.start();
		teaTray.beginTeaTray();
		// we're only setting teamaker so teaTray has access to the next TeaTime for
		// printing
		teaTray.setTeaMaker(baristaBernd);

	}

	// Beginning over the GUI handling steps
	@Override
	public void start(final Stage primaryStage) {
				


		// TODO figure out how to open the menu more than once
		// after you close it. It throws an error "cannot call launch more than once"
		
		// pane items
		ListView<Text> orderItem = createMenuOrderItems();
		ListView<Button> editItemButtons = createEditButtonList();
		ListView<Button> deleteItemButtons = createDeleteButtonList();
		Button addItem = new Button("Add New Order");

		addItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				final ObservableList<TextField> obsSecondaryList = FXCollections.observableArrayList();
				final ObservableList<String> obsStringList = FXCollections.observableArrayList();

				ListView<TextField> textField = new ListView<TextField>();
				ListView<String> labelField = new ListView<String>();

				Button submitButton = new Button("Submit");
				submitButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
										
						String orderName = obsSecondaryList.get(0).getText();
						int newHours = Integer.valueOf(obsSecondaryList.get(1).getText());
						int newMinutes = Integer.valueOf(obsSecondaryList.get(2).getText());
						TeaOrder newOrder = createNewTeaOrder(orderName,
						LocalTime.of(newHours, newMinutes, 0, 0));
						addNewTeaOrder(newOrder);
						System.out.println("Added new order at time: " + newHours + ":" + newMinutes);
						teaLogger.log("Added new order at time: " + newHours + ":" + newMinutes);
						Alert alert = new Alert(AlertType.INFORMATION);
						stage.close();
						alert.setTitle("Teabot Info");
						alert.setHeaderText(null);
						alert.setContentText("The new order has been added. Changes will visually appear after menu close");
						alert.showAndWait();
						
					}
				});
				


				obsSecondaryList.add(new TextField());
				obsSecondaryList.add(new TextField());
				obsSecondaryList.add(new TextField());
				textField.setItems(obsSecondaryList);

				obsStringList.add("Name:");
				obsStringList.add("Hour:");
				obsStringList.add("Minute:");
				labelField.setItems(obsStringList);

				BorderPane borderPane = new BorderPane();
				borderPane.setPadding(new Insets(30, 30, 30, 30));
				borderPane.setCenter(textField);
				borderPane.setLeft(labelField);
				borderPane.setBottom(submitButton);

				Scene scene = new Scene(borderPane, 250, 100);

				
				stage.setScene(scene);
				stage.show();
				
			}

		});
		
		Button closeMain = new Button("Close Menu");
		closeMain.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				primaryStage.close();
			}
		});
		
		Button exitProgram = new Button("Exit Program");
		exitProgram.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				System.out.println("Exit Program button clicked. Shutting down...");
				teaLogger.log("Exit Program button clicked. Shutting down...");
				System.exit(0);
			}
		});
		
		ButtonBar bottomBar = new ButtonBar();
		ButtonBar.setButtonData(addItem, ButtonData.LEFT);
		ButtonBar.setButtonData(closeMain, ButtonData.OK_DONE);
		ButtonBar.setButtonData(exitProgram, ButtonData.RIGHT);
		bottomBar.getButtons().addAll(addItem, closeMain, exitProgram);

		Label teaListLabel = new Label("Current Orders");

		// info about the pane
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(30, 30, 30, 30));

		// add the items to appear in pane
		borderPane.setTop(teaListLabel);
		borderPane.setLeft(orderItem);
		borderPane.setCenter(editItemButtons);
		borderPane.setRight(deleteItemButtons);
		borderPane.setBottom(bottomBar);

		Scene scene = new Scene(borderPane, 750, 450);
		primaryStage.setScene(scene);
		primaryStage.show();
		// lambda expression to set on close
		// TODO how does this work?
		// primaryStage.setOnCloseRequest(e -> Platform.exit());
	}

	public static void launchMenu() {
		
		Application.launch();

	}

	public ListView<Text> createMenuOrderItems() {

		List<TeaOrder> teaOrders = baristaBernd.getActiveOrders();
		ListView<Text> orderList = new ListView<Text>();
		ObservableList<Text> obsList = FXCollections.observableArrayList();
		

		for (TeaOrder order : teaOrders) {
			Text orderText = new Text();
			orderText.setText(order.getOrderName() + ", " + order.getTeaTime());
			orderText.setFont(Font.font(23));
			obsList.add(orderText);

		}

		orderList.setItems(obsList);
		return orderList;

	}

	public ListView<Button> createEditButtonList() {
		final Stage stage = new Stage();
		List<TeaOrder> teaOrders = baristaBernd.getActiveOrders();
		ListView<Button> buttonList = new ListView<Button>();
		final ObservableList<Button> obsButtonList = FXCollections.observableArrayList();

		for (TeaOrder order : teaOrders) {
			obsButtonList.add(new Button("Edit"));

		}

		for (final Button button : obsButtonList) {
			button.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {

					final ObservableList<TextField> obsSecondaryList = FXCollections.observableArrayList();
					ObservableList<String> obsStringList = FXCollections.observableArrayList();

					ListView<TextField> textField = new ListView<TextField>();
					ListView<String> labelField = new ListView<String>();

					Button submitButton = new Button("Submit");
					submitButton.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							System.out.println("Moddified Order number: " + obsButtonList.indexOf(button));
							teaLogger.log("Moddified Order number: " + obsButtonList.indexOf(button));
							String orderName = obsSecondaryList.get(0).getText();

							System.out.println("the new name was: " + orderName);
							int newHours = Integer.valueOf(obsSecondaryList.get(1).getText());
							int newMinutes = Integer.valueOf(obsSecondaryList.get(2).getText());
							TeaOrder replacementOrder = createNewTeaOrder(orderName,
									LocalTime.of(newHours, newMinutes, 0, 0));
							System.out.println(newHours + " & " + newMinutes);
							replaceTeaOrder(replacementOrder, obsButtonList.indexOf(button));
							stage.close();
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Teabot Info");
							alert.setHeaderText(null);
							alert.setContentText("Order has been edited. Changes will visually appear after menu close");
							alert.showAndWait();
							

						}
					});
					obsSecondaryList.add(new TextField());
					obsSecondaryList.add(new TextField());
					obsSecondaryList.add(new TextField());
					textField.setItems(obsSecondaryList);

					obsStringList.add("Name:");
					obsStringList.add("Hour:");
					obsStringList.add("Minute:");
					labelField.setItems(obsStringList);

					BorderPane borderPane = new BorderPane();
					borderPane.setPadding(new Insets(30, 30, 30, 30));
					borderPane.setCenter(textField);
					borderPane.setLeft(labelField);
					borderPane.setBottom(submitButton);

					Scene scene = new Scene(borderPane, 250, 100);

					
					stage.setScene(scene);
					stage.show();
				}

			});


		}

		buttonList.setItems(obsButtonList);
		return buttonList;
	}

	public ListView<Button> createDeleteButtonList() {
		List<TeaOrder> teaOrders = baristaBernd.getActiveOrders();
		ListView<Button> buttonList = new ListView<Button>();
		final ObservableList<Button> obsButtonList = FXCollections.observableArrayList();

		for (TeaOrder order : teaOrders) {
			obsButtonList.add(new Button("Delete"));

		}

		for (final Button button : obsButtonList) {
			button.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					// TODO add logic to update times
					deleteOrder(obsButtonList.indexOf(button));
					System.out.println("Deleted order number: " + obsButtonList.indexOf(button));
					teaLogger.log("Deleted order number: " + obsButtonList.indexOf(button));
				}
			});
		}

		buttonList.setItems(obsButtonList);
		return buttonList;

	}


	public void replaceTeaOrder(TeaOrder teaOrder, int index) {
		System.out.println("THE ORDER INDEX " + openOrders.indexOf(openOrders.get(index)));
		openOrders.set(openOrders.indexOf(openOrders.get(index)), teaOrder);
		// TODO add a method to TeaMaker to update the orders
		// this will cut down on the code below
		baristaBernd = new TeaMaker(openOrders);
		teaTray.setTeaMaker(baristaBernd);
		clock.setBarista(baristaBernd);
		updateConfigFile();

	}

	public void addNewTeaOrder(TeaOrder teaOrder) {
		
		openOrders.add(teaOrder);
		baristaBernd = new TeaMaker(openOrders);
		teaTray.setTeaMaker(baristaBernd);
		clock.setBarista(baristaBernd);
		updateConfigFile();

	}

	public void deleteOrder(int index) {
		
		openOrders.remove(index);
		baristaBernd = new TeaMaker(openOrders);
		teaTray.setTeaMaker(baristaBernd);
		clock.setBarista(baristaBernd);
		updateConfigFile();

	}

	public TeaOrder createNewTeaOrder(String orderName, LocalTime teaTime) {
		TeaOrder bleep = new TeaOrder(orderName, teaTime);
		return bleep;
	}

	public void updateConfigFile() {
		String updatedOrders = gson.toJson(openOrders);

		try {

			if (configFile.exists()) {

				FileWriter writer = new FileWriter(userHome + "/.config/teabot-spice/config");
				writer.write(updatedOrders);
				writer.close();

			} else {
				System.out.println("ERROR: Could not create config file in ~/.config/teabot-spice/config");
				teaLogger.log("ERROR: Could not create config file in ~/.config/teabot-spice/config");
				System.exit(1);
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	
	@Override
	public void stop(){
		teaLogger.log("Stage is closing");
		teaLogger.log("restarting application for UI");
	    System.out.println("Stage is closing");
	    System.out.println("restarting application for UI");
	    try {
			String rerun = "java -cp target/TeaBot-Spice-0.0.1-SNAPSHOT-jar-with-dependencies.jar spiced.tea.cup.time.Main";
			Runtime.getRuntime().exec(rerun);
			System.exit(0);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * TODO create loopTea object to take all of the current TeaJobs make sure
	 * they're running, and handle the daemon
	 * 
	 */

	// TODO figure out how to create a java daemon
	// TODO sort all the current times into a sorted list by the earliest times
	// in relation to localtime

}

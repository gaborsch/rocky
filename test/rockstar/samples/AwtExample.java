package rockstar.samples;

import java.awt.Dialog;
import java.awt.Label;
import java.awt.Window;

public class AwtExample {

	public static void main(String[] args) throws InterruptedException {
		Dialog myDialog = new Dialog(((Window)null),"Hello world!");
		myDialog.setBounds(500, 500, 200, 100);
		Label myLabel = new Label("Hello world!");
		myLabel.setAlignment(Label.CENTER);
		myDialog.add(myLabel);
		myDialog.setVisible(true);

		Thread.sleep(5_000L);

		myDialog.setVisible(false);
		System.out.println("AWT done!");
	}

}

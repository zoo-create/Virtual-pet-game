import javax.swing.SwingUtilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class MainGame {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				VirtualPetGameGUI ex = new VirtualPetGameGUI();
				ex.setVisible(true);
				System.out.println("Game window set to visible"); // デバッグ情報
			}
		});

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("MySQL JDBC Driver loaded");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("Main method started"); // デバッグ情報
	}
}
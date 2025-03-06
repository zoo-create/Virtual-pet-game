import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class VirtualPetGameGUI extends JFrame {
	// フィールド
	private Animal pet; // ペット
	private JTextArea statusTextArea; // ステータス表示用ラベル
	private JTextArea outputArea;
	private JButton playButton, sleepButton, walkButton, feedButton, hospitalButton, exitButton, saveButton, loadButton; // アクションボタン
	private JLabel petImageLabel;
	private Container actionPanel;
	private BufferedImage backgroundImg;
	private BufferedImage overlayImg;
	private JComboBox<String> petSelector; // ペット選択用ドロップダウン
	private Timer timer;
	private int initialWidth = 900; // 初期ウィンドウ幅

	// コンストラクタ
	public VirtualPetGameGUI() {
		// ウィンドウの基本設定
		setTitle("バーチャルペットゲーム");
		setSize(900, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// ペット選択パネルの作成
		createPetSelectionPanel(); // ペット選択パネルを作成して追加
		System.out.println("VirtualPetGameGUI initialized"); // デバッグ情報

		// outputArea の初期化
		outputArea = new JTextArea();
		outputArea.setEditable(false);

		// リサイズ時に再描画するように設定。
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				revalidate();
				repaint();
			}
		});
		playBGM("polka.wav");
	}

	// パネル作成
	private void createPetSelectionPanel() {
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridLayout(5, 1));
		selectionPanel.setBackground(Color.WHITE); // パネルの背景色を変更
		TitledBorder titledBorder = BorderFactory.createTitledBorder("ペット選択"); // 境界線とタイトルを追加
		titledBorder.setTitleFont(new Font("MS Gothic", Font.BOLD, 30)); // フォントサイズを変更
		selectionPanel.setBorder(titledBorder); // 境界線をパネルに設定
		System.out.println("Pet selection panel created"); // デバッグ情報

		// ペット選択ボタンの作成(犬)
		JButton dogButton = new JButton("犬を選ぶ");
		dogButton.setFont(new Font("MS Gothic", Font.BOLD, 20)); // フォントサイズとスタイルを変更
		dogButton.setBackground(Color.LIGHT_GRAY); // 背景色を変更
		dogButton.setForeground(Color.RED); // 前景色を変更
		dogButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // 境界線を追加

		// (鳥)
		JButton birdButton = new JButton("鳥を選ぶ");
		birdButton.setFont(new Font("MS Gothic", Font.BOLD, 20));
		birdButton.setBackground(Color.LIGHT_GRAY);
		birdButton.setForeground(Color.GREEN);
		birdButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		// (魚)
		JButton fishButton = new JButton("魚を選ぶ");
		fishButton.setFont(new Font("MS Gothic", Font.BOLD, 20));
		fishButton.setBackground(Color.LIGHT_GRAY);
		fishButton.setForeground(Color.BLUE);
		fishButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		// (ロード)
		loadButton = new JButton("ロード");
		loadButton.setFont(new Font("MS Gothic", Font.BOLD, 20));
		loadButton.setBackground(Color.LIGHT_GRAY);
		loadButton.setForeground(Color.MAGENTA);
		loadButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		// ペット選択用ドロップダウンメニューの作成(パネルの装飾)
		petSelector = new JComboBox<>();
		petSelector.setFont(new Font("MS Gothic", Font.BOLD, 20)); // フォントサイズとスタイルを変更
		petSelector.setBackground(Color.LIGHT_GRAY); // 背景色を変更
		petSelector.setForeground(Color.BLACK); // 前景色を変更
		petSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// アクションリスナーの中身
			}
		});

		// ボタンにアクションリスナーを追加
		dogButton.addActionListener(e -> createPet(Animal.Type.DOG));
		birdButton.addActionListener(e -> createPet(Animal.Type.BIRD));
		fishButton.addActionListener(e -> createPet(Animal.Type.FISH));
		loadButton.addActionListener(e -> {
			String selectedPet = (String) petSelector.getSelectedItem();
			if (selectedPet != null && !selectedPet.isEmpty()) {
				loadPet(selectedPet);
			} else {
				JOptionPane.showMessageDialog(this, "ロードするペットを選択してください。");
			}
		});

		// パネルにボタンを追加
		selectionPanel.add(dogButton);
		selectionPanel.add(birdButton);
		selectionPanel.add(fishButton);
		selectionPanel.add(loadButton);
		selectionPanel.add(petSelector); // ドロップダウンメニューを追加

		// メインウィンドウの中央にパネルを配置
		add(selectionPanel, BorderLayout.CENTER);
		System.out.println("Selection panel added to the frame"); // デバッグ情報

		// ペット名をロードしてドロップダウンに追加
		loadPetNames();
	}

	// BGM再生
	public void playBGM(String filename) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(filename));
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.loop(Clip.LOOP_CONTINUOUSLY); // BGMをループ再生
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 効果音再生
	public void playSoundEffect(String filename) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(filename));
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

// ----------------------------------------------------------------------------------------------------------------
	// ペット作成画面
	private void createPet(Animal.Type type) {
		String name = JOptionPane.showInputDialog(this, "ペットの名前を入力してください");
		try {
			PetName(name);
			if (name != null && !name.trim().isEmpty()) {
				pet = new Animal(name.trim(), type); // 名前の前後の空白を削除
				System.out.println("Pet created: " + name + " of type " + type); // デバッグ情報
				startGame(); // ゲーム開始
				startTimer(); // タイマーを開始
			}
		} catch (PetGameException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
			createPet(type);
		}
	}

	// 入力検証を強化
	private void PetName(String name) throws PetGameException {
		// ペットの名前を入力するダイアログを表示
		if (name != null && name.trim().isEmpty()) {
			throw new PetGameException("ペットの名前を入力してください");
		}
		if (name.length() > 10) {
			throw new PetGameException("名前が長すぎます。(10文字以内)");
		}
		if (!name.matches("^[a-zA-Z0-9ぁ-んァ-ン--鉞]+$")) {
			throw new PetGameException("使用できない文字が含まれています。");
		}
	}

	// 画像を読み込むメソッドを追加
	private void loadImages() {
		try {
			// 背景画像（JPG）の読み込み
			// backgroundImg = ImageIO.read(new File("src/room.jpg"));
			String backgroundImagePath = "";
			switch (pet.getType().toLowerCase()) {
			case "dog":
				backgroundImagePath = "src/DOG_room.jpg";
				break;
			case "bird":
				backgroundImagePath = "src/BIRD_room.jpg";
				break;
			case "fish":
				backgroundImagePath = "src/FISH_room.jpg";
				break;
			default:
				backgroundImagePath = "src/default_room.jpg";
				break;
			}
			backgroundImg = ImageIO.read(new File(backgroundImagePath));

			// 重ねる画像（PNG）の読み込みとサイズ調整
			BufferedImage originalOverlayImg = ImageIO.read(new File("src/" + pet.getType().toLowerCase() + ".PNG"));
			if (originalOverlayImg != null) {
				overlayImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB); // 新しいサイズ
				Graphics2D g2d = overlayImg.createGraphics();
				g2d.drawImage(originalOverlayImg, 0, 0, 200, 200, null); // 新しいサイズにリサイズ
				g2d.dispose();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------------
	// ゲームスタート
	// startGameメソッド
	private void startGame() {
		// ペット選択パネルを削除
		getContentPane().removeAll();
		// 新しいレイアウトを設定
		setLayout(new BorderLayout());

		System.out.println("Game started"); // デバッグ情報

		// ステータステキストエリアの設定
		JTextArea statusTextArea = new JTextArea();
		statusTextArea.setEditable(false);
		statusTextArea.setFont(new Font("MS Gothic", Font.BOLD, 25)); // フォントサイズを18に設定
		statusTextArea.setLineWrap(true); // 自動改行を有効にする
		statusTextArea.setWrapStyleWord(true); // 単語単位で改行を行う

		// スクロールバーを自動的に表示
		JScrollPane scrollPane = new JScrollPane(statusTextArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPane, BorderLayout.NORTH);

		// ステータス表示用のラベルを作成
		setStatusTextArea(statusTextArea); // ステータステキストエリアをセット
		getStatusTextArea().setText(pet.checkStatus());
		// getStatusTextArea().setEditable(false);

		actionPanel = new JPanel();
		actionPanel.setLayout(new GridLayout(1, 3));

		// アクションボタンのパネルを作成
		actionPanel = new JPanel(); // クラスレベルの変数を初期化
		actionPanel.setLayout(new GridLayout(1, 3));

		// 各アクションボタンを作成し、リスナーを設定
		playButton = new JButton("遊ぶ");
		playButton.addActionListener(e -> performAction("play"));
		sleepButton = new JButton("寝る");
		sleepButton.addActionListener(e -> performAction("sleep"));
		walkButton = new JButton("散歩");
		walkButton.addActionListener(e -> performAction("walk"));
		feedButton = new JButton("食事");
		feedButton.addActionListener(e -> performAction("feed"));
		hospitalButton = new JButton("病院");
		hospitalButton.addActionListener(e -> performAction("gotoHospital"));
		exitButton = new JButton("終了");
		exitButton.addActionListener(e -> {
			int choice = JOptionPane.showConfirmDialog(this, "ゲームを終了してますか？", "確認", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		});

		saveButton = new JButton("保存");
		saveButton.addActionListener(e -> savePet());

		loadButton = new JButton("ロード");
		loadButton.addActionListener(e -> {
			String selectedPet = (String) petSelector.getSelectedItem();
			if (selectedPet != null && !selectedPet.isEmpty()) {
				loadPet(selectedPet);
			} else {
				JOptionPane.showMessageDialog(this, "ロードするペットを選択してください。");
			}
		}); // loadButton.addActionListener(e -> loadPet());

		// アクションボタンをパネルに追加
		actionPanel.add(playButton);
		actionPanel.add(sleepButton);
		actionPanel.add(walkButton);
		actionPanel.add(feedButton);
		actionPanel.add(hospitalButton);
		actionPanel.add(saveButton);
		actionPanel.add(loadButton);
		actionPanel.add(exitButton);

		// アクションパネルをウィンドウの下部に配置
		add(actionPanel, BorderLayout.SOUTH);

		// 画像パネルを作成して設定
		loadImages(); // 画像を読み込む

		JPanel imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (backgroundImg != null) {
					g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
				}
				if (overlayImg != null) {
					// PNG画像（ペット）の位置
					// ウィンドウサイズに基づいてペット画像の位置を計算
					int xOffset = 20; // 右にずらすオフセット値を設定
					int x = (int) ((350.0 / initialWidth) * getWidth()) + xOffset; // オフセットを追加
					int y = getHeight() - overlayImg.getHeight() - 50; // ペット画像を床に接する位置に配置

					// ウィンドウサイズに基づいて相対座標を計算
//					int x = (int) (350.0 / initialWidth * getWidth());
//					int y = (int) (400.0 / initialHeight * getHeight());

					// ウィンドウのリサイズに応じてペット画像の位置を計算
//                  int x = (getWidth() - overlayImg.getWidth()) / 2;
//                  int y = (getHeight() - overlayImg.getHeight()) / 2 + 50;

					// 任意の座標
//					int x = 350;
//					int y = 400;
					g2d.drawImage(overlayImg, x, y, this);
				}
			}
		};

		petImageLabel = new JLabel(); // petImageLabel を初期化
		imagePanel.add(petImageLabel, BorderLayout.CENTER);

		add(imagePanel, BorderLayout.CENTER);

		// 画面を更新
		revalidate();
		repaint();

		// タイマースタート(時間経過でステータス変化開始)
		startTimer();
	}

	// アクションによってペットの画像を更新するためのメソッド
	private void updatePetImage(String action) {
		String imagePath = "images/" + pet.getType().toLowerCase() + "_" + action + ".gif";
		ImageIcon icon = new ImageIcon(imagePath);
		petImageLabel.setIcon(icon);
	}

//-----------------------------------------------------------------------------
	// デバッグ、セーブ、ロード
	// デバッグモード
	public class ErrorLogger {
		private static final String LOG_FILE = "error_log.txt";

		public static void logError(Exception e, String context) {
			try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
				writer.write("===" + new Date() + "===\n");
				writer.write("Context:" + context + "\n");
				writer.write("Error:" + e.getMessage() + "\n");
				writer.write("Stack Trace:\n");
				for (StackTraceElement element : e.getStackTrace()) {
					writer.write(element.toString() + "\n");
				}
				writer.write("\n");
			} catch (IOException ex) {
				System.out.println("ログの書き込みに失敗しました :" + ex.getMessage());
			}
		}
	}

	private boolean isDebugMode = false;

	// デバッグメソッド
	public void toggleDebugMode() {
		try {
			isDebugMode = !isDebugMode;
			System.out.println("デバッグモード:" + (isDebugMode ? "ON" : "OFF"));
			if (isDebugMode) {
				showDebuginfo();
			}
		} catch (Exception e) {
			ErrorLogger.logError(e, "DebugMode");
		}
	}

	private void showDebuginfo() {
		if (!isDebugMode)
			return;

		System.out.println("===デバッグ情報===");
		System.out.println("メモリ使用量:" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
		System.out.println("スレッド数:" + Thread.activeCount());
		System.out.println("OS:" + System.getProperty("os.name"));
		System.out.println("java Version:" + System.getProperty("java.version"));
	}

	// ゲームの情報を保存または更新する(データベース上)
	private void savePet() {
		String url = "jdbc:mysql://localhost:3306/pet";
		String user = "root";
		String password = "";

		String checkSql = "SELECT COUNT(*) FROM pets WHERE name = ?";
		// インサート
		String insertSql = "INSERT INTO pets(name, level, energy, maxEnergy, happiness, satiety, maxSatiety, stress, experience, isSick, type) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		// アップデート
		String updateSql = "UPDATE pets SET level = ?, energy = ?, maxEnergy = ?, happiness = ?, satiety = ?, maxSatiety = ?, stress = ?, experience = ?, isSick = ?, type = ? WHERE name = ?";

		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			// ペットが既に存在するかをチェック
			try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
				checkStmt.setString(1, pet.getName());
				try (ResultSet rs = checkStmt.executeQuery()) {
					if (rs.next() && rs.getInt(1) > 0) {
						// ペットが存在する場合は更新
						try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
							updateStmt.setInt(1, pet.getLevel());
							updateStmt.setInt(2, pet.getEnergy());
							updateStmt.setInt(3, pet.getMaxEnergy());
							updateStmt.setInt(4, pet.getHappiness());
							updateStmt.setInt(5, pet.getSatiety());
							updateStmt.setInt(6, pet.getMaxSatiety());
							updateStmt.setInt(7, pet.getStress());
							updateStmt.setInt(8, pet.getExperience());
							updateStmt.setBoolean(9, pet.getIsSick());
							updateStmt.setString(10, pet.getType().toString());
							updateStmt.setString(11, pet.getName());
							updateStmt.executeUpdate();
							JOptionPane.showMessageDialog(this, "ペットの状態を更新しました！");
						}
					} else {
						// ペットが存在しない場合は挿入
						try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
							insertStmt.setString(1, pet.getName());
							insertStmt.setInt(2, pet.getLevel());
							insertStmt.setInt(3, pet.getEnergy());
							insertStmt.setInt(4, pet.getMaxEnergy());
							insertStmt.setInt(5, pet.getHappiness());
							insertStmt.setInt(6, pet.getSatiety());
							insertStmt.setInt(7, pet.getMaxSatiety());
							insertStmt.setInt(8, pet.getStress());
							insertStmt.setInt(9, pet.getExperience());
							insertStmt.setBoolean(10, pet.getIsSick());
							insertStmt.setString(11, pet.getType().toString());
							insertStmt.executeUpdate();
							JOptionPane.showMessageDialog(this, "ペットの状態を保存しました！");
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "保存に失敗しました。" + e.getMessage());
		}
	}

	// 保存したペット名からロードメソッド
	private void loadPetNames() {
		List<String> petNames = new ArrayList<>();
		String url = "jdbc:mysql://localhost:3306/pet";
		String user = "root";
		String password = "";
		String sql = "SELECT name FROM pets";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				petNames.add(rs.getString("name"));
			}
			for (String petName : petNames) {
				petSelector.addItem(petName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "ペット名のロードに失敗しました。" + e.getMessage());
		}
	}

	// ゲームの情報をロードする(データベース上)
	private void loadPet(String petName) {
		String url = "jdbc:mysql://localhost:3306/pet";
		String user = "root";
		String password = "";
		String sql = "SELECT * FROM pets WHERE name = ?";

		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, petName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString("name");
					int level = rs.getInt("level");
					int energy = rs.getInt("energy");
					int maxEnergy = rs.getInt("maxEnergy");
					int happiness = rs.getInt("happiness");
					int satiety = rs.getInt("satiety");
					int maxSatiety = rs.getInt("maxSatiety");
					int stress = rs.getInt("stress");
					int experience = rs.getInt("experience");
					boolean isSick = rs.getBoolean("isSick");
					String typeString = rs.getString("type");

					pet = new Animal(name, level, energy, maxEnergy, happiness, satiety, maxSatiety, stress, experience,
							isSick, Animal.Type.valueOf(typeString.toUpperCase()));
					startGame(); // ゲーム開始
					updateStatus();
					updatePetImage("normal");
					System.out.println("Loaded pet type: " + pet.getType());

					for (Component comp : actionPanel.getComponents()) {
						if (comp instanceof JButton) {
							comp.setEnabled(true);
						}
					}

					JOptionPane.showMessageDialog(this, "ペットのデータをロードしました！");
				} else {
					JOptionPane.showMessageDialog(this, "ペットが見つかりませんでした。");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "ロードに失敗しました。" + e.getMessage());
		}
	}

//-------------------------------------------------------------------------------------------------------------
	// アクションボタン
	private void performAction(String action) {
		String result = "";
		// boolean success = false;

		switch (action) {
		case "play":
			result = pet.playBall();
			break;
		case "sleep":
			result = pet.sleep();
			break;
		case "walk":
			result = pet.walk();
			break;
		case "feed":
			result = pet.feed();
			break;
		case "gotoHospital":
			result = pet.gotoHospital();
			break;
		}

		// ダイアログ
		// メッセージラベルを作成してフォントを設定
		JLabel messageLabel = new JLabel(result);
		messageLabel.setFont(new Font("MS Gothic", Font.PLAIN, 24)); // フォントを設定

		// ボタンのフォント、サイズの設定
		UIManager.put("OptionPane.buttonFont", new Font("MS Gothic", Font.PLAIN, 24));

		// ダイアログを作成
		JOptionPane pane = new JOptionPane(messageLabel, JOptionPane.INFORMATION_MESSAGE);
		JDialog dialog = pane.createDialog(null, "結果");

		// ダイアログのサイズを設定
		// dialog.setSize(200, 200);

		int centerX = this.getWidth() / 2;
		int centerY = this.getHeight() / 2;

//		int dialogX = centerX + 250;
//		int dialogY = centerY + -100;

		int dialogX = centerX;
		int dialogY = centerY - 100;

		// ダイアログの位置を設定
		dialog.setLocation(dialogX, dialogY);
		// ダイアログを表示
		dialog.setVisible(true);

		// アクションの結果をダイアログで表示
		// JOptionPane.showMessageDialog(this, result);
		// ステータス表示を更新
		getStatusTextArea().setText(pet.checkStatus());

		updatePetImage(action);
	}

	// 時間経過によるステータス変化
	// ゲームオーバーのチェック
	public void updatePetStatus() {
		if (pet != null) {
			try {
				pet.timePass();
				System.out.println("Debug: pet.timePass() called"); // デバッグ情報
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getStatusTextArea().setText(pet.checkStatus());
						System.out.println("Debug: UI updated with pet status"); // デバッグ情報
						revalidate();
						repaint();

						// ゲームオーバーのチェック
						if (pet.isGameOver()) {
							showGameOverDialog(pet.getGameOverInfo());
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ゲームオーバーダイアログを表示するメソッド
	private void showGameOverDialog(String gameOverInfo) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, "ゲームオーバー！", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0); // ゲームを終了する（必要に応じてカスタマイズ可能）
			}
		});
	}

	// ロード後タイマースタート
	private void startTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					updatePetStatus();
					System.out.println("Debug: updatePetStatus() called by Timer"); // デバッグ情報

					// 次の実行時間をランダムに設定
					int randomInterval = getRandomInterval();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							updatePetStatus();
							System.out.println("Debug: updatePetStatus() called by Timer"); // デバッグ情報
						}
					}, randomInterval); // ランダムな間隔で再スケジュール
				}
			}, 0, 20000); // 初回タイマー(20秒後)
		}
		System.out.println("Debug: Timer started"); // デバッグ情報
	}

	// ランダムな間隔を生成するメソッド
	private int getRandomInterval() {
		Random random = new Random();
		return (random.nextInt(10) + 1) * 20000; // 20秒から200秒の間でランダムな値を生成
	}

	//
//	private void startTimer() {
//	    if (timer == null) {
//	        timer = new Timer();
//	        timer.schedule(new TimerTask() {
//	            @Override
//	            public void run() {
//	                updatePetStatus();
//	                System.out.println("Debug: updatePetStatus() called by Timer"); // デバッグ情報
//	            }
//	        }, 0, 10000); // 10秒ごとに呼び出す
//	    }
//	    System.out.println("Debug: Timer started"); // デバッグ情報
//	}

	// ロード時のステータス表示
	private void updateStatus() {
		if (pet != null) {
			String status = "名前: " + pet.getName() + "\n" + "レベル: " + pet.getLevel() + "\n" + "体力: " + pet.getEnergy()
					+ "\n" + "最大体力: " + pet.getMaxEnergy() + "\n" + "幸福度: " + pet.getHappiness() + "\n" + "満腹度: "
					+ pet.getSatiety() + "\n" + "最大満腹度: " + pet.getMaxSatiety() + "\n" + "ストレス度: " + pet.getStress()
					+ "\n" + "獲得経験値: " + pet.getExperience();
			getStatusTextArea().setText(status);
			getStatusTextArea().setText(pet.checkStatus());
		}
	}

	public JTextArea getStatusTextArea() {
		return statusTextArea;
	}

	public void setStatusTextArea(JTextArea statusTextArea) {
		this.statusTextArea = statusTextArea;
	}
}

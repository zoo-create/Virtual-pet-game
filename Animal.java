import java.util.Random;

public class Animal {
	// 動物の種類を表す列挙型
	public enum Type {
		DOG, BIRD, FISH;
	}

	// フィールド
	private Type type; // 動物の種類
	protected String name; // 動物の名前
	protected int level; // レベル
	protected int energy; // 現在の体力
	protected int maxEnergy; // 最大体力
	protected int happiness; // 幸福度
	protected int satiety; // 満腹度
	protected int maxSatiety; // 最大満腹度
	protected int stress; // ストレス度
	protected boolean isSick; // 病気かどうか
	protected int experience; // 経験値
	protected int energyVariance; // 体力の増減値
	protected int happinessVariance; // 幸福度の増減値
	protected int satietyVariance; // 満腹度の増減値
	protected int totalLevel = 0; // 最終レベル
	protected int totalFeeding = 0; // 給餌回数
	protected int totalPlays = 0; // 遊んだ回数
	protected int totalSickDays = 0; // 病気になった日数

	VirtualPetGameGUI virtualPetGameGUI = new VirtualPetGameGUI();

	// コンストラクタ
	public Animal(String name, int level, int energy, int maxEnergy, int happiness, int satiety, int maxSatiety,
			int stress, int experience, boolean isSick, Type type) {
		this.name = name;
		this.level = level;
		this.happiness = happiness;
		this.energy = energy;
		this.maxEnergy = maxEnergy;
		this.satiety = satiety;
		this.maxSatiety = maxSatiety;
		this.stress = stress;
		this.experience = experience;
		this.isSick = isSick;
		this.type = type;
	}

	public Animal(String name, Type type) {
		this.name = name;
		this.type = type;
		this.happiness = 50; // 初期幸福度
		this.satiety = 20; // 初期満腹度
		this.stress = 30; // 初期ストレス
		this.level = 1; // 初期レベル
		this.experience = 0; // 初期経験値

		// 動物の種類に応じて最大エネルギーを設定
		Random random = new Random();
		int variance = random.nextInt(20);
		switch (type) {

		case DOG:
			this.maxEnergy = 100 + variance;
			break;

		case BIRD:
			this.maxEnergy = 60 + variance;
			break;

		case FISH:
			this.maxEnergy = 40 + variance;
			break;
		}

		this.energy = maxEnergy; // 初期エネルギーは最大値

		// 動物の種類に応じて最大満腹度を設定
		switch (type) {

		case DOG:
			this.maxSatiety = 100;
			break;

		case BIRD:
			this.maxSatiety = 80;
			break;

		case FISH:
			this.maxSatiety = 60;
			break;
		}

		this.satiety = maxSatiety / 2; // 初期満腹度は最大値の半分

	}

//------------------------------------------------------------------------------------------------
	// 行動メソッド
	// 時間経過でのステータス変化
	public void timePass() {
		Random random = new Random();
		int variance = random.nextInt(10);
		int variance2 = random.nextInt(5);
		switch (type) {
		case DOG:
			satiety -= 10 + variance; // 時間と共にお腹が空く
			if (satiety < 30) {
				energy -= 5; // お腹が空きすぎると体力減少
				stress += 10; // ストレス上昇
			}
			if (satiety < 0) {
				satiety = 0;
			}
			if (energy <= 0) {
				energy = 0;
			}
			// 病気になる条件
			if (Math.random() < 0.1 && satiety < 30) {
				isSick = true;
				totalSickDays++;
				System.out.println("病気になった回数" + totalSickDays); // ログ
			}
			break;

		case BIRD:
			satiety -= 3 + variance; // 鳥の場合の満腹度減少
			if (satiety < 25) {
				energy -= 4; // お腹が空きすぎると体力減少
				stress += 8; // ストレス上昇
			}
			if (satiety < 0) {
				satiety = 0;
			}
			if (energy <= 0) {
				energy = 0;
			}
			// 病気になる条件
			if (Math.random() < 0.15 && satiety < 25) {
				isSick = true;
				totalSickDays++;
				System.out.println("病気になった回数" + totalSickDays); // ログ
			}
			break;

		case FISH:
			satiety -= 2 + variance2; // 魚の場合の満腹度減少
			if (satiety < 20) {
				energy -= 3; // お腹が空きすぎると体力減少
				stress += 6; // ストレス上昇
			}
			if (satiety < 0) {
				satiety = 0;
			}
			if (energy <= 0) {
				energy = 0;
			}
			// 病気になる条件
			if (Math.random() < 0.2 && satiety < 20) {
				isSick = true;
				totalSickDays++;
				System.out.println("病気になった回数" + totalSickDays); // ログ
			}
			break;
		}

		// 状態の表示（デバッグ用）
		System.out.println("Satiety: " + satiety);
		System.out.println("Energy: " + energy);
		System.out.println("Stress: " + stress);
		System.out.println("Is Sick: " + isSick);
		System.out.println("timePass メソッドが呼び出されました");
	}

	// レベルアップメソッド
	public String gainExperience() {
		if (experience >= level * 100) {
			level++;
			maxEnergy += 20;
			System.out.println(totalLevel);
			return getLevelUpMessage();
		}
		return "";
	}

	// ボール遊びのメソッド
	public String playBall() {
		if (energy < getEnergyForPlay()) {
			return name + "疲れちゃって、これ以上動けないよ‥";
		}

		virtualPetGameGUI.playSoundEffect("わんわん！.wav");
		energy -= getEnergyForPlay();
		happiness += getHappinessForPlay();
		experience += getExperiencePlay();
		totalPlays++;
		System.out.println("遊んだ回数" + totalPlays);
		String levelUpMessage = gainExperience();
		if (happiness > 100) {
			happiness = 100;
		}
		satiety -= getfeed();
		if (satiety < 0) {
			satiety = 0;
		}
		return name + getPlayMessage() + " " + (levelUpMessage.isEmpty() ? "" : "\n" + levelUpMessage);
	}

	// 睡眠メソッド
	public String sleep() {
		if (energy >= maxEnergy) {
			return name + ":元気一杯で眠くないよ。";
		}
		energy = maxEnergy;
		decreaseStress(10);
		return name + getSleepMessage();
	}

	// 散歩メソッド
	public String walk() {
		if (energy < getEnergyForPlay()) {
			return name + "は疲れていて" + getWalkExhaustedMessage();
		}
		energy -= getEnergyForPlay();
		experience += getExperiencePlay();
		satiety -= getfeed();
		if (satiety < 0) {
			satiety = 0;
		}
		happiness += getHappinessForPlay();
		if (happiness >= 100) {
			happiness = 100;
		}
		this.maxEnergy++;
		decreaseStress(10);
		return name + getWalkMessage();
	}

	// 食事メソッド
	public String feed() {
		if (isSick) {
			return name + "は病気で食事ができません。";
		}
		if (satiety >= maxSatiety) {
			satiety = maxSatiety;
			return name + "もうお腹一杯です。";
		}
		satiety += getfeed();
		totalFeeding++;
		System.out.println("給餌した回数" + totalFeeding);
		if (satiety > maxSatiety) {
			satiety = maxSatiety;
		}
		return name + getFeedMessege();
	}

	// 病院に連れていくメソッド
	public String gotoHospital() {
		if (isSick) {
			isSick = false;
			energy = Math.max(energy - 20, stress + 30); // 治療によるステータスの減少
			return name + "は治療を受けて、元気になりました。";
		}
		return name + "は病気ではありません。";
	}

	// ゲームオーバーの条件をチェックするメソッド
	public boolean isGameOver() {
		return satiety == 0 && energy <= 30 && stress >= 80;
	}

//---------------------------------------------------------------------------------------------------------------------------
	// ステータス変動メソッド
	// ステータスのチェックメソッド
	public String checkStatus() {
		return String.format("%sのステータス　-　○レベル：%d,　○体力：%d/%d,　○幸福度：%d,　○満腹度：%d/%d,　○ストレス：%d　○経験値：%d", name, level,
				energy, maxEnergy, happiness, satiety, maxSatiety, stress, experience);
	}

	// 動物の種類ごとに異なる値を返すプライベートメソッド
	// 経験値取得
	private int getExperiencePlay() {
		Random random = new Random();
		int variance = random.nextInt(100);
		switch (type) {
		case DOG:
			return variance;
		case BIRD:
			return variance;
		case FISH:
			return variance;
		default:
			return variance;
		}
	}

	// 消費体力
	private int getEnergyForPlay() {
		Random random = new Random();
		int variance = random.nextInt(5);
		switch (type) {
		case DOG:
			return energyVariance = 15 + variance;
		case BIRD:
			return energyVariance = 10 + variance;
		case FISH:
			return energyVariance = 7 + variance;
		default:
			return energyVariance = 10 + variance;
		}
	}

	// 幸福度
	private int getHappinessForPlay() {
		Random random = new Random();
		int variance = random.nextInt(3);
		switch (type) {
		case DOG:
			return happinessVariance = 5 + variance;
		case BIRD:
			return happinessVariance = 3 + variance;
		case FISH:
			return happinessVariance = 2 + variance;
		default:
			return happinessVariance = 10 + variance;
		}
	}

	// 満腹度
	private int getfeed() {
		Random random = new Random();
		int variance = random.nextInt(5);
		switch (type) {
		case DOG:
			return satietyVariance = 15 + variance;
		case BIRD:
			return satietyVariance = 10 + variance;
		case FISH:
			return satietyVariance = 7 + variance;
		default:
			return satietyVariance = 10 + variance;
		}
	}

	// ストレス減少のメソッド
	protected void decreaseStress(int amount) {
		stress = Math.max(stress - amount, 0);
	}

//-------------------------------------------------------------------------------------------------------------------------------------------
	// メッセージメソッド
	// レベルアップメッセージ
	private String getLevelUpMessage() {
		switch (type) {
		case DOG:
			return "レベルが" + level + "になりました！わんわん！";
		case BIRD:
			return "レベルが" + level + "になりました！ピヨピヨ！";
		case FISH:
			return "レベルが" + level + "になりました！スイスイ！";
		default:
			return "";
		}
	}

	// ボール遊びメッセージ
	private String getPlayMessage() {
		switch (type) {
		case DOG:
			return "はボールを追いかけて楽しく遊んだよ！わんわん！　体力：" + energyVariance + "ダウン" + " " + "幸福度：" + happinessVariance + "アップ";
		case BIRD:
			return "はボールを高い所から落として楽しく遊んだよ！ピヨピヨ！　体力：" + energyVariance + "ダウン" + " " + "幸福度：" + happinessVariance
					+ "アップ";
		case FISH:
			return "は水中でボールを追いかけて遊んだよ！スイスイ！　体力：" + energyVariance + "ダウン" + " " + "幸福度：" + happinessVariance + "アップ";
		default:
			return "は楽しく遊んだよ！";
		}
	}

	// 睡眠メッセージ
	private String getSleepMessage() {
		switch (type) {
		case DOG:
			return "はお気に入りのベッドですやすや眠りました。体力が回復した。";
		case BIRD:
			return "はお気に入りの木の小屋ですやすや眠りました。体力が回復した。";
		case FISH:
			return "はお気に入りの壺の中ですやすや眠りました。体力が回復した。";
		default:
			return "";
		}
	}

	// 歩くメッセージ
	private String getWalkMessage() {
		switch (type) {
		case DOG:
			return "はトコトコ、歩いたよ！わんわん！　体力：" + energyVariance + "ダウン" + " " + "幸福度：" + happinessVariance + "アップ";
		case BIRD:
			return "はパタパタ、飛んだよ！ピヨピヨ！　体力：" + energyVariance + "ダウン" + " " + "幸福度：" + happinessVariance + "アップ";
		case FISH:
			return "はスイスイ、泳いだよ！ピチピチ！　体力：" + energyVariance + "ダウン" + " " + "幸福度：" + happinessVariance + "アップ";
		default:
			return "は散歩したよ！";
		}
	}

	// 歩き疲れたメッセージ
	public String getWalkExhaustedMessage() {
		switch (type) {
		case DOG:
			return "疲れちゃって、もう歩けないワン...";
		case BIRD:
			return "羽がクタクタ...もう飛べないピヨ...";
		case FISH:
			return "ヒレがヘトヘト...もう泳げないブクブク...";
		default:
			return "疲れて動けません...";
		}
	}

	// 食事後メッセージ
	public String getFeedMessege() {
		switch (type) {
		case DOG:
			return "はご飯を食べて、満足そうにしっぽを振っている。ワンワン！";
		case BIRD:
			return "はご飯をついばみ、楽しそうにさえずった。ピヨピヨ！";
		case FISH:
			return "は餌を食べて、気持ちよさそうに泳ぎ回っている。ブクブク！";
		default:
			return "おいしいよ。";
		}
	}

//----------------------------------------------------------------------------------------------------------------------
	// getTypeメソッド
	public String getType() {
		return this.type.toString().toLowerCase();
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getHappiness() {
		return happiness;
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	public int getSatiety() {
		return satiety;
	}

	public int getMaxSatiety() {
		return maxSatiety;
	}

	public int getStress() {
		return stress;
	}

	public int getExperience() {
		return experience;
	}

	public boolean getIsSick() {
		return isSick;
	}

	// ゲームオーバー時の情報を取得するメソッド
	public String getGameOverInfo() {
		totalLevel = level;
		String gameOverInfo = String.format("最終レベル: %d\n遊んだ回数: %d\n給餌回数: %d\n病気になった日数: %d", totalLevel, totalPlays,
				totalFeeding, totalSickDays);
		return gameOverInfo;
	}
}

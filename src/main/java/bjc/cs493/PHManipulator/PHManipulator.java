package bjc.cs493.PHManipulator;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import bjc.utils.data.IHolder;
import bjc.utils.data.Identity;

/**
 * Main program for CS-493 project: manipulating the PH of a known solution
 * using two other solutions with known PH
 * 
 * @author ben
 *
 */
public class PHManipulator {
	private static final class RandomPHUpdater extends TimerTask {
		private IHolder<String> phHolder;

		public RandomPHUpdater(IHolder<String> holder) {
			this.phHolder = holder;

		}

		@Override
		public void run() {
			phHolder.replace(String.format("%.2f", Math.random() * 14));
		}
	}

	private static final class RandomPumpSpeedUpdater extends TimerTask {
		private IHolder<String> phHolder;

		public RandomPumpSpeedUpdater(IHolder<String> holder) {
			this.phHolder = holder;

		}

		@Override
		public void run() {
			double randVal = Math.random() * 100;

			phHolder.replace(String.format("%d", Math.round(randVal)));
		}
	}

	/**
	 * Start the program
	 * 
	 * @param args
	 *            Unused CLI args
	 */
	public static void main(String[] args) {
		IHolder<String> currentPH = new Identity<>("");
		IHolder<String> strongPumpSpeed = new Identity<>("");
		IHolder<String> weakPumpSpeed = new Identity<>("");

		IHolder<Timer> timerHolder = new Identity<>();

		SwingUtilities.invokeLater(() -> {
			PHValues values = new PHValues();
			IHolder<Integer> pumpSpeed = new Identity<>();

			JFrame mainFrame = PHManipulatorGUI.buildGUI(values, pumpSpeed,
					currentPH, strongPumpSpeed, weakPumpSpeed, () -> {
						timerHolder.replace(new Timer(true));

						Timer phTimer = timerHolder.getValue();

						phTimer.scheduleAtFixedRate(
								new RandomPHUpdater(currentPH), 1000,
								1000);

						phTimer.scheduleAtFixedRate(
								new RandomPumpSpeedUpdater(
										strongPumpSpeed),
								1200, 1000);

						phTimer.scheduleAtFixedRate(
								new RandomPumpSpeedUpdater(weakPumpSpeed),
								1400, 1000);
					}, () -> {
						timerHolder.getValue().cancel();
					});

			mainFrame.setSize(640, 480);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			mainFrame.setVisible(true);
		});
	}
}

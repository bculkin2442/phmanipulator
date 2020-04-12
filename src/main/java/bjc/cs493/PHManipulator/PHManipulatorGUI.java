package bjc.cs493.PHManipulator;

import java.math.BigDecimal;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bjc.utils.data.IHolder;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;
import bjc.utils.gui.FormattedInputPanel;
import bjc.utils.gui.HolderOutputPanel;
import bjc.utils.gui.SliderInputPanel;
import bjc.utils.gui.SliderInputPanel.SliderSettings;
import bjc.utils.gui.layout.HLayout;
import bjc.utils.gui.layout.VLayout;

/**
 * GUI builder class for PHManipulator
 * 
 * @author ben
 *
 */
public class PHManipulatorGUI {
	private static final BigDecimal			INIT_PH			= new BigDecimal(
			7);

	private static final AbstractFormatter	PH_FORMATTER	= new PHFormatter();

	private static final class PHFormatter
			extends JFormattedTextField.AbstractFormatter {
		private static final long		serialVersionUID	= -1149611569131212241L;

		private static final BigDecimal	PH_MINVAL			= new BigDecimal(
				0);
		private static final BigDecimal	PH_MAXVAL			= new BigDecimal(
				14);

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value == null) {
				// Default PH value
				return "7.0";
			} else if (value.equals(INIT_PH)) {
				return "7.0";
			}

			return value.toString();
		}

		@Override
		public Object stringToValue(String text) throws ParseException {
			try {
				BigDecimal phValue = new BigDecimal(text);

				if (phValue.compareTo(PH_MAXVAL) > 0) {
					throw new ParseException("PH must be less than 14", 0);
				} else if (phValue.compareTo(PH_MINVAL) < 0) {
					throw new ParseException("PH must be greater than 0",
							0);
				}

				return phValue;
			} catch (NumberFormatException nfex) {
				ParseException pex = new ParseException(
						"PH value must be a decimal-value", 0);

				pex.initCause(nfex);

				throw pex;
			}
		}
	}

	/**
	 * Build the main GUI window
	 * 
	 * @param values
	 *            The container of pH values to bind the fields to
	 * @param pumpSpeed
	 *            The place to store the chosen speed for the feed pump
	 * @param currentPH
	 *            The source for the current pH value to display
	 * @param strongPumpSpeed
	 *            The source for the speed of the strong pump
	 * @param weakPumpSpeed
	 *            The source for the speed of the weak pump
	 * @param startAction
	 *            The action to run upon the user confirming their choices
	 * @param stopAction
	 *            The action to run upon the user stopping their choices
	 * @return The main GUI window
	 */
	public static JFrame buildGUI(PHValues values,
			IHolder<Integer> pumpSpeed, IHolder<String> currentPH,
			IHolder<String> strongPumpSpeed, IHolder<String> weakPumpSpeed,
			Runnable startAction, Runnable stopAction) {
		IList<HolderOutputPanel> panels = new FunctionalList<>();

		JFrame mainFrame = new JFrame("PH Manipulator");

		JPanel holderPanel = new JPanel();
		holderPanel
				.setLayout(new BoxLayout(holderPanel, BoxLayout.Y_AXIS));

		Runnable confirmAction = () -> {
			startAction.run();

			panels.forEach(HolderOutputPanel::startUpdating);
		};

		JPanel inputPanel = createIputPanel(values, pumpSpeed,
				confirmAction, () -> {
					stopAction.run();

					panels.forEach(HolderOutputPanel::stopUpdating);
				}, () -> {
					panels.forEach(HolderOutputPanel::reset);
				});

		JPanel outputPanel = createOutputPanel(currentPH, strongPumpSpeed,
				weakPumpSpeed, panels);

		holderPanel.add(inputPanel);
		holderPanel.add(outputPanel);

		mainFrame.add(holderPanel);

		return mainFrame;
	}

	private static JPanel createOutputPanel(IHolder<String> currentPH,
			IHolder<String> strongPumpSpeed, IHolder<String> weakPumpSpeed,
			IList<HolderOutputPanel> panels) {
		JPanel outputPanel = new JPanel();
		outputPanel
				.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));

		HolderOutputPanel currentPHPanel = new HolderOutputPanel(
				"Current pH of output stream: ", currentPH, 1000);

		HolderOutputPanel strongPumpSpeedPanel = new HolderOutputPanel(
				"Current pump speed of strong solution: ", strongPumpSpeed,
				1000);

		HolderOutputPanel weakPumpSpeedPanel = new HolderOutputPanel(
				"Current pump speed of weak solution: ", weakPumpSpeed,
				1000);

		panels.add(currentPHPanel);
		panels.add(strongPumpSpeedPanel);
		panels.add(weakPumpSpeedPanel);

		outputPanel.add(currentPHPanel);
		outputPanel.add(strongPumpSpeedPanel);
		outputPanel.add(weakPumpSpeedPanel);

		return outputPanel;
	}

	private static JPanel createIputPanel(PHValues values,
			IHolder<Integer> pumpSpeed, Runnable confirmAction,
			Runnable stopAction, Runnable resetAction) {
		JPanel inputPanel = new JPanel();

		inputPanel.setLayout(new VLayout(6));

		FormattedInputPanel<BigDecimal> feedPHPanel = new FormattedInputPanel<>(
				"pH of feed solution (0.0 - 14.0)", 15, PH_FORMATTER,
				values::setFeedPH);

		FormattedInputPanel<BigDecimal> strongPHPanel = new FormattedInputPanel<>(
				"pH of strong solution (0.0 - 14.0)", 15, PH_FORMATTER,
				values::setStrongPH);
		FormattedInputPanel<BigDecimal> weakPHPanel = new FormattedInputPanel<>(
				"pH of weak solution (0.0 - 14.0)", 15, PH_FORMATTER,
				values::setWeakPH);

		FormattedInputPanel<BigDecimal> desiredPHPanel = new FormattedInputPanel<>(
				"Desired pH of output solution (0.0 - 14.0)", 15,
				PH_FORMATTER, values::setDesiredPH);

		SliderInputPanel feedPumpSpeedSlider = new SliderInputPanel(
				"Pump speed of feed solution:",
				new SliderSettings(0, 100, 10), 20, 5, (value) -> {
					pumpSpeed.replace(value);
				});

		values.setResetHandler(() -> {
			feedPHPanel.resetValues(INIT_PH);

			strongPHPanel.resetValues(INIT_PH);
			weakPHPanel.resetValues(INIT_PH);

			desiredPHPanel.resetValues(INIT_PH);

			feedPumpSpeedSlider.resetValues(10);

			resetAction.run();
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new HLayout(2));

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new VLayout(2));

		JButton resetVals = new JButton("Reset");
		JButton confirmVals = new JButton("Start");
		JButton stopVals = new JButton("Stop");

		resetVals.addActionListener((event) -> {
			values.resetValues();
		});

		confirmVals.addActionListener((event) -> {
			confirmAction.run();
		});

		stopVals.addActionListener((event) -> {
			stopAction.run();
		});

		statusPanel.add(confirmVals);
		statusPanel.add(stopVals);

		buttonPanel.add(resetVals);
		buttonPanel.add(statusPanel);

		inputPanel.add(feedPHPanel);

		inputPanel.add(strongPHPanel);
		inputPanel.add(weakPHPanel);

		inputPanel.add(desiredPHPanel);

		inputPanel.add(feedPumpSpeedSlider);

		inputPanel.add(buttonPanel);

		return inputPanel;
	}
}

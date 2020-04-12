package bjc.cs493.PHManipulator;

import java.math.BigDecimal;

import bjc.utils.data.IHolder;
import bjc.utils.data.Identity;

/**
 * Holds all of the user-specified PH values
 * 
 * @author ben
 *
 */
public class PHValues {
	private static final BigDecimal	INIT_PH	= new BigDecimal(7);

	private IHolder<BigDecimal>		feedPH;

	private IHolder<BigDecimal>		strongPH;
	private IHolder<BigDecimal>		weakPH;

	private IHolder<BigDecimal>		desiredPH;

	private Runnable				resetHandler;

	/**
	 * Create a new PH
	 */
	public PHValues() {
		feedPH = new Identity<>(INIT_PH);

		strongPH = new Identity<>(INIT_PH);
		weakPH = new Identity<>(INIT_PH);

		desiredPH = new Identity<>(INIT_PH);
	}

	/**
	 * Reset all pH values to their initial values
	 */
	public void resetValues() {
		feedPH.replace(INIT_PH);

		strongPH.replace(INIT_PH);
		weakPH.replace(INIT_PH);

		desiredPH.replace(INIT_PH);

		resetHandler.run();
	}

	/**
	 * Set the desired pH value of the output stream
	 * 
	 * @param value
	 *            The desired pH value of the output stream
	 */
	public void setDesiredPH(BigDecimal value) {
		if (value != null) {
			desiredPH.replace(value);
		}
	}

	/**
	 * Set the pH value of the feed stream
	 * 
	 * @param value
	 *            The pH of the feed stream
	 */
	public void setFeedPH(BigDecimal value) {
		if (value != null) {
			feedPH.replace(value);
		}
	}

	/**
	 * Set the action to run on value reset
	 * 
	 * @param resetHandler
	 *            the action to run on value reset
	 */
	public void setResetHandler(Runnable resetHandler) {
		this.resetHandler = resetHandler;
	}

	/**
	 * Set the pH value of the strong stream
	 * 
	 * @param value
	 *            The pH of the strong stream
	 */
	public void setStrongPH(BigDecimal value) {
		if (value != null) {
			strongPH.replace(value);
		}
	}

	/**
	 * Set the pH value of the weak stream
	 * 
	 * @param value
	 *            The pH of the weak stream
	 */
	public void setWeakPH(BigDecimal value) {
		if (value != null) {
			weakPH.replace(value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PHValues [feedPH=" + feedPH + ", strongPH=" + strongPH
				+ ", weakPH=" + weakPH + ", desiredPH=" + desiredPH + "]";
	}
}

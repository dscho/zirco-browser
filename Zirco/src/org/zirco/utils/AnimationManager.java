package org.zirco.utils;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationManager {

	private static final int ANIMATION_DURATION = 400;

	private Animation mInFromRightAnimation;
	private Animation mOutToLeftAnimation;
	private Animation mInFromLeftAnimation;
	private Animation mOutToRightAnimation;

	/**
	 * Holder for singleton implementation.
	 */
	private static class AnimationManagerHolder {
		private static final AnimationManager INSTANCE = new AnimationManager();
	}

	/**
	 * Get the unique instance of the Controller.
	 * @return The instance of the Controller
	 */
	public static AnimationManager getInstance() {
		return AnimationManagerHolder.INSTANCE;
	}

	private AnimationManager() {
		mInFromRightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT,
				0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

		mInFromRightAnimation.setDuration(ANIMATION_DURATION);
		mInFromRightAnimation.setInterpolator(new AccelerateInterpolator());

		mOutToLeftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		mOutToLeftAnimation.setDuration(ANIMATION_DURATION);
		mOutToLeftAnimation.setInterpolator(new AccelerateInterpolator());

		mInFromLeftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		mInFromLeftAnimation.setDuration(ANIMATION_DURATION);
		mInFromLeftAnimation.setInterpolator(new AccelerateInterpolator());

		mOutToRightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		mOutToRightAnimation.setDuration(ANIMATION_DURATION);
		mOutToRightAnimation.setInterpolator(new AccelerateInterpolator());
	}

	public Animation getInFromRightAnimation() {
		return mInFromRightAnimation;
	}

	public Animation getOutToLeftAnimation() {
		return mOutToLeftAnimation;
	}

	public Animation getInFromLeftAnimation() {
		return mInFromLeftAnimation;
	}

	public Animation getOutToRightAnimation() {
		return mOutToRightAnimation;
	}

}

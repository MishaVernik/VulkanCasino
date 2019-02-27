package stoswb.aas.vulvukan.schedule;

import android.animation.ObjectAnimator;
import android.view.View;
import com.daimajia.androidanimations.library.BaseViewAnimator;

public class PulseAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "scaleY", 1, 0.9f, 1),
                ObjectAnimator.ofFloat(target, "scaleX", 1, 0.9f, 1)
        );
    }
}
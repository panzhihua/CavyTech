package com.basecore.widget.dialog;

import com.basecore.widget.dialog.effects.BaseEffects;
import com.basecore.widget.dialog.effects.FadeIn;
import com.basecore.widget.dialog.effects.Fall;
import com.basecore.widget.dialog.effects.FlipH;
import com.basecore.widget.dialog.effects.FlipV;
import com.basecore.widget.dialog.effects.NewsPaper;
import com.basecore.widget.dialog.effects.RotateBottom;
import com.basecore.widget.dialog.effects.RotateLeft;
import com.basecore.widget.dialog.effects.Shake;
import com.basecore.widget.dialog.effects.SideFall;
import com.basecore.widget.dialog.effects.SlideBottom;
import com.basecore.widget.dialog.effects.SlideLeft;
import com.basecore.widget.dialog.effects.SlideRight;
import com.basecore.widget.dialog.effects.SlideTop;
import com.basecore.widget.dialog.effects.Slit;

/**
 * Created by lee on 2014/7/30.
 */
public enum  Effectstype {

    Fadein(FadeIn.class),
    Slideleft(SlideLeft.class),
    Slidetop(SlideTop.class),
    SlideBottom(SlideBottom.class),
    Slideright(SlideRight.class),
    Fall(Fall.class),
    Newspager(NewsPaper.class),
    Fliph(FlipH.class),
    Flipv(FlipV.class),
    RotateBottom(RotateBottom.class),
    RotateLeft(RotateLeft.class),
    Slit(Slit.class),
    Shake(Shake.class),
    Sidefill(SideFall.class);
    private Class<? extends BaseEffects> effectsClazz;

    private Effectstype(Class<? extends BaseEffects> mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        BaseEffects bEffects=null;
	try {
		bEffects = effectsClazz.newInstance();
	} catch (ClassCastException e) {
		throw new Error("Can not init animatorClazz instance");
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		throw new Error("Can not init animatorClazz instance");
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		throw new Error("Can not init animatorClazz instance");
	}
	return bEffects;
    }
}

package com.sesolutions.animate;

/**
 * Created by Himanshu Kumar on 25-05-2017.
 */


public enum Techniques {
    Test(ShakeAnimator.class),
    Shake(ShakeAnimator.class),
    SlideDown(SlideDownAnimator.class),
    SlideUp(SlideUpAnimator.class),
    Fade(FadeOutAnimator.class),
    ZoomIn(ZoomInAnimator.class),
    ZoomOut(ZoomOutAnimator.class),
    SlideInRight(SlideInRightAnimator.class),
    SlideInUp(SlideInUpAnimator.class),
    SlideOutDown(SlideOutDownAnimator.class),
    SlideInLeft(SlideInLeftAnimator.class),
    SlideOutLeft(SlideOutLeftAnimator.class),
    RotateIn(RotateInAnimator.class),
    RotateOut(RotateOutAnimator.class),
    FadeInUp(FadeInUpAnimator.class),
    FadeOutUp(FadeOutUpAnimator.class),
    BounceInDown(SlideDownLittleAnimator.class),
    BounceInUp(BounceInUpAnimator.class),
    FadeIn(FadeInAnimator.class),
    FadeOut(FadeOutAnimator.class);


    public static final int SHAKE = 1;
    public static final int SLIDE_DOWN = 2;
    public static final int SLIDE_UP = 3;
    public static final int FADE = 4;
    public static final int ZOOM_IN = 5;
    public static final int ZOOM_OUT = 6;
    public static final int SLIDE_IN_RIGHT = 7;
    public static final int SLIDE_IN_UP = 8;
    public static final int SLIDE_OUT_DOWN = 9;
    public static final int SLIDE_IN_LEFT = 10;
    public static final int SLIDE_OUT_LEFT = 11;
    public static final int ROTATE_IN = 12;
    public static final int ROTATE_OUT = 13;
    public static final int FADE_IN_UP = 14;
    public static final int FADE_OUT_UP = 15;
    public static final int BOUNCE_IN_DOWN = 16;
    public static final int BOUNCE_IN_UP = 17;
    public static final int FADE_IN = 18;
    public static final int FADE_OUT = 19;
    private Class animatorClazz;


    Techniques(Class clazz) {
        animatorClazz = clazz;
    }


    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}

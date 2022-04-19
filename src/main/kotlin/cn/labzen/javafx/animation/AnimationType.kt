package cn.labzen.javafx.animation

import animatefx.animation.*

/**
 * 参考 [animate.style](https://animate.style/) 查看动画效果
 */
enum class AnimationType(val cls: Class<out AnimationFX>) {

  NONE(AnimationFX::class.java),

  /**
   * 使用动画类 [Bounce]
   */
  BOUNCE(Bounce::class.java),

  /**
   * 使用动画类 [BounceIn]
   */
  BOUNCE_IN(BounceIn::class.java),

  /**
   * 使用动画类 [BounceInDown]
   */
  BOUNCE_IN_DOWN(BounceInDown::class.java),

  /**
   * 使用动画类 [BounceInLeft]
   */
  BOUNCE_IN_LEFT(BounceInLeft::class.java),

  /**
   * 使用动画类 [BounceInRight]
   */
  BOUNCE_IN_RIGHT(BounceInRight::class.java),

  /**
   * 使用动画类 [BounceInUp]
   */
  BOUNCE_IN_UP(BounceInUp::class.java),

  /**
   * 使用动画类 [BounceOut]
   */
  BOUNCE_OUT(BounceOut::class.java),

  /**
   * 使用动画类 [BounceOutDown]
   */
  BOUNCE_OUT_DOWN(BounceOutDown::class.java),

  /**
   * 使用动画类 [BounceOutLeft]
   */
  BOUNCE_OUT_LEFT(BounceOutLeft::class.java),

  /**
   * 使用动画类 [BounceOutRight]
   */
  BOUNCE_OUT_RIGHT(BounceOutRight::class.java),

  /**
   * 使用动画类 [BounceOutUp]
   */
  BOUNCE_OUT_UP(BounceOutUp::class.java),

  /**
   * 使用动画类 [FadeIn]
   */
  FADE_IN(FadeIn::class.java),

  /**
   * 使用动画类 [FadeInDown]
   */
  FADE_IN_DOWN(FadeInDown::class.java),

  /**
   * 使用动画类 [FadeInDownBig]
   */
  FADE_IN_DOWN_BIG(FadeInDownBig::class.java),

  /**
   * 使用动画类 [FadeInLeft]
   */
  FADE_IN_LEFT(FadeInLeft::class.java),

  /**
   * 使用动画类 [FadeInLeftBig]
   */
  FADE_IN_LEFT_BIG(FadeInLeftBig::class.java),

  /**
   * 使用动画类 [FadeInRight]
   */
  FADE_IN_RIGHT(FadeInRight::class.java),

  /**
   * 使用动画类 [FadeInRightBig]
   */
  FADE_IN_RIGHT_BIG(FadeInRightBig::class.java),

  /**
   * 使用动画类 [FadeInUp]
   */
  FADE_IN_UP(FadeInUp::class.java),

  /**
   * 使用动画类 [FadeInUpBig]
   */
  FADE_IN_UP_BIG(FadeInUpBig::class.java),

  /**
   * 使用动画类 [FadeOut]
   */
  FADE_OUT(FadeOut::class.java),

  /**
   * 使用动画类 [FadeOutDown]
   */
  FADE_OUT_DOWN(FadeOutDown::class.java),

  /**
   * 使用动画类 [FadeOutDownBig]
   */
  FADE_OUT_DOWN_BIG(FadeOutDownBig::class.java),

  /**
   * 使用动画类 [FadeOutLeft]
   */
  FADE_OUT_LEFT(FadeOutLeft::class.java),

  /**
   * 使用动画类 [FadeOutLeftBig]
   */
  FADE_OUT_LEFT_BIG(FadeOutLeftBig::class.java),

  /**
   * 使用动画类 [FadeOutRight]
   */
  FADE_OUT_RIGHT(FadeOutRight::class.java),

  /**
   * 使用动画类 [FadeOutRightBig]
   */
  FADE_OUT_RIGHT_BIG(FadeOutRightBig::class.java),

  /**
   * 使用动画类 [FadeOutUp]
   */
  FADE_OUT_UP(FadeOutUp::class.java),

  /**
   * 使用动画类 [FadeOutUpBig]
   */
  FADE_OUT_UP_BIG(FadeOutUpBig::class.java),

  /**
   * 使用动画类 [Flash]
   */
  FLASH(Flash::class.java),

  /**
   * 使用动画类 [Flip]
   */
  FLIP(Flip::class.java),

  /**
   * 使用动画类 [FlipInX]
   */
  FLIP_IN_X(FlipInX::class.java),

  /**
   * 使用动画类 [FlipInY]
   */
  FLIP_IN_Y(FlipInY::class.java),

  /**
   * 使用动画类 [FlipOutX]
   */
  FLIP_OUT_X(FlipOutX::class.java),

  /**
   * 使用动画类 [FlipOutY]
   */
  FLIP_OUT_Y(FlipOutY::class.java),

  /**
   * 使用动画类 [GlowBackground]
   */
  GLOW_BACKGROUND(GlowBackground::class.java),

  /**
   * 使用动画类 [GlowText]
   */
  GLOW_TEXT(GlowText::class.java),

  /**
   * 使用动画类 [Hinge]
   */
  HINGE(Hinge::class.java),

  /**
   * 使用动画类 [JackInTheBox]
   */
  JACK_IN_THE_BOX(JackInTheBox::class.java),

  /**
   * 使用动画类 [Jello]
   */
  JELLO(Jello::class.java),

  /**
   * 使用动画类 [LightSpeedIn]
   */
  LIGHT_SPEED_IN(LightSpeedIn::class.java),

  /**
   * 使用动画类 [LightSpeedOut]
   */
  LIGHT_SPEED_OUT(LightSpeedOut::class.java),

  /**
   * 使用动画类 [Pulse]
   */
  PULSE(Pulse::class.java),

  /**
   * 使用动画类 [RollIn]
   */
  ROLL_IN(RollIn::class.java),

  /**
   * 使用动画类 [RollOut]
   */
  ROLL_OUT(RollOut::class.java),

  /**
   * 使用动画类 [RotateIn]
   */
  ROTATE_IN(RotateIn::class.java),

  /**
   * 使用动画类 [RotateInDownLeft]
   */
  ROTATE_IN_DOWN_LEFT(RotateInDownLeft::class.java),

  /**
   * 使用动画类 [RotateInDownRight]
   */
  ROTATE_IN_DOWN_RIGHT(RotateInDownRight::class.java),

  /**
   * 使用动画类 [RotateInUpLeft]
   */
  ROTATE_IN_UP_LEFT(RotateInUpLeft::class.java),

  /**
   * 使用动画类 [RotateInUpRight]
   */
  ROTATE_IN_UP_RIGHT(RotateInUpRight::class.java),

  /**
   * 使用动画类 [RotateOut]
   */
  ROTATE_OUT(RotateOut::class.java),

  /**
   * 使用动画类 [RotateOutDownLeft]
   */
  ROTATE_OUT_DOWN_LEFT(RotateOutDownLeft::class.java),

  /**
   * 使用动画类 [RotateOutDownRight]
   */
  ROTATE_OUT_DOWN_RIGHT(RotateOutDownRight::class.java),

  /**
   * 使用动画类 [RotateOutUpLeft]
   */
  ROTATE_OUT_UP_LEFT(RotateOutUpLeft::class.java),

  /**
   * 使用动画类 [RotateOutUpRight]
   */
  ROTATE_OUT_UP_RIGHT(RotateOutUpRight::class.java),

  /**
   * 使用动画类 [RubberBand]
   */
  RUBBER_BAND(RubberBand::class.java),

  /**
   * 使用动画类 [Shake]
   */
  SHAKE(Shake::class.java),

  /**
   * 使用动画类 [SlideInDown]
   */
  SLIDE_IN_DOWN(SlideInDown::class.java),

  /**
   * 使用动画类 [SlideInLeft]
   */
  SLIDE_IN_LEFT(SlideInLeft::class.java),

  /**
   * 使用动画类 [SlideInRight]
   */
  SLIDE_IN_RIGHT(SlideInRight::class.java),

  /**
   * 使用动画类 [SlideInUp]
   */
  SLIDE_IN_UP(SlideInUp::class.java),

  /**
   * 使用动画类 [SlideOutDown]
   */
  SLIDE_OUT_DOWN(SlideOutDown::class.java),

  /**
   * 使用动画类 [SlideOutLeft]
   */
  SLIDE_OUT_LEFT(SlideOutLeft::class.java),

  /**
   * 使用动画类 [SlideOutRight]
   */
  SLIDE_OUT_RIGHT(SlideOutRight::class.java),

  /**
   * 使用动画类 [SlideOutUp]
   */
  SLIDE_OUT_UP(SlideOutUp::class.java),

  /**
   * 使用动画类 [Swing]
   */
  SWING(Swing::class.java),

  /**
   * 使用动画类 [Tada]
   */
  TADA(Tada::class.java),

  /**
   * 使用动画类 [Wobble]
   */
  WOBBLE(Wobble::class.java),

  /**
   * 使用动画类 [ZoomIn]
   */
  ZOOM_IN(ZoomIn::class.java),

  /**
   * 使用动画类 [ZoomInDown]
   */
  ZOOM_IN_DOWN(ZoomInDown::class.java),

  /**
   * 使用动画类 [ZoomInLeft]
   */
  ZOOM_IN_LEFT(ZoomInLeft::class.java),

  /**
   * 使用动画类 [ZoomInRight]
   */
  ZOOM_IN_RIGHT(ZoomInRight::class.java),

  /**
   * 使用动画类 [ZoomInUp]
   */
  ZOOM_IN_UP(ZoomInUp::class.java),

  /**
   * 使用动画类 [ZoomOut]
   */
  ZOOM_OUT(ZoomOut::class.java),

  /**
   * 使用动画类 [ZoomOutDown]
   */
  ZOOM_OUT_DOWN(ZoomOutDown::class.java),

  /**
   * 使用动画类 [ZoomOutLeft]
   */
  ZOOM_OUT_LEFT(ZoomOutLeft::class.java),

  /**
   * 使用动画类 [ZoomOutRight]
   */
  ZOOM_OUT_RIGHT(ZoomOutRight::class.java),

  /**
   * 使用动画类 [ZoomOutUp]
   */
  ZOOM_OUT_UP(ZoomOutUp::class.java),
}

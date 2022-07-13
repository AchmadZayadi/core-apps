package com.sesolutions.ui.live.custom_anim

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.sesolutions.utils.CustomLog
import com.squareup.picasso.Picasso
import java.util.*


class ZeroGravityAnimation {

    private var mOriginationDirection = Direction.RANDOM
    private var mDestinationDirection = Direction.RANDOM
    private var mDuration = RANDOM_DURATION
    private var mCount = 1
    private var mImageResId: Int = 0
    private var imageUrl: String = ""
    private var mScalingFactor = 1f
    private var mAnimationListener: Animation.AnimationListener? = null


    /**
     * Sets the orignal direction. The animation will originate from the given direction.
     *
     */
    fun setOriginationDirection(direction: Direction): ZeroGravityAnimation {
        this.mOriginationDirection = direction
        return this
    }

    /**
     * Sets the animation destination direction. The translate animation will proceed towards the given direction.
     * @param direction
     * @return
     */
    fun setDestinationDirection(direction: Direction): ZeroGravityAnimation {
        this.mDestinationDirection = direction
        return this
    }

    /**
     * Will take a random time duriation for the animation
     * @return
     */
    fun setRandomDuration(): ZeroGravityAnimation {
        return setDuration(RANDOM_DURATION)
    }

    /**
     * Sets the time duration in millseconds for animation to proceed.
     * @param duration
     * @return
     */
    fun setDuration(duration: Int): ZeroGravityAnimation {
        this.mDuration = duration
        return this
    }

    /**
     * Sets the image reference id for drawing the image
     * @param resId
     * @return
     */
    fun setImage(resId: Int): ZeroGravityAnimation {
        this.mImageResId = resId
        return this
    }

    fun setImageUrl(path: String): ZeroGravityAnimation {
        this.imageUrl = path
        return this
    }

    /**
     * Sets the image scaling value.
     * @param scale
     * @return
     */
    fun setScalingFactor(scale: Float): ZeroGravityAnimation {
        this.mScalingFactor = scale
        return this
    }

    fun setAnimationListener(listener: Animation.AnimationListener): ZeroGravityAnimation {
        this.mAnimationListener = listener
        return this
    }

    fun setCount(count: Int): ZeroGravityAnimation {
        this.mCount = count
        return this
    }


    /**
     * Starts the Zero gravity animation by creating an OTT and attach it to th given ViewGroup
     * @param activity
     * @param ottParent
     */
    @JvmOverloads
    fun play(activity: Activity, ottParent: ViewGroup? = null) {

        val generator = DirectionGenerator()

        if (mCount > 0) {

            for (i in 0 until mCount) {

                val origin = if (mOriginationDirection === Direction.RANDOM) generator.randomDirection else mOriginationDirection
                val destination = if (mDestinationDirection === Direction.RANDOM) generator.getRandomDirection(origin) else mDestinationDirection

                val startingPoints = generator.getPointsInDirection(activity, origin)
                val endPoints = generator.getPointsInDirection(activity, destination)

                CustomLog.e("imageUrl", imageUrl)
//                val bitmap = BitmapFactory.decodeResource(activity.resources, mImageResId)
                var bitmap: Bitmap? = null
                Picasso.get().load(imageUrl).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap2: Bitmap?, from: Picasso.LoadedFrom?) {
                        // loaded bitmap is here (bitmap)
                         bitmap = bitmap2
                    }
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                })

//                Glide.with(context).asBitmap().load(imageUrl).into(object : SimpleTarget<Bitmap>(){
//                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                                bitmap = resource
//                            }
//                            override fun onLoadCleared(placeholder: Drawable?) {
//                            }
//                        })

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap!!, (bitmap!!.width * mScalingFactor).toInt(), (bitmap!!.height * mScalingFactor).toInt(), false)

                when (origin) {
                    Direction.LEFT -> startingPoints[0] -= scaledBitmap.width

                    Direction.RIGHT -> startingPoints[0] += scaledBitmap.width

                    Direction.TOP -> startingPoints[1] -= scaledBitmap.height
                    Direction.BOTTOM -> startingPoints[1] += scaledBitmap.height
                }

                when (destination) {
                    Direction.LEFT -> endPoints[0] -= scaledBitmap.width

                    Direction.RIGHT -> endPoints[0] += scaledBitmap.width

                    Direction.TOP -> endPoints[1] -= scaledBitmap.height
                    Direction.BOTTOM -> endPoints[1] += scaledBitmap.height
                }


                val layer = OverTheTopLayer()

                val ottLayout = layer.with(activity)
                        .scale(mScalingFactor)
                        .attachTo(ottParent)
                        .setBitmap(scaledBitmap, startingPoints)
                        .create()


                when (origin) {

                }

                val deltaX = endPoints[0] - startingPoints[0]
                val deltaY = endPoints[1] - startingPoints[1]

                var duration = mDuration
                if (duration == RANDOM_DURATION) {
                    duration = generateRandomBetween(3500, 12500)
                }

                val animation = TranslateAnimation(0f, deltaX.toFloat(), 0f, deltaY.toFloat())
                animation.duration = duration.toLong()
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                        if (i == 0) {
                            if (mAnimationListener != null) {
                                mAnimationListener!!.onAnimationStart(animation)
                            }
                        }
                    }

                    override fun onAnimationEnd(animation: Animation) {

                        layer.destroy()

                        if (i == mCount - 1) {
                            if (mAnimationListener != null) {
                                mAnimationListener!!.onAnimationEnd(animation)
                            }
                        }

                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
                layer.applyAnimation(animation)
            }
        } else {

            Log.e(ZeroGravityAnimation::class.java.simpleName, "Count was not provided, animation was not started")
        }
    }

    /**
     * Generates the random between two given integers.
     */
    private fun generateRandomBetween(start: Int, end: Int): Int {

        val random = Random()
        var rand = random.nextInt(Integer.MAX_VALUE - 1) % end

        if (rand < start) {
            rand = start
        }
        return rand
    }

    companion object {

        private val RANDOM_DURATION = -1
    }
}
/**
 * Takes the content view as view parent for laying the animation objects and starts the animation.
 * @param activity - activity on which the zero gravity animation should take place.
 */

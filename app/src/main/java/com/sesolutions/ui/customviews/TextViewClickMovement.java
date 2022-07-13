package com.sesolutions.ui.customviews;

/**
 * Created by AheadSoft on 16-03-2018.
 */

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class TextViewClickMovement extends LinkMovementMethod {

    private final String TAG = TextViewClickMovement.class.getSimpleName();

    private final OnUserClickedListener<Integer, Object> mListener;
    private final GestureDetector mGestureDetector;
    private final int mItemPosition;
    private TextView mWidget;
    private Spannable mBuffer;

    public enum LinkType {

        /**
         * Indicates that phone link was clicked
         */
        PHONE,

        /**
         * Identifies that URL was clicked
         */
        WEB_URL,

        /**
         * Identifies that Email Address was clicked
         */
        EMAIL_ADDRESS,

        /**
         * Indicates that none of above mentioned were clicked
         */
        NONE
    }

    /**
     * Interface used to handle Long clicks on the {@link TextView} and taps
     * on the phone, web, mail links inside of {@link TextView}.
     */
    //  public interface OnTextViewClickMovementListener {

    /**
     * This method will be invoked when user press and hold
     * finger on the {@link TextView}
     *
     * @param linkText Text which contains link on which user presses.
     * @param linkType Type of the link can be one of {@link LinkType} enumeration
     */
    //    void onLinkClicked(final String linkText, final LinkType linkType);

    /**
     * Whole text of {@link TextView}
     */
    //   void onLongClick(final String text);
    //  }
   /* public TextViewClickMovement(final OnTextViewClickMovementListener listener, final Context context) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener());
    }*/
    public TextViewClickMovement(final OnUserClickedListener<Integer, Object> listener, final Context context) {
        mListener = listener;
        mItemPosition = -1;
        mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener());
    }

    public TextViewClickMovement(final OnUserClickedListener<Integer, Object> listener, final Context context, int itemPosition) {
        mListener = listener;
        mItemPosition = itemPosition;
        mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener());
    }


    @Override
    public boolean onTouchEvent(final TextView widget, final Spannable buffer, final MotionEvent event) {

        mWidget = widget;
        mBuffer = buffer;
        mGestureDetector.onTouchEvent(event);

        return false;
    }

    /**
     * Detects various gestures and events.
     * Notify users when a particular motion event has occurred.
     */
    class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            // Notified when a tap occurs.
            return true;
        }

       /* @Override
        public void onLongPress(MotionEvent e) {
            // Notified when a long press occurs.
            final String text = mBuffer.toString();

            if (mListener != null) {
                CustomLog.d(TAG, "----> Long Click Occurs on TextView with ID: " + mWidget.getId() + "\n" +
                        "Text: " + text + "\n<----");

                //mListener.onLongClick(text);
                mListener.onItemClicked(Constant.Events.LINK_CLICKED_LONG, text, 0);
            }
        }*/

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            // Notified when tap occurs.
            final String linkText = getLinkText(mWidget, mBuffer, event);

            LinkType linkType = LinkType.NONE;

            if (Patterns.PHONE.matcher(linkText).matches()) {
                linkType = LinkType.PHONE;
            } else if (Patterns.WEB_URL.matcher(linkText).matches()) {
                linkType = LinkType.WEB_URL;
            } else if (Patterns.EMAIL_ADDRESS.matcher(linkText).matches()) {
                linkType = LinkType.EMAIL_ADDRESS;
            }

            if (mListener != null) {
                CustomLog.d(TAG, "----> Tap Occurs on TextView with ID: " + mWidget.getId() + "\n" +
                        "Link Text: " + linkText + "\n" +
                        "Link Type: " + linkType + "\n<----");

                //  mListener.onLinkClicked(linkText, linkType);
                if (linkType == LinkType.WEB_URL)
                    mListener.onItemClicked(Constant.Events.LINK_CLICKED, linkText, mItemPosition);
                else {
                    mListener.onItemClicked(Constant.Events.LINK_CLICKED_NONE, linkText, mItemPosition);
                }
            }

            return false;
        }

        private String getLinkText(final TextView widget, final Spannable buffer, final MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                return buffer.subSequence(buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0])).toString();
            }

            return "";
        }
    }
}

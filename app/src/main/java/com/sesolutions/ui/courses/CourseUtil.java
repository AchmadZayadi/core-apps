package com.sesolutions.ui.courses;

import androidx.fragment.app.FragmentManager;

import com.sesolutions.R;
import com.sesolutions.ui.courses.cart.CourseCartFragment;
import com.sesolutions.ui.courses.cart.CourseCheckoutFragment;
import com.sesolutions.ui.courses.myaccount.ViewCourseWishlist;
import com.sesolutions.ui.courses.myaccount.ViewCourseOrderFragment;

public class CourseUtil {

    public static void openCourseCartFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new CourseCartFragment())
                .addToBackStack(null)
                .commit();
    }

    public static void openCourseCheckoutFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new CourseCheckoutFragment())
                .addToBackStack(null)
                .commit();
    }
    public static void openViewWishlistFragmnet(FragmentManager fragmentManager,String type, int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCourseWishlist.newInstance(type, id))
                .addToBackStack(null)
                .commit();
    }
    public static void openCourseViewOrderFragment(FragmentManager fragmentManager, int position) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCourseOrderFragment.newInstance(position))
                .addToBackStack(null)
                .commit();

    }
}

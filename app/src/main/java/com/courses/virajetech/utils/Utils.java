package com.courses.virajetech.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import java.lang.reflect.Field;

public class Utils {

    public static final String SHARED_PREF = "menorahoes_firebase";
    public static final String MyPREFERENCES = "my_preferences";

    public static final String PAYPAL_CLIENT_ID = "AZ0c96CbjGnAT8btB4VkCg7R5u3VQeue4QxgiOPTbx5kO-8biNQ0PazuBdKAc7LJFz7gJTwBeM2BTzsg";

    // 🔗 Server URL
    public static final String SERVER_URL = "https://livecourses.co.in";

    public static final String URL = SERVER_URL + "/api/v1/";
    public static final String IMAGE_BASE_URL = SERVER_URL + "/public/uploads/";
    public static final String FILE_BASE_URL = IMAGE_BASE_URL + "lms/content/";
    public static final String USER_PIC_BASE_URL = IMAGE_BASE_URL + "users/";
    public static final String EXAM_SERIES_BASE_URL = IMAGE_BASE_URL + "exams/series/";
    public static final String LMS_SERIES_BASE_URL = IMAGE_BASE_URL + "lms/series/";
    public static final String EXAM_TYPE_BASE_URL = IMAGE_BASE_URL + "exams/";

    // 🔐 Authentication Endpoints
    public static final String LOGIN = URL + "login";
    public static final String REGISTER = URL + "register";
    public static final String CHECK_EMAIL = URL + "users/reset-password";
    public static final String SOCIAL_LOGINS = URL + "users/social-login";

    // 📄 General Pages
    public static final String PRIVACY_POLICY = URL + "pages/privacy-policy";
    public static final String TERMS_CONDITIONS = URL + "pages/terms-conditions";

    // 📦 LMS/Exam Data APIs
    public static final String POPULAR_RECORDS = URL + "dashboard-top-records";
    public static final String EXAMS_CATEGORIES_LIST = URL + "exam-categories?user_id=";
    public static final String LMS_CATEGORIES_LIST = URL + "lms-categories?user_id=";
    public static final String GET_CATEGORY_EXAM_LIST = URL + "exams/";
    public static final String GET_CATEGORY_LMS_LIST = URL + "lms/";
    public static final String GET_LMS_SERIES_LIST = URL + "lms/series/";
    public static final String LMS_SERIES = URL + "lms-series?user_id=";
    public static final String EXAMS_SERIES = URL + "exam-series?user_id=";
    public static final String GET_EXAM_SERIES_LIST = URL + "exams/student-exam-series/";
    public static final String GET_EXAMS_HISTORY = URL + "analysis/history/";

    // 🧾 User Profile & Settings
    public static final String GET_MY_PROFILE = URL + "user/profile/";
    public static final String GET_SETTINGS = URL + "user/settings/";
    public static final String UPDATE_PROFILE = URL + "users/edit/";
    public static final String CHANGE_PASSWORD = URL + "user/update-password";
    public static final String UPDATE_SETTINGS = URL + "update/user-sttings/";
    public static final String UPLOAD_PROFILE_PIC = URL + "profile-image";

    // 📊 Exam Related
    public static final String START_EXAM = URL + "get-exam-questions/";
    public static final String EXAM_INSTUCTIONS = URL + "instructions/";
    public static final String GET_RESULT = URL + "finish-exam/";
    public static final String GET_EXAM_KEY = URL + "get-exam-key/";

    // 📈 Analytics & Bookmarks
    public static final String ANALYSIS_BY_SUBJECT = URL + "analysis/subject/";
    public static final String ANALYSIS_BY_EXAM = URL + "analysis/exam/";
    public static final String BOOKMARKS = URL + "user/bookmarks/";
    public static final String BOOK_MARK_SAVE_REMOVE = URL + "bookmarks/save";
    public static final String DELETE_BOOK_MARK = URL + "bookmarks/delete/";

    // 💬 Feedback & Notifications
    public static final String ADD_FEED_BACK = URL + "feedback/send";
    public static final String NOTIFICATIONS = URL + "notifications";

    // 🛒 Subscriptions & Payments
    public static final String SUBSCRIPTIONS = URL + "user/subscriptions/";
    public static final String APPLY_COUPON_CODE = URL + "validate/coupon";
    public static final String SAVE_TRANSACTION_STATUS = URL + "save-transaction";
    public static final String OFFLINE_PAYMENT = URL + "update-offline-payment";
    public static final String GET_CURRENCY_CODE = URL + "get-currency-code";
    public static final String GET_PAYMET_GATEWAYS = URL + "get-payment-gateways";

    // 🆎 Font override (custom font)
    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ⏳ ProgressBar Overlay
    private static ProgressBar m_Dialog;

    public static ProgressBar showProgressDialog(Context context, String message) {
        if (context instanceof Activity) {
            try {
                Activity activity = (Activity) context;
                if (m_Dialog == null) {
                    m_Dialog = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
                    m_Dialog.setIndeterminate(true);

                    RelativeLayout rl = new RelativeLayout(context);
                    rl.setGravity(Gravity.CENTER);
                    rl.addView(m_Dialog);

                    ViewGroup layout = activity.findViewById(android.R.id.content);
                    layout.addView(rl, new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                }
                m_Dialog.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return m_Dialog;
    }

    public static boolean showProgress(Context context) {
        if (m_Dialog != null) {
            m_Dialog.setVisibility(View.VISIBLE);
            return true;
        } else {
            showProgressDialog(context, "");
            return true;
        }
    }

    public static void dissmisProgress() {
        if (m_Dialog != null) {
            m_Dialog.setVisibility(View.GONE);
        }
    }

    // 🌐 Internet Availability Check
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    return capabilities != null && (
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                } else {
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    return activeNetwork != null && activeNetwork.isConnected();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

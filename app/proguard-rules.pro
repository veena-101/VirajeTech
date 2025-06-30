# Preserve line number and source file names
-keepattributes SourceFile,LineNumberTable

# Application-level components
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.fragment.app.DialogFragment

# If using legacy fragments
-keep public class * extends android.app.Fragment

# Serializable support
-keepclassmembers class * implements java.io.Serializable {
    static ** CREATOR;
}

# MathJax WebView custom class
#noinspection ShrinkerUnresolvedReference
-keepclassmembers class com.courses.virajetech.MathJaxWebView {
    public *;
}

# Razorpay SDK rules
-dontwarn com.razorpay.**
-keep class com.razorpay.** { *; }

# Firebase and Google Play Services
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Facebook SDK (if used)
-keep class com.facebook.** { *; }
-dontwarn com.facebook.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# JSoup
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# ViewBinding & Reflection
-keep class androidx.databinding.** { *; }
-keep class androidx.viewbinding.** { *; }
-dontwarn androidx.viewbinding.**

# AdMob / Play Services Ads
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# General AndroidX support
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Optional: Hide the original source file names
# -renamesourcefileattribute SourceFile

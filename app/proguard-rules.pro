# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class primaryName to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-dontobfuscate
-keepattributes SourceFile,LineNumberTable
-keep class kotlin.reflect.jvm.internal.** { *; }

# If you keep the line number information, uncomment this to
# hide the original source file primaryName.
-renamesourcefileattribute SourceFile

#-keepattributes *Annotation*,InnerClasses,EnclosingMethod

-keep class kotlin.Metadata { *; }
#-keep class com.chibatching.kotpref.** {
#  public protected private *;
#}

-keepclassmembers enum * { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class se.oskarh.boardgamehub.api.**
-keep class se.oskarh.boardgamehub.db.boardgame.BoardGame
-keep class se.oskarh.boardgamehub.util.AppPreferences

-dontwarn okio.**

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
-keepattributes Exceptions
-keep class android.content.**

-keep class com.tickaroo.tikxml.** { *; }
-keep @com.tickaroo.tikxml.annotation.Xml public class *
-keep class **$$TypeAdapter { *; }

-keepclasseswithmembernames class * {
    @com.tickaroo.tikxml.* <fields>;
}

-keepclasseswithmembernames class * {
    @com.tickaroo.tikxml.* <methods>;
}
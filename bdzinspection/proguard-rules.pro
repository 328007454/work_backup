# 保持native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保持Parcelable不被混淆
-keep class * implements Android.os.Parcelable {
    public static final Android.os.Parcelable$Creator *;
}

#枚举类不混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * extends java.lang.annotation.Annotation { *; }
-keepclassmembers class * extends android.support.v4.app.Fragment {
  public *;
}
-keep class com.cnksi.bdzinspection.model.**{*;}


#内部类不混淆
#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.bdzinspection.adapter.MultipleBackAdapter$*{*;}
#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.bdzinspection.adapter.SignNameAdapter$*{*;}
#-keepattributes Exceptions,InnerClasses,...
#-keep class com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter$*{*;}
#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.bdzinspection.adapter.defectcontrol.DefectContentAdapter$*{*;}
-keepattributes Exceptions,InnerClasses,...
-keep  class com.cnksi.bdzinspection.adapter.**$*{*;}




#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.bdzinspection.activity.GenerateReportActivity$*{*;}
#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.bdzinspection.activity.AddTaskActivity*{*;}
#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.common.base.BaseActivity$*{*;}
#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.bdzinspection.activity.ImageDetailsActivity$*{*;}
#-keepattributes Exceptions,InnerClasses,...
#-keep  class com.cnksi.bdzinspection.activity.NewDeviceDetailsActivity$*{*;}

-keepattributes Exceptions,InnerClasses,...
-keep  class com.cnksi.bdzinspection.activity.**$*{*;}

-keepattributes Exceptions,InnerClasses,...
-keep  class com.cnksi.bdzinspection.fragment.defectcontrol.EliminateDefectFragment$*{*;}



-keepattributes Exceptions,InnerClasses,...
-keep  class com.cnksi.bdzinspection.utils.DialogUtils*{*;}

-dontwarn com.cnksi.tts.**
-keep class com.cnksi.tts.**{*;}
-keep class com.cnksi.tts.ISpeakCallback{*;}
-keep class com.cnksi.tts.ISpeakInterface{*;}

-dontwarn com.cnksi.bdloc.**
-keep class com.cnksi.bdloc.**{*;}

-dontwarn com.cnksi.ksynclib.**
-keep class com.cnksi.ksynclib.**{*;}


-ignorewarnings
-keep class javax.ws.rs.** { *; }
-keep class org.spring.**{*;}
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-dontwarn okio.**
-keep class okio.**{*;}

-keep class com.github.mikephil.charting.**{*;}
-dontwarn io.realm.**

-dontwarn junit.**

-dontwarn org.xutils.**
-keep class org.xutils.**{*;}
-keep class net.sqlcipher.**{*;}

-dontwarn java.lang.management.**



-libraryjars ../bdzinspection/libs/MPChartLib.jar
-dontwarn org.dom4j.**
-libraryjars ../bdzinspection/libs/libs/dom4j-2.0.0.jar
-keep class org.dom4j.**{*;}
-libraryjars ../bdzinspection/libs/libs/M100_RFID_API.jar
-libraryjars ../bdzinspection/libs/libs/httpmime-4.1.3.jar

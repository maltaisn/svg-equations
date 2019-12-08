-dontobfuscate

# JCommander
-keep class com.beust.jcommander.** { *; }

# Enums
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Entry points
-keep public class com.maltaisn.svgequations.MainKt {
    public static void main(java.lang.String[]);
}
-keepclassmembers class com.maltaisn.svgequations.Parameters { *; }

# Firebase
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * { @com.google.firebase.firestore.PropertyName <fields>; }
-keep class com.kaushalya.karnataka.data.**.dto.** { *; }

# Kotlinx serialization
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keep,includedescriptorclasses class com.kaushalya.karnataka.**$$serializer { *; }
-keepclassmembers class com.kaushalya.karnataka.** {
    *** Companion;
}
-keepclasseswithmembers class com.kaushalya.karnataka.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }

# Coil
-keep class coil3.** { *; }

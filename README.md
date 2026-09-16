# FamilyTracker
دو اپ اندرویدی:
- «مار بازی» — `com.familytracker.child`
- «موقعیت مکانی» — `com.familytracker.parent`

این نسخه برای اشتراک‌گذاری موقعیت با رضایت و مجوز صریح Android طراحی شده است؛
ردیابی مخفی، دور زدن مجوزها یا جلوگیری از حذف برنامه ندارد.

Firebase:
- `child/google-services.json`
- `parent/google-services.json`

این دو فایل در `.gitignore` هستند و نباید در Repository عمومی قرار بگیرند.

Build:
GitHub Actions workflow با نام `Android APK Build` دو APK debug می‌سازد.

قابلیت‌های نسخه پایه:
- توکن ۵ رقمی
- Firebase Anonymous Auth + Firestore
- GPS با foreground service در اپ فرزند
- بازی Snake
- نقشه OpenStreetMap
- ثبت خانه با شعاع پیش‌فرض ۱۰ متر
- نمایش موقعیت در اپ والد
- هشدار داخل اپ والد هنگام خروج از محدوده

اعلان Push کاملاً پس‌زمینه‌ای برای تولید به backend امن نیاز دارد؛ credential سرور عمداً داخل پروژه قرار داده نشده است.

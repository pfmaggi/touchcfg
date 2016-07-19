# Zebra's Mx - Touch Manager sample
Sample application showing how to configure the touch controller on Zebra's Android devices.

There're two options:

    - Stylus and Finger
    - Gloves and Finger

You can find more information about this profile on [Zebra's Techdocs](http://techdocs.zebra.com/emdk-for-android/4-2/mx/touchmgr/).

Pay attention that setting the touch mode, it's a configuration change, and your activity is going to be destroyed and recreated (I've added some logging to the Activity's lifecycle callbacks). 


Note: This project does not includes the EMDK jar library, you will need to download that from [Zebra's support website](https://portal.motorolasolutions.com/Support/US-EN/Search?searchType=simple&searchTerm=EMDK%20Android) accepting the license agreement. Once you've the library, you can modify the gradle file to point at the copy you've on your PC.  
In my case I've the EMDK v4.2 library installed in `/Applications/androidSDK/add-ons/addon-symbol-emdk_v4.2-API-16/libs/`.

Happy coding  
~Pietro
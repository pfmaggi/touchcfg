# Zebra's Mx - Touch Manager sample
Sample application showing how to configure the touch controller on Zebra's Android devices.

There're two options:

    - Stylus and Finger
    - Gloves and Finger

You can find more information about this profile on [Zebra's Techdocs](http://techdocs.zebra.com/emdk-for-android/4-2/mx/touchmgr/).

Pay attention that setting the touch mode, it's a configuration change, and your activity is going to be destroyed and recreated (I've added some logging to the Activity's lifecycle callbacks). 

~Pietro
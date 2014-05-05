# Behavioral Biometric Logger

See [http://vmonaco.com/biometrics/](http://vmonaco.com/biometrics/)

## About

This application is designed to capture the user input to a computer over the course of a session. 

Instructions:
1. If you have already done so, create an account. Your email address will not be released or spammed in any way. If you forget your password, it can be used to log back into your account. A valid email address is required since an activation link will be sent there after you create an account.

2. Log in to your account and go to the Logger. The application can be launched from this page.

3. Select the task that you will perform and launch the logger. 

5. Perform the task. During this time, the logger will record your input to the computer *system wide*. For this reason, do *not* log into any other accounts which require inputting your credentials. If you do and would like that information removed, notify the admin and it will be removed from the database.

6. Once the task is complete, exit the application. At this point, any data still in the buffer will be uploaded to the server. This may take several minutes, please be patient and let the upload finish. The application will stop recording event *as soon* as you click exit.


Data collected:

keystroke events
    press time: 'Key press timestamp'
    release time: 'Key release timestamp'
    key code: 'The key code'
    key string: 'The symbolic key entered'
    modifier code: 'Modifier code during the event'
    modifier string: 'Modifier string during the event'
    key location: 'The key location'

stylometry events
    start time: 'Time the segment began'
    end time: 'Time the segment ended'
    text: 'Text entered'

motion events
    time: 'Timestamp of the event'
    x: 'X location of the pointer device'
    y: 'Y location of the pointer device'
    modifier code: 'Modifier code during the event'
    modifier string: 'Modifier string during the event'
    dragged: 'Was the mouse dragged as opposed to just moved?'

click events
    press time: 'Pointer click press timestamp'
    release time: 'Pointer release timestamp'
    button code: 'Pointer button'
    press x: 'Start X location of the pointer device'
    press y: 'Start Y location of the pointer device'
    release x: 'End X location of the pointer device'
    release y: 'End Y location of the pointer device'
    modifier code: 'Modifier code during the event'
    modifier string: 'Modifier string during the event'
    image: 'Region in which the event took place'

scroll events
    time: 'Timestamp of the event'
    amount: 'Amount of scroll'
    rotation: 'Scroll direction (either +1 or -1)'
    type: 'Scroll type (0 for unit 1 for block)'
    x: 'X location of the pointer device'
    y: 'Y location of the pointer device'
    modifier code: 'Modifier code during the event'
    modifier string: 'Modifier string during the event'

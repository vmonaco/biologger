# Behavioral Biometric Logger

## About

This application is designed to capture the user input to a computer over the course of a session. It captures events system wide by registering system wide hooks. It is designed to be web-launched from a browser with Java web start.

Data collected:


```
#!text

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
```

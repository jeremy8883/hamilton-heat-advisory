# Hamilton Heat Alert

Android application for fetching the Hamilton Ontario heat advisory warnings and displaying a notification when the alert value is above 0.

[More info on heat alerts](http://www.hamilton.ca/public-health/health-topics/heat-alerts-heat-related-illness?WT.mc_id=heat&WT.hamilton_redirect_friendly=1)

## Directory Structure

There are three main folders:

- app - the android application that receives the heat alerts and notifies the user of them
- server - the server that polls the hamilton heat alert rss feed and sends a GCM message when the warning is above 0
- shared - common classes between the app and server

## Google Cloud Messaging

We use GCM for broadcasting alerts to all devices. Contact [Jeremy Casey](jeremy8883@gmail.com) for access to the GCM account. To run the server, you'll also need to create a `gcm-settings.json` file in the `server` folder.

```
{
  "apiKey": "API_KEY_HERE"
}
```


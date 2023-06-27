# App Services Usage Samples

Reference app that show cases different design patterns and examples of using the Realm Kotlin SDK with Atlas App Services.

## Samples

### [Property level encryption](apps/property-encryption/README.md)

This demo shows the process of protecting users' sensitive data by employing end-to-end encryption techniques while guaranteeing access from any user's device.

## Demo app structure

The project has been structured in two main folders:

- Demo - Android app containing the different samples. Samples have been separated in different packages.
- Apps - Atlas App services apps required by each sample.

## Getting started

The demos are indendepent of each other, this means that it is not required to install all the app services app samples to test an individual sample.

To begin, locate the App services app sources that you wish to install. We have conveniently linked them in the Samples list of this document.

Next, follow the steps outlined in the [Atlas documentation](https://www.mongodb.com/docs/atlas/app-services/apps/create/) to setup the apps. These docs will guide you through the process and help troubleshoot any issue you might encounter.

After deploying the Atlas apps, you will need to update [Constants.kt](demo/src/main/java/io/realm/appservicesusagesamples/Constants.kt) with the newly created app ids.

Once you have completed these steps, you would be able to run the samples using the Kotlin demo app.
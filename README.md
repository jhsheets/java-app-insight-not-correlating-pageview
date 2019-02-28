# java-app-insight-not-correlating-pageview
Java SpingBoot app with ApplicationInsight starter project that doesn't correlate pageview to other telemetry

This example is to show that correlation between browser-generated telemetry and server-generated telemetry either doesn't work, or there doesn't seem to be enough information for me to understand what I'm doing wrong.

To test:
- Modify the application.yml file
  - Enter a valid Application Insights instrumentation key
  - Enter a valid Azure Blob Storage account information
- Run the application
  - Open a web browser to http://localhost/html/welcome
  - Click on the File Manager link

Now go to your Application Insights and look at the telemetry that's uploaded.

Various REQUEST, TRACE, DEPENDENCY and PAGEVIEW telemetry will get uploaded. Each time you go to File Manager, or perform an action on that page, it will generate at least one of each of these telemetry types.

The REQUEST, TRACE and DEPENDENCY telemetries will all be correlated, and show as being triggerd from the same operation. However, the PAGEVIEW - opening the HTML page which triggered the server request and generated all the other telemetries - is not correlated, and there's no way to tell that it's tied to the other events.

The \_header.html includes the application insights javascript file, which should send the necessary information back the server so it can correlate the browser-generated telemetry with the server-generated telemetry.

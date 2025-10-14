## Table of Contents
- [Project Tracking Documentation](#project-tracking)
- [Deployment Guide](#deployment)
- [Opa Testing Guide](#opa-testing)

# Project Tracking
This is the documentation for the Project Tracking project.


**Custom (non-Java/CAP specific) components used:**
  - EventContextAnalyzer (srv\src\main\java\com\sap\internal\digitallab\doctracking\utilities\EventContextAnalyzer.java)

## Deployment

[See the full deployment guide here](https://pages.github.tools.sap/cap/docs/guides/deployment/to-cf?impl-variant=java)

1. Ugrade packages to the latest versions

    ```sh
    npm -g outdated       #> check whether @sap/cds-dk is listed
    npm i -g @sap/cds-dk  #> if necessary
    ```

2. Install mbt globally

    ```sh
    npm i -g mbt
    ```

    [On Windows install GNU Make](https://sap.github.io/cloud-mta-build-tool/makefile/)

3. Install Cloud Foundry plugins

    ```sh
    cf add-plugin-repo CF-Community https://plugins.cloudfoundry.org
    cf install-plugin -f multiapps
    cf install-plugin -f html5-plugin
    ```

4. Add the required services

    ```sh
    cds add hana
    cds add xsuaa
    cds add mta
    cds add approuter
    cds add portal
    ```

5. Watch out for
   - In CommonDataModel.json the appId should be the same as 'name' property in ui5.deploy.yaml
   - In the xs-app files make sure the requests made to the odata service get rerouted properly

        ```json
        {
        "source": "^/odata/v4/(.*)$",
        "target": "/odata/v4/$1",
        "destination": "srv-api",
        "authenticationType": "xsuaa",
        "csrfProtection": true
        },
        ```

   - In CommonDataModel.json the vizId should be the same as in the webapp/manifest.json file, under the crossNavigation/inbounds property. Make sure the semanticObject and action is also correct

6. Login to Cloud Foundry

     ```sh
    cf login -a https://api.cf.il30.hana.ondemand.com/ --sso  
                # change the address accordingly
    ```

    In BTP you can find it here:

    ![API Endpoint](/docs/api_endpoint.png)

7. Deploy

    ```sh
    cds up
    ```

8. User Roles & Collections

    In BTP under Security > Role Collections

    ![User Collections](/docs/user_collections.png)

    Create a new collection & Add necessary Roles and Users to access the deployed app


## OPA Testing: 
### Overview - UI5 OPA testing using Odata V4 protocol
Integration tests in SAP Fiori applications are conducted using OPA5, a tool integrated with SAPUI5 designed for one-page acceptance tests. OPA5 runs in the same browser window as the application to be tested, making it suitable for single-page web applications.

### Integration Test Setup
All integration tests are located in the webapp/test/integration folder. Tests are initiated by calling the opaTests.qunit.html file in the same folder. This HTML page is a QUnit runner that triggers all integration tests and displays the results.

### Writing Integration Tests with OPA5
OPA5 tests are structured into journeys, which encapsulate test cases for specific views or use cases. These journeys use page objects that encapsulate actions and assertions needed to describe the user interactions with the app.

#### Prerequisites

Ensure that the following scripts are available in your `package.json`:

```json
"scripts": {
"start": "ui5 serve --config ./ui5-test.yaml",
"testOpa": "ui5 serve --config ./ui5-test.yaml --open test/integration/opaTests.qunit.html"
}
```
This script allows us to serve the application using the `ui5.yaml` configuration when we run `npm start`.

Check the dependencies for the mock server and UI5 CLI tools, and make sure they are up to date:

```json
    "devDependencies": {
        "@sap-ux/ui5-middleware-fe-mockserver": "^2.2.95",
        "@sap/ui5-builder-webide-extension": "^1.1.8",
        "@ui5/cli": "^2.14.10",
        "mbt": "^1.2.18",
        "ui5-middleware-livereload": "^3.1.1",
        "ui5-middleware-simpleproxy": "^3.4.1",
        "ui5-task-zipper": "^0.5.0"
    },
    "ui5": {
        "dependencies": [
            "@sap/ui5-builder-webide-extension",
            "ui5-task-zipper",
            "mbt",
            "@sap-ux/ui5-middleware-fe-mockserver"
        ]
    }
```

If any of these dependencies are missing, install them using npm, for example:

```bash
npm i -D @sap-ux/ui5-middleware-fe-mockserver
```

These dependencies are essential for building, serving, and mocking the Fiori application.

### Setting Up the Mock Server

We will make our application independent from the backend by mocking the services. A complete mock server setup requires three main components:
- Mock Data
- "Hardcoded" Metadata
- Configuration of the mock server in the `ui5-test.yaml` file

#### Configuring the `ui5-test.yaml` File

Add the following configuration to your `ui5-test.yaml`:

```yaml
server:
  customMiddleware:
    - name: sap-fe-mockserver
      beforeMiddleware: compression
      configuration:
        service:
          urlPath: "/odata/v4/DocService/"
          metadataPath: "./webapp/localService/metadata.xml"
          mockdataPath: "./webapp/localService/data"
          generateMockData: false

```

- The `watch` property ensures the app automatically refreshes, avoiding the need to restart the mock server for updates.
- The `urlPath` can be found in the `manifest.json` of the app under the "main service".
- Create mock data within the `localService` directory. The directory name should match the declared entity set in the service (check `manifest.json` under `contextPath`).

#### Creating Metadata

To make the mock server work independently:
1. Run the application (`mvn spring-boot:run`).
2. Open the application in the browser and inspect the network requests to access the actual metadata.
3. Copy the metadata and save it as `metadata.xml` under the `localService` directory.

#### Alternative mode for Metadata

Alternatively the `metadataXmlPath` line can be changed to the following:

```yaml
server:
  customMiddleware:
    - name: sap-fe-mockserver
      beforeMiddleware: compression
      configuration:
        service:
          urlPath: "/odata/v4/DocService/"
          metadataPath: "../../srv/src/main/resources/edmx/odata/v4/com.sap.internal.digitallab.doctracking.service.DocService.xml"
          mockdataPath: "./webapp/localService/data"
          generateMockData: false
```

This way, the mockserver will use the generated metadata from the cds.

The pros of this, is that the mockserver will use the same structure as the production/local server, so each time something is changed in the code, and the cds is complied, the tests will run on the new structure. The metadata doesn't have to be manually saved.

#### Example Directory Structure

```plaintext
/webapp
/localService
metadata.xml
/data
users.json
```

### Running the Application with Mock Server

Run the following command to start your application with the mock server:

```bash
npm start
```
To start the OPA tests, run this command which will open the localhost/index.html page in the default browser

```bash
npm run testOpa
```